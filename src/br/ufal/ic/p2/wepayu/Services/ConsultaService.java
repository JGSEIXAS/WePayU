package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * Serviço responsável por realizar consultas e cálculos sobre os dados dos empregados,
 * sem alterar o estado do sistema. Fornece métodos para obter informações
 * sobre horas trabalhadas, vendas, taxas de serviço e totais da folha de pagamento.
 */
public class ConsultaService extends BaseService {

    /**
     * Constrói uma instância de ConsultaService com o repositório de empregados.
     * @param repository O repositório para acesso aos dados dos empregados.
     */
    public ConsultaService(EmpregadoRepository repository) {
        super(repository);
    }

    /**
     * Trunca um valor double para duas casas decimais, evitando problemas de arredondamento.
     * @param value O valor a ser truncado.
     * @return O valor truncado com duas casas decimais.
     */
    private double truncate(double value) {
        return Math.floor((value * 100) + 1e-9) / 100.0;
    }

    /**
     * Calcula o total de horas normais (até 8h/dia) trabalhadas por um horista em um período.
     * @param id O ID do empregado horista.
     * @param dataInicialStr A data de início do período (inclusiva).
     * @param dataFinalStr A data de fim do período (exclusiva).
     * @return Uma string formatada com o total de horas normais.
     * @throws ValidacaoException Se as datas forem inválidas.
     * @throws EmpregadoNaoExisteException Se o empregado não for encontrado ou não for horista.
     */
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

    /**
     * Calcula o total de horas extras (acima de 8h/dia) trabalhadas por um horista em um período.
     * @param id O ID do empregado horista.
     * @param dataInicialStr A data de início do período (inclusiva).
     * @param dataFinalStr A data de fim do período (exclusiva).
     * @return Uma string formatada com o total de horas extras.
     * @throws ValidacaoException Se as datas forem inválidas.
     * @throws EmpregadoNaoExisteException Se o empregado não for encontrado ou não for horista.
     */
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

    /**
     * Calcula o valor total de vendas realizadas por um comissionado em um período.
     * @param id O ID do empregado comissionado.
     * @param dataInicialStr A data de início do período (inclusiva).
     * @param dataFinalStr A data de fim do período (exclusiva).
     * @return Uma string formatada com o valor total das vendas.
     * @throws ValidacaoException Se as datas forem inválidas.
     * @throws EmpregadoNaoExisteException Se o empregado não for encontrado ou não for comissionado.
     */
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

    /**
     * Calcula o valor total de taxas de serviço de um membro de sindicato em um período.
     * @param id O ID do empregado.
     * @param dataInicialStr A data de início do período (inclusiva).
     * @param dataFinalStr A data de fim do período (exclusiva).
     * @return Uma string formatada com o valor total das taxas.
     * @throws ValidacaoException Se as datas forem inválidas.
     * @throws EmpregadoNaoExisteException Se o empregado não for encontrado ou não for sindicalizado.
     */
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

    /**
     * Verifica se uma data está dentro de um intervalo de datas especificado.
     * @param dataStr A data a ser verificada.
     * @param dataInicialStr A data de início do intervalo (inclusiva).
     * @param dataFinalStr A data de fim do intervalo (exclusiva).
     * @return true se a data estiver no intervalo, false caso contrário.
     */
    private boolean isDataNoIntervalo(String dataStr, String dataInicialStr, String dataFinalStr) {
        LocalDate data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("d/M/yyyy"));
        LocalDate dataInicial = LocalDate.parse(dataInicialStr, DateTimeFormatter.ofPattern("d/M/yyyy"));
        LocalDate dataFinal = LocalDate.parse(dataFinalStr, DateTimeFormatter.ofPattern("d/M/yyyy"));
        return !data.isBefore(dataInicial) && data.isBefore(dataFinal);
    }

    /**
     * Valida um intervalo de datas.
     * @param dataInicialStr A data de início.
     * @param dataFinalStr A data de fim.
     * @throws ValidacaoException Se alguma das datas for inválida ou se a data inicial for posterior à final.
     */
    private void validarIntervaloDeDatas(String dataInicialStr, String dataFinalStr) throws ValidacaoException {
        if (!isDataValida(dataInicialStr)) throw new DataInicialInvalidaException();
        if (!isDataValida(dataFinalStr)) throw new DataFinalInvalidaException();
        LocalDate dataInicial = LocalDate.parse(dataInicialStr, DateTimeFormatter.ofPattern("d/M/yyyy"));
        LocalDate dataFinal = LocalDate.parse(dataFinalStr, DateTimeFormatter.ofPattern("d/M/yyyy"));
        if (dataInicial.isAfter(dataFinal)) throw new DataInicialPosteriorException();
    }

    /**
     * Formata um resultado numérico, omitindo casas decimais se for um inteiro.
     * @param valor O valor a ser formatado.
     * @return A string formatada.
     */
    private String formatarResultadoNumerico(double valor) {
        if (valor == (long) valor) {
            return String.format("%d", (long) valor);
        } else {
            return String.format("%.1f", valor).replace('.', ',');
        }
    }

    /**
     * Calcula o valor bruto total da folha de pagamento para uma data específica.
     * @param data A data para a qual a folha deve ser calculada.
     * @return Uma string formatada com o valor total da folha.
     * @throws Exception Se ocorrer um erro durante o cálculo.
     */
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

    /**
     * Delega o cálculo do salário bruto para o objeto Empregado apropriado (Polimorfismo).
     * @param empregado O empregado para o qual o salário será calculado.
     * @param dataFolha A data de referência para o cálculo.
     * @return O valor do salário bruto.
     * @throws ValidacaoException Se houver erro de validação nos dados.
     * @throws EmpregadoNaoExisteException Se houver referência a um empregado inexistente.
     */
    public double calcularSalarioBruto(Empregado empregado, LocalDate dataFolha) throws ValidacaoException, EmpregadoNaoExisteException {
        return empregado.calcularSalarioBruto(dataFolha, this);
    }

    /**
     * Calcula o total de deduções (taxa sindical + taxas de serviço) para um empregado.
     * @param empregado O empregado para o qual as deduções serão calculadas.
     * @param dataFolha A data de referência para o cálculo.
     * @return O valor total das deduções.
     * @throws ValidacaoException Se houver erro de validação nos dados.
     * @throws EmpregadoNaoExisteException Se houver referência a um empregado inexistente.
     */
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

    /**
     * Verifica se um empregado deve ser pago em uma determinada data, com base em sua agenda de pagamento.
     * @param empregado O empregado a ser verificado.
     * @param dataFolha A data do pagamento.
     * @return true se for dia de pagamento, false caso contrário.
     */
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

    /**
     * Formata a descrição do método de pagamento para exibição em relatórios.
     * @param e O empregado.
     * @return Uma string formatada descrevendo o método de pagamento.
     */
    public String getMetodoPagamentoFormatado(Empregado e) {
        MetodoPagamento mp = e.getMetodoPagamento();
        if (mp instanceof EmMaos) return "Em maos";
        if (mp instanceof Correios) return "Correios, " + e.getEndereco();
        if (mp instanceof Banco b) {
            return "Banco do Brasil, Ag. " + b.getAgencia() + " CC " + b.getContaCorrente();
        }
        return "";
    }

    /**
     * Calcula o salário fixo quinzenal de um empregado comissionado.
     * @param c O empregado comissionado.
     * @return O valor do salário fixo para a quinzena.
     */
    public double getSalarioFixoComissionado(EmpregadoComissionado c) {
        double salarioAnual = Double.parseDouble(c.getSalarioSemFormato().replace(',', '.')) * 12.0;
        return truncate(salarioAnual / 26.0);
    }

    /**
     * Calcula o valor da comissão sobre as vendas para um empregado comissionado.
     * @param c O empregado comissionado.
     * @param vendas O valor total das vendas no período.
     * @return O valor da comissão.
     */
    public double getComissaoSobreVendas(EmpregadoComissionado c, double vendas) {
        double comissaoPercentual = Double.parseDouble(c.getComissao().replace(',', '.'));
        return truncate(vendas * comissaoPercentual);
    }
}
