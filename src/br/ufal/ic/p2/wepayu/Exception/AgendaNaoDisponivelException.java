package br.ufal.ic.p2.wepayu.Exception;

public class AgendaNaoDisponivelException extends ValidacaoException {
    public AgendaNaoDisponivelException() {
        super("Agenda de pagamento nao esta disponivel");
    }
}