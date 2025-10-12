package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

/**
 * Exceção lançada quando o salário não é um valor numérico.
 */
public class SalarioNumericoException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public SalarioNumericoException() { super("Salario deve ser numerico."); }
}