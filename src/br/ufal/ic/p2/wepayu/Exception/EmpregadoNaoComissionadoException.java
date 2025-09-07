package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

public class EmpregadoNaoComissionadoException extends ValidacaoException {
    public EmpregadoNaoComissionadoException() { super("Empregado nao eh comissionado."); }
}