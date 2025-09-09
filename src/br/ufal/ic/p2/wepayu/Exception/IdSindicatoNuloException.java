package br.ufal.ic.p2.wepayu.Exception;

public class IdSindicatoNuloException extends ValidacaoException {
    public IdSindicatoNuloException() {
        super("Identificacao do sindicato nao pode ser nula.");
    }
}