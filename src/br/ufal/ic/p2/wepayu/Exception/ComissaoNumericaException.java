package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

/**
 * Exceção lançada quando a comissão não é um valor numérico.
 */
public class ComissaoNumericaException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public ComissaoNumericaException() { super("Comissao deve ser numerica."); }
}