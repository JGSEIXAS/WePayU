package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

public class SalarioNumericoException extends ValidacaoException {
    public SalarioNumericoException() { super("Salario deve ser numerico."); }
}