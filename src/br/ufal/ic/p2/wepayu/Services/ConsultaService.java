package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class ConsultaService extends BaseService {

    public ConsultaService(EmpregadoRepository repository) {
        super(repository);
    }

    public String getHorasNormaisTrabalhadas(String id, String dataInicialStr, String dataFinalStr) throws ValidacaoException, EmpregadoNaoExisteException {
        validarIntervaloDeDatas(dataInicialStr, dataFinalStr);
        EmpregadoHorista horista = (EmpregadoHorista) getEmpregadoValido(id, EmpregadoHorista.class);

        double horasNormaisTotal = 0.0;
        for (CartaoDePonto cartao : horista.getCartoesDePonto().values()) {
            if (isDataNoIntervalo(cartao.getData(), dataInicialStr, dataFinalStr)) {
                horasNormaisTotal += Math.min(cartao.getHoras(), 8);
            }
        }
        return formatarResultadoNumerico(horasNormaisTotal);
    }

    public String getHorasExtrasTrabalhadas(String id, String dataInicialStr, String dataFinalStr) throws ValidacaoException, EmpregadoNaoExisteException {
        validarIntervaloDeDatas(dataInicialStr, dataFinalStr);
        EmpregadoHorista horista = (EmpregadoHorista) getEmpregadoValido(id, EmpregadoHorista.class);

        double horasExtrasTotal = 0.0;
        for (CartaoDePonto cartao : horista.getCartoesDePonto().values()) {
            if (isDataNoIntervalo(cartao.getData(), dataInicialStr, dataFinalStr)) {
                double horasTrabalhadas = cartao.getHoras();
                if (horasTrabalhadas > 8) {
                    horasExtrasTotal += (horasTrabalhadas - 8);
                }
            }
        }
        return formatarResultadoNumerico(horasExtrasTotal);
    }

    public String getVendasRealizadas(String id, String dataInicialStr, String dataFinalStr) throws ValidacaoException, EmpregadoNaoExisteException {
        validarIntervaloDeDatas(dataInicialStr, dataFinalStr);
        EmpregadoComissionado comissionado = (EmpregadoComissionado) getEmpregadoValido(id, EmpregadoComissionado.class);

        double totalVendas = 0.0;
        for (ResultadoVenda venda : comissionado.getVendas().values()) {
            if (isDataNoIntervalo(venda.getData(), dataInicialStr, dataFinalStr)) {
                totalVendas += venda.getValor();
            }
        }
        return String.format("%.2f", totalVendas).replace('.', ',');
    }

    public String getTaxasServico(String id, String dataInicialStr, String dataFinalStr) throws ValidacaoException, EmpregadoNaoExisteException {
        validarIntervaloDeDatas(dataInicialStr, dataFinalStr);
        Empregado empregado = getEmpregadoValido(id);

        MembroSindicato membro = empregado.getMembroSindicato();
        if (membro == null) {
            throw new ValidacaoException("Empregado nao eh sindicalizado.");
        }

        double totalTaxas = 0.0;
        for (TaxaServico taxa : membro.getTaxasDeServico().values()) {
            if (isDataNoIntervalo(taxa.getData(), dataInicialStr, dataFinalStr)) {
                totalTaxas += taxa.getValor();
            }
        }
        return String.format("%.2f", totalTaxas).replace('.', ',');
    }

    // --- Métodos Privados de Ajuda ---

    // Nova função para comparar datas manualmente
    private boolean isDataNoIntervalo(String dataStr, String dataInicialStr, String dataFinalStr) {
        String[] dataParts = dataStr.split("/");
        int dia = Integer.parseInt(dataParts[0]);
        int mes = Integer.parseInt(dataParts[1]);
        int ano = Integer.parseInt(dataParts[2]);
        long dataNumerica = ano * 10000L + mes * 100L + dia;

        String[] dataInicialParts = dataInicialStr.split("/");
        int diaInicial = Integer.parseInt(dataInicialParts[0]);
        int mesInicial = Integer.parseInt(dataInicialParts[1]);
        int anoInicial = Integer.parseInt(dataInicialParts[2]);
        long dataInicialNumerica = anoInicial * 10000L + mesInicial * 100L + diaInicial;

        String[] dataFinalParts = dataFinalStr.split("/");
        int diaFinal = Integer.parseInt(dataFinalParts[0]);
        int mesFinal = Integer.parseInt(dataFinalParts[1]);
        int anoFinal = Integer.parseInt(dataFinalParts[2]);
        long dataFinalNumerica = anoFinal * 10000L + mesFinal * 100L + diaFinal;

        // Retorna verdadeiro se data >= inicial E data < final
        return dataNumerica >= dataInicialNumerica && dataNumerica < dataFinalNumerica;
    }

    private void validarIntervaloDeDatas(String dataInicialStr, String dataFinalStr) throws ValidacaoException {
        if (!isDataValida(dataInicialStr)) throw new DataInicialInvalidaException();
        if (!isDataValida(dataFinalStr)) throw new DataFinalInvalidaException();

        String[] dataInicialParts = dataInicialStr.split("/");
        long dataInicialNumerica = Long.parseLong(dataInicialParts[2]) * 10000L + Long.parseLong(dataInicialParts[1]) * 100L + Long.parseLong(dataInicialParts[0]);
        String[] dataFinalParts = dataFinalStr.split("/");
        long dataFinalNumerica = Long.parseLong(dataFinalParts[2]) * 10000L + Long.parseLong(dataFinalParts[1]) * 100L + Long.parseLong(dataFinalParts[0]);

        if (dataInicialNumerica > dataFinalNumerica) throw new DataInicialPosteriorException();
    }

    private String formatarResultadoNumerico(double valor) {
        if (valor == (long) valor) {
            return String.format("%d", (long) valor);
        } else {
            return String.valueOf(valor).replace('.', ',');
        }
    }

    public String totalFolha(String data) throws Exception {
        LocalDate dataFolha = LocalDate.parse(data, DateTimeFormatter.ofPattern("d/M/yyyy"));
        double total = 0.0;
        for (Empregado empregado : repository.findAll()) {
            if (isDiaDePagar(empregado, dataFolha)) {
                double salarioBruto = calcularSalarioBruto(empregado, dataFolha);
                double deducoes = calcularDeducoes(empregado, dataFolha);
                total += Math.max(0, salarioBruto - deducoes);
            }
        }
        return String.format("%.2f", total).replace('.', ',');
    }



    // --- Métodos Públicos de Cálculo (para serem usados pelo FolhaPagamentoService) ---

    public double calcularSalarioBruto(Empregado empregado, LocalDate dataFolha) throws ValidacaoException, EmpregadoNaoExisteException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        String dataInicialStr = empregado.getDataUltimoPagamento().plusDays(1).format(formatter);
        String dataFinalStr = dataFolha.plusDays(1).format(formatter);

        if (empregado instanceof EmpregadoHorista h) {
            double horasNormais = Double.parseDouble(getHorasNormaisTrabalhadas(h.getId(), dataInicialStr, dataFinalStr).replace(',', '.'));
            double horasExtras = Double.parseDouble(getHorasExtrasTrabalhadas(h.getId(), dataInicialStr, dataFinalStr).replace(',', '.'));
            double taxaHoraria = Double.parseDouble(h.getSalarioSemFormato().replace(',', '.'));
            return (horasNormais * taxaHoraria) + (horasExtras * taxaHoraria * 1.5);
        }
        if (empregado instanceof EmpregadoAssalariado) {
            return Double.parseDouble(empregado.getSalarioSemFormato().replace(',', '.'));
        }
        if (empregado instanceof EmpregadoComissionado c) {
            double salarioFixo = Double.parseDouble(c.getSalarioSemFormato().replace(',', '.')) * 12.0 / 26.0;
            double vendas = Double.parseDouble(getVendasRealizadas(c.getId(), dataInicialStr, dataFinalStr).replace(',', '.'));
            double comissao = Double.parseDouble(c.getComissao().replace(',', '.'));
            return salarioFixo + (vendas * comissao);
        }
        return 0;
    }

    public double calcularDeducoes(Empregado empregado, LocalDate dataFolha) throws ValidacaoException, EmpregadoNaoExisteException {
        if (!empregado.isSindicalizado()) return 0;
        MembroSindicato membro = empregado.getMembroSindicato();
        long diasDesdeUltimoPagamento = ChronoUnit.DAYS.between(empregado.getDataUltimoPagamento(), dataFolha);
        double taxaSindicalTotal = membro.getTaxaSindical() * diasDesdeUltimoPagamento;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        String dataInicialStr = empregado.getDataUltimoPagamento().plusDays(1).format(formatter);
        String dataFinalStr = dataFolha.plusDays(1).format(formatter);
        double taxasServicoTotal = Double.parseDouble(getTaxasServico(empregado.getId(), dataInicialStr, dataFinalStr).replace(',', '.'));

        return taxaSindicalTotal + taxasServicoTotal;
    }
    // --- Métodos Privados de Ajuda ---
    public boolean isDiaDePagar(Empregado empregado, LocalDate dataFolha) {
        if (empregado.getDataContratacao() != null && dataFolha.isBefore(empregado.getDataContratacao())) {
            return false;
        }
        if (empregado instanceof EmpregadoHorista) {
            return dataFolha.getDayOfWeek() == DayOfWeek.FRIDAY;
        }
        if (empregado instanceof EmpregadoAssalariado) {
            LocalDate ultimoDiaUtil = dataFolha.with(TemporalAdjusters.lastDayOfMonth());
            if (ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SATURDAY) ultimoDiaUtil = ultimoDiaUtil.minusDays(1);
            if (ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SUNDAY) ultimoDiaUtil = ultimoDiaUtil.minusDays(2);
            return dataFolha.equals(ultimoDiaUtil);
        }
        if (empregado instanceof EmpregadoComissionado) {
            LocalDate dataContrato = empregado.getDataContratacao();
            LocalDate primeiraSexta = dataContrato.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
            LocalDate primeiroPagamento = primeiraSexta.plusWeeks(1);

            long diasDesdePrimeiroPagamento = ChronoUnit.DAYS.between(primeiroPagamento, dataFolha);
            return dataFolha.isEqual(primeiroPagamento) || (diasDesdePrimeiroPagamento > 0 && diasDesdePrimeiroPagamento % 14 == 0);
        }
        return false;
    }
}