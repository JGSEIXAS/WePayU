package br.ufal.ic.p2.wepayu.models;

public class ResultadoVenda {
    private String data;
    private double valor;

    public ResultadoVenda() {
    }

    public ResultadoVenda(String data, double valor) {
        this.data = data;
        this.valor = valor;
    }

    // Getters
    public String getData() {
        return data;
    }

    public double getValor() {
        return valor;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}