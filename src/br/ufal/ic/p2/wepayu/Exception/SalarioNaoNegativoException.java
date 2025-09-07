package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

public class SalarioNaoNegativoException extends ValidacaoException {
    public SalarioNaoNegativoException() { super("Salario deve ser nao-negativo."); }
}