package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada quando o nome do banco não é fornecido.
 */
public class BancoNuloException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public BancoNuloException() {
        super("Banco nao pode ser nulo.");
    }
}