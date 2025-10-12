package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada quando a data inicial de um período é inválida.
 */
public class DataInicialInvalidaException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public DataInicialInvalidaException() {
        super("Data inicial invalida.");
    }
}