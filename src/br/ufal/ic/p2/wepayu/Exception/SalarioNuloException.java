package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

public class SalarioNuloException extends ValidacaoException {
    public SalarioNuloException() { super("Salario nao pode ser nulo."); }
}