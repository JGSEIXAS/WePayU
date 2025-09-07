package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

public class EmpregadoNaoHoristaException extends ValidacaoException {
    public EmpregadoNaoHoristaException() { super("Empregado nao eh horista."); }
}
