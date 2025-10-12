package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

/**
 * Exceção lançada quando a data final de um período é inválida.
 */
public class DataFinalInvalidaException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public DataFinalInvalidaException() { super("Data final invalida."); }
}