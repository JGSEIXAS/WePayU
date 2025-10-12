package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada ao tentar criar uma agenda de pagamentos que já existe.
 */
public class AgendaJaExisteException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public AgendaJaExisteException() {
        super("Agenda de pagamentos ja existe");
    }
}