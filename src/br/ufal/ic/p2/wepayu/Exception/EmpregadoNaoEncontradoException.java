package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada quando não há empregado com o nome especificado.
 */
public class EmpregadoNaoEncontradoException extends Exception {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public EmpregadoNaoEncontradoException() {
        super("Nao ha empregado com esse nome.");
    }
}