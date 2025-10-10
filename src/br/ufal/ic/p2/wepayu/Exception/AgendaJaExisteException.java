package br.ufal.ic.p2.wepayu.Exception;

public class AgendaJaExisteException extends ValidacaoException {
    public AgendaJaExisteException() {
        super("Agenda de pagamentos ja existe");
    }
}