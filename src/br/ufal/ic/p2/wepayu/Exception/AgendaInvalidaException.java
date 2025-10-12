package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada quando uma descrição de agenda de pagamentos é inválida.
 */
public class AgendaInvalidaException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public AgendaInvalidaException() {
        super("Descricao de agenda invalida");
    }
}