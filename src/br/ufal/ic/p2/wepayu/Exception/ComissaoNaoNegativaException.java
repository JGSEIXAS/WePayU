package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

public class ComissaoNaoNegativaException extends ValidacaoException {
    public ComissaoNaoNegativaException() { super("Comissao deve ser nao-negativa."); }
}