package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada quando a taxa sindical não é fornecida.
 */
public class TaxaSindicalNulaException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public TaxaSindicalNulaException() {
        super("Taxa sindical nao pode ser nula.");
    }
}