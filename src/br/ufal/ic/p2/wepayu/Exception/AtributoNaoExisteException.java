package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

/**
 * Exceção lançada quando um atributo especificado não existe para um empregado.
 */
public class AtributoNaoExisteException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public AtributoNaoExisteException() { super("Atributo nao existe."); }
}