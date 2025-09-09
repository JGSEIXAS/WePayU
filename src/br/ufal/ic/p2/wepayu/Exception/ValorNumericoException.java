package br.ufal.ic.p2.wepayu.Exception;

public class ValorNumericoException extends ValidacaoException {
    public ValorNumericoException() {
        super("Valor deve ser numerico.");
    }
}