package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada quando a identificação do membro do sindicato não é fornecida.
 */
public class MembroNuloException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public MembroNuloException() {
        super("Identificacao do membro nao pode ser nula.");
    }
}