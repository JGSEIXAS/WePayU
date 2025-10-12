package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada quando um valor que deveria ser numérico não é.
 */
public class ValorNumericoException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public ValorNumericoException() {
        super("Valor deve ser numerico.");
    }
}