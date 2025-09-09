package br.ufal.ic.p2.wepayu.Exception;

public class TaxaSindicalNumericaException extends ValidacaoException {
    public TaxaSindicalNumericaException() {
        super("Taxa sindical deve ser numerica.");
    }
}