package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada quando a identificação do sindicato não é fornecida.
 */
public class IdSindicatoNuloException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public IdSindicatoNuloException() {
        super("Identificacao do sindicato nao pode ser nula.");
    }
}