package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada quando a taxa sindical não é um valor numérico.
 */
public class TaxaSindicalNumericaException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public TaxaSindicalNumericaException() {
        super("Taxa sindical deve ser numerica.");
    }
}