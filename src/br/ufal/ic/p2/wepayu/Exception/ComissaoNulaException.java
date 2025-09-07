package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

public class ComissaoNulaException extends ValidacaoException {
    public ComissaoNulaException() { super("Comissao nao pode ser nula."); }
}