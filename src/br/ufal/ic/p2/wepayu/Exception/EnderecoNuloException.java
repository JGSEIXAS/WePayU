package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

public class EnderecoNuloException extends ValidacaoException {
    public EnderecoNuloException() { super("Endereco nao pode ser nulo."); }
}