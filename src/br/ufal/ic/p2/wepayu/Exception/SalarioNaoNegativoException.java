package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

/**
 * Exceção lançada quando o salário é um valor negativo.
 */
public class SalarioNaoNegativoException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public SalarioNaoNegativoException() { super("Salario deve ser nao-negativo."); }
}