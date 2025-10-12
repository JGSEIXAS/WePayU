package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Serviço responsável por gerenciar a lógica de negócio de lançamentos no sistema,
 * como cartões de ponto, resultados de vendas e taxas de serviço.
 * Utiliza o Command Pattern para suportar a funcionalidade de undo/redo.
 */
public class LancamentoService extends BaseService {
    private final CommandHistoryService commandHistoryService;

    /**
     * Constrói uma instância de LancamentoService com as dependências necessárias.
     * @param repository O repositório para acesso aos dados dos empregados.
     * @param commandHistoryService O serviço de histórico de comandos para undo/redo.
     */
    public LancamentoService(EmpregadoRepository repository, CommandHistoryService commandHistoryService) {
        super(repository);
        this.commandHistoryService = commandHistoryService;
    }

    /**
     * Lança um cartão de ponto para um empregado horista.
     * Define a data de contratação se for o primeiro lançamento.
     * @param id O ID do empregado horista.
     * @param data A data do registro de ponto (formato "d/M/yyyy").
     * @param horasStr As horas trabalhadas no dia.
     * @throws ValidacaoException Se os dados de entrada forem inválidos (data, horas).
     * @throws EmpregadoNaoExisteException Se o empregado não for encontrado ou não for horista.
     */
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

    /**
     * Lança um resultado de venda para um empregado comissionado.
     * @param id O ID do empregado comissionado.
     * @param data A data da venda (formato "d/M/yyyy").
     * @param valorStr O valor da venda.
     * @throws ValidacaoException Se os dados de entrada forem inválidos (data, valor).
     * @throws EmpregadoNaoExisteException Se o empregado não for encontrado ou não for comissionado.
     */
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

    /**
     * Lança uma taxa de serviço para um membro do sindicato.
     * @param idMembro O ID de membro do sindicato.
     * @param data A data da cobrança da taxa (formato "d/M/yyyy").
     * @param valorStr O valor da taxa de serviço.
     * @throws ValidacaoException Se os dados de entrada forem inválidos.
     * @throws EmpregadoNaoExisteException Se nenhum empregado corresponder ao ID de membro do sindicato.
     */
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

    /**
     * Valida se a string de horas é um número positivo.
     * @param horasStr As horas em formato de String.
     * @return O valor das horas em double.
     * @throws ValidacaoException Se as horas não forem um número ou não forem positivas.
     */
    private double validarHoras(String horasStr) throws ValidacaoException {
        try {
            double horas = Double.parseDouble(horasStr.replace(',', '.'));
            if (horas <= 0) throw new HorasPositivasException();
            return horas;
        } catch (NumberFormatException e) {
            throw new HorasNumericasException();
        }
    }

    /**
     * Valida se a string de valor é um número positivo.
     * @param valorStr O valor em formato de String.
     * @return O valor em double.
     * @throws ValidacaoException Se o valor não for um número ou não for positivo.
     */
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