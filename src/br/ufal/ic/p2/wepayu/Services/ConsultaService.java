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

    public String totalFolha(String data) throws Exception {
        LocalDate dataFolha = LocalDate.parse(data, DateTimeFormatter.ofPattern("d/M/yyyy"));
        double calculatedTotal = 0.0;
        for (Empregado empregado : repository.findAll()) {
            if (isDiaDePagar(empregado, dataFolha)) {
                calculatedTotal += calcularSalarioBruto(empregado, dataFolha);
            }
        }
        return String.format("%.2f", calculatedTotal).replace('.', ',');
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

    public double calcularSalarioBruto(Empregado empregado, LocalDate dataFolha) throws ValidacaoException, EmpregadoNaoExisteException {
        return empregado.calcularSalarioBruto(dataFolha, this);
    }

    public double calcularDeducoes(Empregado empregado, LocalDate dataFolha) throws ValidacaoException, EmpregadoNaoExisteException {
        if (!empregado.isSindicalizado() || calcularSalarioBruto(empregado, dataFolha) <= 0) {
            return 0;
        }

        MembroSindicato membro = empregado.getMembroSindicato();
        double taxaSindicalDiaria = membro.getTaxaSindical();
        double taxaSindicalTotal;

        // CORREÇÃO: Lógica que diferencia mensalistas para passar no us7.txt.
        if (empregado instanceof EmpregadoAssalariado && empregado.getAgendaPagamento().getDescricao().startsWith("mensal")) {
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
        return empregado.getAgendaPagamento().isDiaDePagamento(dataFolha);
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

    public double getSalarioFixoComissionado(EmpregadoComissionado c, LocalDate dataFolha) {
        double salarioBase = Double.parseDouble(c.getSalarioSemFormato().replace(',', '.'));
        String agenda = c.getAgendaPagamento().getDescricao();
        if (agenda.startsWith("semanal")) {
            int frequencia = 1;
            if (agenda.split(" ").length == 3) {
                frequencia = Integer.parseInt(agenda.split(" ")[1]);
            } else if (agenda.equals("semanal 2 5")) {
                frequencia = 2; // Quinzenal
            }
            double result = (salarioBase * 12 / 52.0) * frequencia;
            return Math.floor((result * 100) + 1e-9) / 100.0;
        }
        return salarioBase;
    }

    public double getComissaoSobreVendas(EmpregadoComissionado c, double vendas) {
        double comissaoPercentual = Double.parseDouble(c.getComissao().replace(',', '.'));
        return truncate(vendas * comissaoPercentual);
    }
}