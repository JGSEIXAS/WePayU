package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada quando as horas trabalhadas não são um valor numérico.
 */
public class HorasNumericasException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public HorasNumericasException() {
        super("Horas deve ser um valor numerico.");
    }
}