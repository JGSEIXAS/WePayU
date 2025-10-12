package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada ao tentar realizar uma operação de sindicalizado em um empregado que não é.
 */
public class EmpregadoNaoSindicalizadoException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public EmpregadoNaoSindicalizadoException() {
        super("Empregado nao eh sindicalizado.");
    }
}