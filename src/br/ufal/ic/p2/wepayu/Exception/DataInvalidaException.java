package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

/**
 * Exceção lançada quando uma data fornecida é inválida.
 */
public class DataInvalidaException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public DataInvalidaException() { super("Data invalida."); }
}