package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

/**
 * Exceção lançada quando a comissão é um valor negativo.
 */
public class ComissaoNaoNegativaException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public ComissaoNaoNegativaException() { super("Comissao deve ser nao-negativa."); }
}