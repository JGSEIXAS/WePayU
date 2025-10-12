package br.ufal.ic.p2.wepayu.Exception;

/**
 * Exceção lançada ao tentar executar um comando após o sistema ter sido encerrado.
 */
public class SistemaEncerradoException extends ValidacaoException {
    /**
     * Construtor que define a mensagem de erro padrão.
     */
    public SistemaEncerradoException() {
        super("Nao pode dar comandos depois de encerrarSistema.");
    }
}