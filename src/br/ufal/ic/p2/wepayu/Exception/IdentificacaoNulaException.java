package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

public class IdentificacaoNulaException extends ValidacaoException {
    public IdentificacaoNulaException() { super("Identificacao do empregado nao pode ser nula."); }
}