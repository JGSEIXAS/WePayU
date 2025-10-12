package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada ao tentar acessar dados bancários de um empregado que não recebe em banco.
 */
public class EmpregadoNaoRecebeEmBancoException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public EmpregadoNaoRecebeEmBancoException() {
        super("Empregado nao recebe em banco.");
    }
}