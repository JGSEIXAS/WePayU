package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

/**
 * Exceção lançada quando um valor que deveria ser positivo não é.
 */
public class ValorPositivoException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public ValorPositivoException() { super("Valor deve ser positivo."); }
}