package br.ufal.ic.p2.wepayu.Exception;

public class MembroNuloException extends ValidacaoException {
    public MembroNuloException() {
        super("Identificacao do membro nao pode ser nula.");
    }
}