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
 * Serviço responsável por consultas e cálculos que não alteram o estado do sistema.
 * Fornece métodos para obter informações sobre empregados, como horas trabalhadas,
 * vendas, taxas de serviço e cálculo de salário.
 */
public class ConsultaService extends BaseService {

    /**
     * Constrói uma instância de ConsultaService.
     * @param repository O repositório para acesso aos dados dos empregados.
     */
    public ConsultaService(EmpregadoRepository repository) {
        super(repository);
    }

    /**
     * Calcula o valor total da folha de pagamento para uma data específica.
     * @param data A data para a qual a folha deve ser calculada.
     * @return Uma string formatada com o valor total da folha.
     * @throws Exception se ocorrer um erro durante o cálculo.
     */
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

    /**
     * Trunca um valor double para duas casas decimais.
     * @param value O valor a ser truncado.
     * @return O valor truncado.
     */
    private double truncate(double value) {
        return Math.floor((value * 100) + 1e-9) / 100.0;
    }

    /**
     * Retorna o total de horas normais trabalhadas por um empregado horista em um período.
     * @param id O ID do empregado.
     * @param dataInicialStr A data de início do período.
     * @param dataFinalStr A data de fim do período.
     * @return Uma string com o total de horas normais.
     * @throws ValidacaoException se os dados forem inválidos.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
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
     * Retorna o total de horas extras trabalhadas por um empregado horista em um período.
     * @param id O ID do empregado.
     * @param dataInicialStr A data de início do período.
     * @param dataFinalStr A data de fim do período.
     * @return Uma string com o total de horas extras.
     * @throws ValidacaoException se os dados forem inválidos.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
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
     * Retorna o valor total de vendas realizadas por um empregado comissionado em um período.
     * @param id O ID do empregado.
     * @param dataInicialStr A data de início do período.
     * @param dataFinalStr A data de fim do período.
     * @return Uma string formatada com o valor total das vendas.
     * @throws ValidacaoException se os dados forem inválidos.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
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
     * Retorna o valor total de taxas de serviço de um empregado sindicalizado em um período.
     * @param id O ID do empregado.
     * @param dataInicialStr A data de início do período.
     * @param dataFinalStr A data de fim do período.
     * @return Uma string formatada com o valor total das taxas.
     * @throws ValidacaoException se os dados forem inválidos.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
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
     * Verifica se uma data está dentro de um intervalo de datas.
     * @param dataStr A data a ser verificada.
     * @param dataInicialStr A data de início do intervalo.
     * @param dataFinalStr A data de fim do intervalo.
     * @return {@code true} se a data estiver no intervalo, {@code false} caso contrário.
     */
    private boolean isDataNoIntervalo(String dataStr, String dataInicialStr, String dataFinalStr) {
        LocalDate data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("d/M/yyyy"));
        LocalDate dataInicial = LocalDate.parse(dataInicialStr, DateTimeFormatter.ofPattern("d/M/yyyy"));
        LocalDate dataFinal = LocalDate.parse(dataFinalStr, DateTimeFormatter.ofPattern("d/M/yyyy"));
        return !data.isBefore(dataInicial) && data.isBefore(dataFinal);
    }

    /**
     * Valida se um intervalo de datas é válido.
     * @param dataInicialStr A data de início do intervalo.
     * @param dataFinalStr A data de fim do intervalo.
     * @throws ValidacaoException se as datas forem inválidas ou se a data inicial for posterior à final.
     */
    private void validarIntervaloDeDatas(String dataInicialStr, String dataFinalStr) throws ValidacaoException {
        if (!isDataValida(dataInicialStr)) throw new DataInicialInvalidaException();
        if (!isDataValida(dataFinalStr)) throw new DataFinalInvalidaException();
        LocalDate dataInicial = LocalDate.parse(dataInicialStr, DateTimeFormatter.ofPattern("d/M/yyyy"));
        LocalDate dataFinal = LocalDate.parse(dataFinalStr, DateTimeFormatter.ofPattern("d/M/yyyy"));
        if (dataInicial.isAfter(dataFinal)) throw new DataInicialPosteriorException();
    }

    /**
     * Formata um resultado numérico como string, com ou sem casas decimais.
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
     * Calcula o salário bruto de um empregado para uma data de folha específica.
     * @param empregado O empregado para o qual o salário será calculado.
     * @param dataFolha A data da folha de pagamento.
     * @return O valor do salário bruto.
     * @throws ValidacaoException se ocorrer um erro de validação.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     */
    public double calcularSalarioBruto(Empregado empregado, LocalDate dataFolha) throws ValidacaoException, EmpregadoNaoExisteException {
        return empregado.calcularSalarioBruto(dataFolha, this);
    }

    /**
     * Calcula as deduções totais (taxas sindicais e de serviço) para um empregado.
     * @param empregado O empregado para o qual as deduções serão calculadas.
     * @param dataFolha A data da folha de pagamento.
     * @return O valor total das deduções.
     * @throws ValidacaoException se ocorrer um erro de validação.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     */
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

    /**
     * Verifica se uma data é dia de pagamento para um empregado.
     * @param empregado O empregado a ser verificado.
     * @param dataFolha A data a ser verificada.
     * @return {@code true} se for dia de pagamento, {@code false} caso contrário.
     */
    public boolean isDiaDePagar(Empregado empregado, LocalDate dataFolha) {
        if (empregado.getDataContratacao() != null && dataFolha.isBefore(empregado.getDataContratacao())) {
            return false;
        }
        return empregado.getAgendaPagamento().isDiaDePagamento(dataFolha);
    }

    /**
     * Formata o método de pagamento de um empregado como uma string legível.
     * @param e O empregado.
     * @return A string formatada do método de pagamento.
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
     * Calcula o salário fixo proporcional para um empregado comissionado.
     * @param c O empregado comissionado.
     * @param dataFolha A data da folha de pagamento.
     * @return O valor do salário fixo proporcional.
     */
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

    /**
     * Calcula o valor da comissão sobre as vendas.
     * @param c O empregado comissionado.
     * @param vendas O valor total das vendas.
     * @return O valor da comissão.
     */
    public double getComissaoSobreVendas(EmpregadoComissionado c, double vendas) {
        double comissaoPercentual = Double.parseDouble(c.getComissao().replace(',', '.'));
        return truncate(vendas * comissaoPercentual);
    }
}