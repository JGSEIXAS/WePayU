package br.ufal.ic.p2.wepayu.Exception;

public class ValorTrueOrFalseException extends ValidacaoException {
    public ValorTrueOrFalseException() {
        super("Valor deve ser true ou false.");
    }
}