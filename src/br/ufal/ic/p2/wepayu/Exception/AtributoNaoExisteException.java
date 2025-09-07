package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

public class AtributoNaoExisteException extends ValidacaoException {
    public AtributoNaoExisteException() { super("Atributo nao existe."); }
}