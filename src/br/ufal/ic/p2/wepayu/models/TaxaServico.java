package br.ufal.ic.p2.wepayu.models;

public class TaxaServico {
    private String data;
    private double valor;

    public TaxaServico() {} // Construtor para persistência
    public TaxaServico(String data, double valor) { this.data = data; this.valor = valor; }

    // Getters e Setters
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
}