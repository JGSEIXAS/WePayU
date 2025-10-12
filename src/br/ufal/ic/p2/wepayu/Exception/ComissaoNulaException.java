package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

/**
 * Exceção lançada quando a comissão não é fornecida.
 */
public class ComissaoNulaException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public ComissaoNulaException() { super("Comissao nao pode ser nula."); }
}