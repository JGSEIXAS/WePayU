package br.ufal.ic.p2.wepayu.Exception;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;

/**
 * Exceção lançada quando o endereço não é fornecido.
 */
public class EnderecoNuloException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public EnderecoNuloException() { super("Endereco nao pode ser nulo."); }
}