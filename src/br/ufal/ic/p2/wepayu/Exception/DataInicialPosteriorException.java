package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

/**
 * Exceção lançada quando a data inicial é posterior à data final em um período.
 */
public class DataInicialPosteriorException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public DataInicialPosteriorException() { super("Data inicial nao pode ser posterior aa data final."); }
}