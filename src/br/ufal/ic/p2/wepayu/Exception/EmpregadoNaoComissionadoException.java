package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

/**
 * Exceção lançada ao tentar realizar uma operação específica de empregado comissionado em um empregado de outro tipo.
 */
public class EmpregadoNaoComissionadoException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public EmpregadoNaoComissionadoException() { super("Empregado nao eh comissionado."); }
}