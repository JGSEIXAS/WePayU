package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada quando o método de pagamento especificado é inválido.
 */
public class MetodoPagamentoInvalidoException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public MetodoPagamentoInvalidoException() {
        super("Metodo de pagamento invalido.");
    }
}