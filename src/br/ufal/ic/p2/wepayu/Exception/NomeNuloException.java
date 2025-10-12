package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

/**
 * Exceção lançada quando o nome do empregado não é fornecido.
 */
public class NomeNuloException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public NomeNuloException() { super("Nome nao pode ser nulo."); }
}