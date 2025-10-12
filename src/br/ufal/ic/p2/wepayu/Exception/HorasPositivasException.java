package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

/**
 * Exceção lançada quando as horas trabalhadas não são um valor positivo.
 */
public class HorasPositivasException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public HorasPositivasException() { super("Horas devem ser positivas."); }
}