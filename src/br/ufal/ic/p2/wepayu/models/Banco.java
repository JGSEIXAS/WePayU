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
    /**
     * Retorna o nome do banco.
     * @return O nome do banco.
     */
    public String getBanco() { return banco; }
    /**
     * Define o nome do banco.
     * @param banco O novo nome do banco.
     */
    public void setBanco(String banco) { this.banco = banco; }
    /**
     * Retorna o número da agência.
     * @return O número da agência.
     */
    public String getAgencia() { return agencia; }
    /**
     * Define o número da agência.
     * @param agencia O novo número da agência.
     */
    public void setAgencia(String agencia) { this.agencia = agencia; }
    /**
     * Retorna o número da conta corrente.
     * @return O número da conta corrente.
     */
    public String getContaCorrente() { return contaCorrente; }
    /**
     * Define o número da conta corrente.
     * @param contaCorrente O novo número da conta corrente.
     */
    public void setContaCorrente(String contaCorrente) { this.contaCorrente = contaCorrente; }
}