package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

public class TipoNaoAplicavelException extends ValidacaoException {
    public TipoNaoAplicavelException() { super("Tipo nao aplicavel."); }
}