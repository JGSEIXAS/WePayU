package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;
import java.util.Stack;

/**
 * Serviço que implementa o padrão de projeto Command para gerenciar o histórico de operações.
 * Permite que as ações que modificam o estado do sistema sejam desfeitas (undo) e refeitas (redo).
 * Utiliza duas pilhas para manter o controle dos comandos executados e desfeitos.
 */
public class CommandHistoryService {

    /**
     * Interface interna que define o contrato para um comando executável e reversível.
     */
    private interface Command {
        /**
         * Executa a ação principal do comando.
         */
        void execute();

        /**
         * Reverte a ação executada pelo comando.
         */
        void undo();
    }

    /**
     * Pilha de comandos que foram executados e podem ser desfeitos.
     */
    private final Stack<Command> undoStack = new Stack<>();

    /**
     * Pilha de comandos que foram desfeitos e podem ser refeitos.
     */
    private final Stack<Command> redoStack = new Stack<>();

    /**
     * Executa uma nova ação, encapsulando-a em um objeto Command e a adicionando ao histórico de undo.
     * Se a ação for executada com sucesso, a pilha de redo é limpa.
     * Se a ação falhar, a exceção original é propagada e o comando não é adicionado ao histórico.
     *
     * @param commandAction A ação a ser executada (lógica principal).
     * @param undoAction A ação que desfaz a ação principal.
     * @throws ValidacaoException Se a ação principal lançar uma exceção de validação.
     * @throws EmpregadoNaoExisteException Se a ação principal lançar uma exceção de empregado não existente.
     */
    public void execute(Runnable commandAction, Runnable undoAction) throws ValidacaoException, EmpregadoNaoExisteException {
        Command command = new Command() {
            @Override
            public void execute() {
                commandAction.run();
            }

            @Override
            public void undo() {
                undoAction.run();
            }
        };

        try {
            command.execute();
            undoStack.push(command); // Apenas adiciona à pilha se a execução for bem-sucedida
            redoStack.clear();
        } catch (Exception e) {
            // Desembrulha a exceção original se ela foi encapsulada em uma RuntimeException
            Throwable cause = e.getCause();
            if (e instanceof RuntimeException && cause != null) {
                if (cause instanceof ValidacaoException) {
                    throw (ValidacaoException) cause;
                }
                if (cause instanceof EmpregadoNaoExisteException) {
                    throw (EmpregadoNaoExisteException) cause;
                }
            }
            // Relança a exceção original se não for encapsulada, ou a RuntimeException
            throw e;
        }
    }

    /**
     * Desfaz o último comando executado.
     * Move o comando desfeito da pilha de undo para a de redo.
     *
     * @throws ValidacaoException Se não houver comandos para desfazer.
     */
    public void undo() throws ValidacaoException {
        if (undoStack.isEmpty()) {
            throw new ValidacaoException("Nao ha comando a desfazer.");
        }
        Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);
    }

    /**
     * Refaz o último comando que foi desfeito.
     * Move o comando refeito da pilha de redo para a de undo.
     *
     * @throws ValidacaoException Se não houver comandos para refazer.
     */
    public void redo() throws ValidacaoException {
        if (redoStack.isEmpty()) {
            throw new ValidacaoException("Nao ha comando a refazer.");
        }
        Command command = redoStack.pop();
        command.execute();
        undoStack.push(command);
    }
}