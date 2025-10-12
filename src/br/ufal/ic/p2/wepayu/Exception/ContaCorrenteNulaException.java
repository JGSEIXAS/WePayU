package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada quando a conta corrente não é fornecida.
 */
public class ContaCorrenteNulaException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public ContaCorrenteNulaException() {
        super("Conta corrente nao pode ser nulo.");
    }
}