package br.ufal.ic.p2.wepayu.Exception;

public class AgendaInvalidaException extends ValidacaoException {
    public AgendaInvalidaException() {
        super("Descricao de agenda invalida");
    }
}