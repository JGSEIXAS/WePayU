package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LancamentoService extends br.ufal.ic.p2.wepayu.Services.BaseService {

    public LancamentoService(EmpregadoRepository repository) {
        super(repository);
    }

    public void lancaCartao(String id, String data, String horasStr) throws ValidacaoException, EmpregadoNaoExisteException {
        Empregado empregado = getEmpregadoValido(id, EmpregadoHorista.class);
        if (!isDataValida(data)) throw new DataInvalidaException();
        double horas = validarHoras(horasStr);

        EmpregadoHorista horista = (EmpregadoHorista) empregado;

        // Define a data de contratação no primeiro cartão
        if (horista.getDataContratacao() == null) {
            LocalDate dataContratacao = LocalDate.parse(data, DateTimeFormatter.ofPattern("d/M/yyyy"));
            horista.setDataContratacao(dataContratacao);
            horista.setDataUltimoPagamento(dataContratacao.minusDays(1));
        }

        CartaoDePonto novoCartao = new CartaoDePonto(data, horas);
        horista.lancaCartao(novoCartao);
    }

    public void lancaVenda(String id, String data, String valorStr) throws ValidacaoException, EmpregadoNaoExisteException {
        Empregado empregado = getEmpregadoValido(id, EmpregadoComissionado.class);
        if (!isDataValida(data)) throw new DataInvalidaException();
        double valor = validarValorPositivo(valorStr);
        ResultadoVenda novaVenda = new ResultadoVenda(data, valor);
        ((EmpregadoComissionado) empregado).lancaVenda(novaVenda);
    }

    public void lancaTaxaServico(String idMembro, String data, String valorStr) throws ValidacaoException, EmpregadoNaoExisteException {
        if (idMembro == null || idMembro.isEmpty()) throw new MembroNuloException();
        if (!isDataValida(data)) throw new DataInvalidaException();
        double valor = validarValorPositivo(valorStr);

        Empregado empregadoAlvo = null;
        for (Empregado e : repository.findAll()) {
            if (e.isSindicalizado() && e.getMembroSindicato().getIdMembro().equals(idMembro)) {
                empregadoAlvo = e;
                break;
            }
        }
        if (empregadoAlvo == null) throw new MembroNaoExisteException();

        TaxaServico novaTaxa = new TaxaServico(data, valor);
        empregadoAlvo.getMembroSindicato().lancaTaxaServico(novaTaxa);
    }

    private double validarHoras(String horasStr) throws ValidacaoException {
        try {
            double horas = Double.parseDouble(horasStr.replace(',', '.'));
            if (horas <= 0) throw new HorasPositivasException();
            return horas;
        } catch (NumberFormatException e) {
            throw new ValidacaoException("Horas deve ser um valor numerico.");
        }
    }

    private double validarValorPositivo(String valorStr) throws ValidacaoException {
        try {
            double valor = Double.parseDouble(valorStr.replace(',', '.'));
            if (valor <= 0) throw new ValorPositivoException();
            return valor;
        } catch (NumberFormatException e) {
            throw new ValidacaoException("Valor deve ser numerico.");
        }
    }
}