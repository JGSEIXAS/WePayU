package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada quando dados bancários são necessários mas não foram fornecidos.
 */
public class DadosBancariosDevemSerFornecidosException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public DadosBancariosDevemSerFornecidosException() {
        super("Dados bancarios devem ser fornecidos.");
    }
}