package br.ufal.ic.p2.wepayu.models;

/**
 * Classe que representa o método de pagamento por depósito em conta bancária.
 * Armazena as informações necessárias para o depósito.
 */
public class Banco extends MetodoPagamento {
    private String banco;
    private String agencia;
    private String contaCorrente;

    /**
     * Construtor padrão para persistência.
     */
    public Banco() {}

    /**
     * Constrói uma instância de Banco com os dados da conta.
     * @param banco O nome do banco.
     * @param agencia O número da agência.
     * @param contaCorrente O número da conta corrente.
     */
    public Banco(String banco, String agencia, String contaCorrente) {
        this.banco = banco;
        this.agencia = agencia;
        this.contaCorrente = contaCorrente;
    }

    /**
     * Cria uma cópia profunda (clone) do objeto Banco.
     * @return Uma nova instância de {@link Banco} com os mesmos dados.
     */
    @Override
    public Banco clone() {
        return new Banco(this.banco, this.agencia, this.contaCorrente);
    }

    // Getters e Setters
    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }
    public String getAgencia() { return agencia; }
    public void setAgencia(String agencia) { this.agencia = agencia; }
    public String getContaCorrente() { return contaCorrente; }
    public void setContaCorrente(String contaCorrente) { this.contaCorrente = contaCorrente; }
}
