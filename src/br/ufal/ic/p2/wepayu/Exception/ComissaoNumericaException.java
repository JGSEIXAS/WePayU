package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

public class ComissaoNumericaException extends ValidacaoException {
    public ComissaoNumericaException() { super("Comissao deve ser numerica."); }
}