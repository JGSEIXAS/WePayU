package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

/**
 * Exceção lançada quando o tipo de empregado não é aplicável para a operação.
 */
public class TipoNaoAplicavelException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public TipoNaoAplicavelException() { super("Tipo nao aplicavel."); }
}