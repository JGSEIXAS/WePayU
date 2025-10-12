package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada quando um membro do sindicato com o ID especificado não existe.
 */
public class MembroNaoExisteException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public MembroNaoExisteException() {
        super("Membro nao existe.");
    }
}