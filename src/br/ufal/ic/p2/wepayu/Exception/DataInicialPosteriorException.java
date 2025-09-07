package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

public class DataInicialPosteriorException extends ValidacaoException {
    public DataInicialPosteriorException() { super("Data inicial nao pode ser posterior aa data final."); }
}