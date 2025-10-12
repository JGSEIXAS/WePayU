package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

/**
 * Exceção lançada ao tentar realizar uma operação específica de empregado horista em um empregado de outro tipo.
 */
public class EmpregadoNaoHoristaException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public EmpregadoNaoHoristaException() { super("Empregado nao eh horista."); }
}