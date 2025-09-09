package br.ufal.ic.p2.wepayu.models;

/**
 * Classe que representa o resultado de uma venda realizada por um empregado comissionado.
 */
public class ResultadoVenda {
    private String data;
    private double valor;

    /**
     * Construtor padrão para persistência.
     */
    public ResultadoVenda() {
    }

    /**
     * Constrói uma instância de ResultadoVenda.
     * @param data A data em que a venda foi realizada.
     * @param valor O valor monetário da venda.
     */
    public ResultadoVenda(String data, double valor) {
        this.data = data;
        this.valor = valor;
    }

    /**
     * Cria uma cópia profunda (clone) do objeto ResultadoVenda.
     * @return Uma nova instância de {@link ResultadoVenda} com os mesmos dados.
     */
    @Override
    public ResultadoVenda clone() {
        return new ResultadoVenda(this.data, this.valor);
    }

    // Getters e Setters
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
