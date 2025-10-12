package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada quando já existe outro empregado com a mesma identificação de sindicato.
 */
public class SindicatoIdJaExisteException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public SindicatoIdJaExisteException() {
        super("Ha outro empregado com esta identificacao de sindicato");
    }
}