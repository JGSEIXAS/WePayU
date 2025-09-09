package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;
import java.util.Stack;

public class CommandHistoryService {

    private interface Command {
        void execute();
        void undo();
    }

    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();

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
            // LÓGICA CORRIGIDA E FINAL
            Throwable cause = e.getCause();
            if (e instanceof RuntimeException && cause != null) {
                if (cause instanceof ValidacaoException) {
                    throw (ValidacaoException) cause;
                }
                if (cause instanceof EmpregadoNaoExisteException) {
                    throw (EmpregadoNaoExisteException) cause;
                }
            }
            throw e;
        }
    }

    public void undo() throws ValidacaoException {
        if (undoStack.isEmpty()) {
            throw new ValidacaoException("Nao ha comando a desfazer.");
        }
        Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);
    }

    public void redo() throws ValidacaoException {
        if (redoStack.isEmpty()) {
            throw new ValidacaoException("Nao ha comando a refazer.");
        }
        Command command = redoStack.pop();
        command.execute();
        undoStack.push(command);
    }
}