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
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
}
