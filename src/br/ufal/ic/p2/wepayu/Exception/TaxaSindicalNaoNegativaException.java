package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada quando a taxa sindical é um valor negativo.
 */
public class TaxaSindicalNaoNegativaException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public TaxaSindicalNaoNegativaException() {
        super("Taxa sindical deve ser nao-negativa.");
    }
}