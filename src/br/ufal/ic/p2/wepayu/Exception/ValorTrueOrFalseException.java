package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada quando um valor que deveria ser 'true' ou 'false' não é.
 */
public class ValorTrueOrFalseException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public ValorTrueOrFalseException() {
        super("Valor deve ser true ou false.");
    }
}