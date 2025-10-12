package br.ufal.ic.p2.wepayu.models;

/**
 * Classe que representa uma taxa de serviço adicional cobrada pelo sindicato.
 */
public class TaxaServico {
    private String data;
    private double valor;

    /**
     * Construtor padrão para persistência.
     */
    public TaxaServico() {}

    /**
     * Constrói uma instância de TaxaServico.
     * @param data A data da cobrança da taxa.
     * @param valor O valor da taxa de serviço.
     */
    public TaxaServico(String data, double valor) { this.data = data; this.valor = valor; }

    // Getters e Setters
    /**
     * Retorna a data da taxa de serviço.
     * @return A data da taxa.
     */
    public String getData() { return data; }
    /**
     * Define a data da taxa de serviço.
     * @param data A nova data.
     */
    public void setData(String data) { this.data = data; }
    /**
     * Retorna o valor da taxa de serviço.
     * @return O valor da taxa.
     */
    public double getValor() { return valor; }
    /**
     * Define o valor da taxa de serviço.
     * @param valor O novo valor.
     */
    public void setValor(double valor) { this.valor = valor; }
}