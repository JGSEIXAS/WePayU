package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada ao tentar atribuir uma agenda de pagamentos que não está disponível.
 */
public class AgendaNaoDisponivelException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public AgendaNaoDisponivelException() {
        super("Agenda de pagamento nao esta disponivel");
    }
}