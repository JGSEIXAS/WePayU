// ValidacaoException.java
package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção base para erros de validação no sistema.
 */
public class ValidacaoException extends Exception {
    /**
     * Construtor que recebe uma mensagem de erro.
     * @param message A mensagem de erro.
     */
    public ValidacaoException(String message) {
        super(message);
    }
}