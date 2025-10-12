package br.ufal.ic.p2.wepayu.models;

/**
 * Classe que representa um cartão de ponto de um empregado horista.
 * Contém a data e o número de horas trabalhadas em um dia específico.
 */
public class CartaoDePonto {

    private String data;
    private Double horas;

    /**
     * Construtor padrão para persistência.
     */
    public CartaoDePonto() {
    }

    /**
     * Constrói uma instância de CartaoDePonto.
     * @param data A data do registro (formato "d/M/yyyy").
     * @param horas O total de horas trabalhadas no dia.
     */
    public CartaoDePonto(String data, Double horas) {
        this.data = data;
        this.horas = horas;
    }

    // Getters e Setters
    /**
     * Retorna a data do registro.
     * @return A data do registro.
     */
    public String getData() {
        return data;
    }

    /**
     * Retorna o total de horas trabalhadas.
     * @return O total de horas.
     */
    public Double getHoras() {
        return horas;
    }

    /**
     * Define a data do registro.
     * @param data A nova data.
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Define o total de horas trabalhadas.
     * @param horas O novo total de horas.
     */
    public void setHoras(Double horas) {
        this.horas = horas;
    }
}