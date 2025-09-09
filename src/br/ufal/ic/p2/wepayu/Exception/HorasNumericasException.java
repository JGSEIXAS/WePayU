package br.ufal.ic.p2.wepayu.Exception;

public class HorasNumericasException extends ValidacaoException {
    public HorasNumericasException() {
        super("Horas deve ser um valor numerico.");
    }
}