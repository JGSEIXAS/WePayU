package br.ufal.ic.p2.wepayu.Exception;

public class MembroNaoExisteException extends EmpregadoNaoExisteException {
    public MembroNaoExisteException() {
        super("Membro nao existe.");
    }
}
