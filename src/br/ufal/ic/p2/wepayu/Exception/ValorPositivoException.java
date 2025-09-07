package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

public class ValorPositivoException extends ValidacaoException {
    public ValorPositivoException() { super("Valor deve ser positivo."); }
}