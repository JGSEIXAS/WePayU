package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

/**
 * Exceção lançada quando a identificação do empregado não é fornecida.
 */
public class IdentificacaoNulaException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public IdentificacaoNulaException() { super("Identificacao do empregado nao pode ser nula."); }
}