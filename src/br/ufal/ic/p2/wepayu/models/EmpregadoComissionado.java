package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;
import br.ufal.ic.p2.wepayu.Services.ConsultaService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe que representa um empregado que recebe um salário fixo mais uma comissão sobre vendas.
 */
public class EmpregadoComissionado extends Empregado {
    private Map<String, ResultadoVenda> vendas;
    private String comissao;

    /**
     * Construtor padrão que inicializa a lista de vendas.
     */
    public EmpregadoComissionado() {
        this.vendas = new HashMap<>();
    }

    /**
     * Constrói uma instância de EmpregadoComissionado.
     * @param id ID único do empregado.
     * @param nome Nome do empregado.
     * @param endereco Endereço do empregado.
     * @param tipo Tipo do empregado (deve ser "comissionado").
     * @param salario Salário base mensal.
     * @param comissao Taxa de comissão sobre vendas.
     */
    public EmpregadoComissionado(String id, String nome, String endereco, String tipo, String salario, String comissao) {
        super(id, nome, endereco, tipo, salario);
        this.comissao = comissao;
        this.vendas = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Calcula o salário bruto como a soma do salário fixo quinzenal mais a comissão sobre as vendas do período.
     */
    @Override
    public double calcularSalarioBruto(LocalDate dataFolha, ConsultaService consultaService) throws ValidacaoException, EmpregadoNaoExisteException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        String dataInicialStr = getDataUltimoPagamento().plusDays(1).format(formatter);
        String dataFinalStr = dataFolha.plusDays(1).format(formatter);

        double salarioFixo = consultaService.getSalarioFixoComissionado(this);
        double vendas = Double.parseDouble(consultaService.getVendasRealizadas(getId(), dataInicialStr, dataFinalStr).replace(',', '.'));
        double comissao = consultaService.getComissaoSobreVendas(this, vendas);

        double salario = salarioFixo + comissao;
        return Math.floor((salario * 100) + 1e-9) / 100.0;
    }

    /**
     * Adiciona um novo resultado de venda para este empregado.
     * @param venda O objeto {@link ResultadoVenda} a ser adicionado.
     */
    public void lancaVenda(ResultadoVenda venda) {
        this.vendas.put(venda.getData(), venda);
    }

    // Getters e Setters
    public Map<String, ResultadoVenda> getVendas() {
        return vendas;
    }

    public void setVendas(Map<String, ResultadoVenda> vendas) {
        this.vendas = vendas;
    }

    public String getComissao() {
        if (this.comissao == null) return "0,00";
        String comissaoParaParse = this.comissao.replace(',', '.');
        double valorNumerico = Double.parseDouble(comissaoParaParse);
        return String.format("%.2f", valorNumerico).replace('.', ',');
    }

    public void setComissao(String comissao) {
        this.comissao = comissao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Empregado clone() {
        EmpregadoComissionado cloned = new EmpregadoComissionado();
        super.copy(cloned);
        cloned.setComissao(this.getComissao());
        Map<String, ResultadoVenda> clonedVendas = new HashMap<>();
        for (Map.Entry<String, ResultadoVenda> entry : this.vendas.entrySet()) {
            clonedVendas.put(entry.getKey(), entry.getValue().clone());
        }
        cloned.setVendas(clonedVendas);
        return cloned;
    }
}