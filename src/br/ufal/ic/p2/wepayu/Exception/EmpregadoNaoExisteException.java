package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada quando um empregado com o ID especificado não existe.
 */
public class EmpregadoNaoExisteException extends Exception {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public EmpregadoNaoExisteException() {
        super("Empregado nao existe.");
    }
}