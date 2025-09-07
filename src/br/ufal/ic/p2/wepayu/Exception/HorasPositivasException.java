package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

public class HorasPositivasException extends ValidacaoException {
    public HorasPositivasException() { super("Horas devem ser positivas."); }
}