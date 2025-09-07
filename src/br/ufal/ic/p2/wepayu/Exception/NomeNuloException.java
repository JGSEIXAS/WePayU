package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

public class NomeNuloException extends ValidacaoException {
    public NomeNuloException() { super("Nome nao pode ser nulo."); }
}