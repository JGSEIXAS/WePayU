package br.ufal.ic.p2.wepayu.Exception;

public class SindicatoIdJaExisteException extends ValidacaoException {
    public SindicatoIdJaExisteException() {
        super("Ha outro empregado com esta identificacao de sindicato");
    }
}