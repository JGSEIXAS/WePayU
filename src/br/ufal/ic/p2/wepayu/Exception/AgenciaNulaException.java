package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada quando a agência bancária não é fornecida.
 */
public class AgenciaNulaException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public AgenciaNulaException() {
        super("Agencia nao pode ser nulo.");
    }
}