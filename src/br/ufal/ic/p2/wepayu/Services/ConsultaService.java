package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

public class ConsultaService extends BaseService {

    public ConsultaService(EmpregadoRepository repository) {
        super(repository);
    }

    private double truncate(double value) {
        return Math.floor((value * 100) + 1e-9) / 100.0;
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
            throw new EmpregadoNaoSindicalizadoException();
        }
        double totalTaxas = 0.0;
        for (TaxaServico taxa : membro.getTaxasDeServico().values()) {
            if (isDataNoIntervalo(taxa.getData(), dataInicialStr, dataFinalStr)) {
                totalTaxas += taxa.getValor();
            }
        }
        return String.format("%.2f", totalTaxas).replace('.', ',');
    }

    private boolean isDataNoIntervalo(String dataStr, String dataInicialStr, String dataFinalStr) {
        LocalDate data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("d/M/yyyy"));
        LocalDate dataInicial = LocalDate.parse(dataInicialStr, DateTimeFormatter.ofPattern("d/M/yyyy"));
        LocalDate dataFinal = LocalDate.parse(dataFinalStr, DateTimeFormatter.ofPattern("d/M/yyyy"));
        return !data.isBefore(dataInicial) && data.isBefore(dataFinal);
    }

    private void validarIntervaloDeDatas(String dataInicialStr, String dataFinalStr) throws ValidacaoException {
        if (!isDataValida(dataInicialStr)) throw new DataInicialInvalidaException();
        if (!isDataValida(dataFinalStr)) throw new DataFinalInvalidaException();
        LocalDate dataInicial = LocalDate.parse(dataInicialStr, DateTimeFormatter.ofPattern("d/M/yyyy"));
        LocalDate dataFinal = LocalDate.parse(dataFinalStr, DateTimeFormatter.ofPattern("d/M/yyyy"));
        if (dataInicial.isAfter(dataFinal)) throw new DataInicialPosteriorException();
    }

    private String formatarResultadoNumerico(double valor) {
        if (valor == (long) valor) {
            return String.format("%d", (long) valor);
        } else {
            return String.format("%.1f", valor).replace('.', ',');
        }
    }

    public String totalFolha(String data) throws Exception {
        LocalDate dataFolha = LocalDate.parse(data, DateTimeFormatter.ofPattern("d/M/yyyy"));
        double total = 0.0;
        for (Empregado empregado : repository.findAll()) {
            if (isDiaDePagar(empregado, dataFolha)) {
                total += calcularSalarioBruto(empregado, dataFolha);
            }
        }
        return String.format("%.2f", total).replace('.', ',');
    }

    public double calcularSalarioBruto(Empregado empregado, LocalDate dataFolha) throws ValidacaoException, EmpregadoNaoExisteException {
        return empregado.calcularSalarioBruto(dataFolha, this);
    }

    public double calcularDeducoes(Empregado empregado, LocalDate dataFolha) throws ValidacaoException, EmpregadoNaoExisteException {
        if (!empregado.isSindicalizado() || calcularSalarioBruto(empregado, dataFolha) <= 0) {
            return 0;
        }
        MembroSindicato membro = empregado.getMembroSindicato();
        double taxaSindicalDiaria = membro.getTaxaSindical();
        double taxaSindicalTotal = 0;
        if (empregado instanceof EmpregadoAssalariado) {
            taxaSindicalTotal = taxaSindicalDiaria * dataFolha.lengthOfMonth();
        } else {
            long daysBetween = ChronoUnit.DAYS.between(empregado.getDataUltimoPagamento(), dataFolha);
            taxaSindicalTotal = daysBetween * taxaSindicalDiaria;
        }
        String dataInicialStr = empregado.getDataUltimoPagamento().plusDays(1).format(DateTimeFormatter.ofPattern("d/M/yyyy"));
        String dataFinalStr = dataFolha.plusDays(1).format(DateTimeFormatter.ofPattern("d/M/yyyy"));
        double taxasServicoTotal = Double.parseDouble(getTaxasServico(empregado.getId(), dataInicialStr, dataFinalStr).replace(',', '.'));
        return truncate(taxaSindicalTotal + taxasServicoTotal);
    }

    public boolean isDiaDePagar(Empregado empregado, LocalDate dataFolha) {
        if (empregado.getDataContratacao() != null && dataFolha.isBefore(empregado.getDataContratacao())) {
            return false;
        }
        if (empregado instanceof EmpregadoHorista) {
            return dataFolha.getDayOfWeek() == DayOfWeek.FRIDAY;
        }
        if (empregado instanceof EmpregadoAssalariado) {
            LocalDate ultimoDiaUtil = dataFolha.with(TemporalAdjusters.lastDayOfMonth());
            while (ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SATURDAY || ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SUNDAY) {
                ultimoDiaUtil = ultimoDiaUtil.minusDays(1);
            }
            return dataFolha.equals(ultimoDiaUtil);
        }
        if (empregado instanceof EmpregadoComissionado) {
            LocalDate dataContrato = empregado.getDataContratacao();
            LocalDate primeiroPagamento = dataContrato.with(TemporalAdjusters.next(DayOfWeek.FRIDAY)).with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
            if (dataFolha.isBefore(primeiroPagamento)) return false;
            long daysBetween = ChronoUnit.DAYS.between(primeiroPagamento, dataFolha);
            return daysBetween % 14 == 0 && dataFolha.getDayOfWeek() == DayOfWeek.FRIDAY;
        }
        return false;
    }

    public String getMetodoPagamentoFormatado(Empregado e) {
        MetodoPagamento mp = e.getMetodoPagamento();
        if (mp instanceof EmMaos) return "Em maos";
        if (mp instanceof Correios) return "Correios, " + e.getEndereco();
        if (mp instanceof Banco b) {
            return "Banco do Brasil, Ag. " + b.getAgencia() + " CC " + b.getContaCorrente();
        }
        return "";
    }

    public double getSalarioFixoComissionado(EmpregadoComissionado c) {
        double salarioAnual = Double.parseDouble(c.getSalarioSemFormato().replace(',', '.')) * 12.0;
        return truncate(salarioAnual / 26.0);
    }

    public double getComissaoSobreVendas(EmpregadoComissionado c, double vendas) {
        double comissaoPercentual = Double.parseDouble(c.getComissao().replace(',', '.'));
        return truncate(vendas * comissaoPercentual);
    }
}