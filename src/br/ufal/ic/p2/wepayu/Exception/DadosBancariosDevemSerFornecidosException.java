package br.ufal.ic.p2.wepayu.Exception;

public class DadosBancariosDevemSerFornecidosException extends ValidacaoException {
    public DadosBancariosDevemSerFornecidosException() {
        super("Dados bancarios devem ser fornecidos.");
    }
}