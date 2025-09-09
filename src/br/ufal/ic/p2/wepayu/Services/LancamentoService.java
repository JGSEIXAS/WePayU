package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class LancamentoService extends BaseService {
    private final CommandHistoryService commandHistoryService;

    public LancamentoService(EmpregadoRepository repository, CommandHistoryService commandHistoryService) {
        super(repository);
        this.commandHistoryService = commandHistoryService;
    }

    public void lancaCartao(String id, String data, String horasStr) throws ValidacaoException, EmpregadoNaoExisteException {
        getEmpregadoValido(id, EmpregadoHorista.class);
        Map.Entry<Map<String, Empregado>, Integer> estadoAnterior = repository.getState();
        Runnable undoAction = () -> repository.setState(estadoAnterior);

        Runnable commandAction = () -> {
            try {
                EmpregadoHorista original = (EmpregadoHorista) repository.findById(id);
                EmpregadoHorista modificado = (EmpregadoHorista) original.clone();
                if (!isDataValida(data)) throw new DataInvalidaException();
                double horas = validarHoras(horasStr);
                if (modificado.getDataContratacao() == null) {
                    LocalDate dataContratacao = LocalDate.parse(data, DateTimeFormatter.ofPattern("d/M/yyyy"));
                    modificado.setDataContratacao(dataContratacao);
                    modificado.setDataUltimoPagamento(dataContratacao.minusDays(1));
                }
                CartaoDePonto novoCartao = new CartaoDePonto(data, horas);
                modificado.lancaCartao(novoCartao);
                repository.save(modificado);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        commandHistoryService.execute(commandAction, undoAction);
    }

    public void lancaVenda(String id, String data, String valorStr) throws ValidacaoException, EmpregadoNaoExisteException {
        getEmpregadoValido(id, EmpregadoComissionado.class);
        Map.Entry<Map<String, Empregado>, Integer> estadoAnterior = repository.getState();
        Runnable undoAction = () -> repository.setState(estadoAnterior);

        Runnable commandAction = () -> {
            try {
                EmpregadoComissionado original = (EmpregadoComissionado) repository.findById(id);
                EmpregadoComissionado modificado = (EmpregadoComissionado) original.clone();
                if (!isDataValida(data)) throw new DataInvalidaException();
                double valor = validarValorPositivo(valorStr);
                ResultadoVenda novaVenda = new ResultadoVenda(data, valor);
                modificado.lancaVenda(novaVenda);
                repository.save(modificado);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        commandHistoryService.execute(commandAction, undoAction);
    }

    public void lancaTaxaServico(String idMembro, String data, String valorStr) throws ValidacaoException, EmpregadoNaoExisteException {
        if (idMembro == null || idMembro.isEmpty()) throw new MembroNuloException();
        Map.Entry<Map<String, Empregado>, Integer> estadoAnterior = repository.getState();
        Runnable undoAction = () -> repository.setState(estadoAnterior);

        Runnable commandAction = () -> {
            try {
                Empregado empregadoAlvo = null;
                for (Empregado e : repository.findAll()) {
                    if (e.isSindicalizado() && e.getMembroSindicato().getIdMembro().equals(idMembro)) {
                        empregadoAlvo = e;
                        break;
                    }
                }
                if (empregadoAlvo == null) throw new MembroNaoExisteException();
                Empregado modificado = empregadoAlvo.clone();
                if (!isDataValida(data)) throw new DataInvalidaException();
                double valor = validarValorPositivo(valorStr);
                TaxaServico novaTaxa = new TaxaServico(data, valor);
                modificado.getMembroSindicato().lancaTaxaServico(novaTaxa);
                repository.save(modificado);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        commandHistoryService.execute(commandAction, undoAction);
    }

    private double validarHoras(String horasStr) throws ValidacaoException {
        try {
            double horas = Double.parseDouble(horasStr.replace(',', '.'));
            if (horas <= 0) throw new HorasPositivasException();
            return horas;
        } catch (NumberFormatException e) {
            throw new HorasNumericasException();
        }
    }

    private double validarValorPositivo(String valorStr) throws ValidacaoException {
        try {
            double valor = Double.parseDouble(valorStr.replace(',', '.'));
            if (valor <= 0) throw new ValorPositivoException();
            return valor;
        } catch (NumberFormatException e) {
            throw new ValorNumericoException();
        }
    }
}