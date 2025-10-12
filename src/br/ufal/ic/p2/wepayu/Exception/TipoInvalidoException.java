package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

/**
 * Exceção lançada quando o tipo de empregado especificado é inválido.
 */
public class TipoInvalidoException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public TipoInvalidoException() { super("Tipo invalido."); }
}