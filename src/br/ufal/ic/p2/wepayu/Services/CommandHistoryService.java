package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;
import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;

import java.util.Stack;

public class CommandHistoryService {
    private final Stack<Runnable> undoStack = new Stack<>();
    private final Stack<Runnable> redoStack = new Stack<>();

    public void execute(Runnable command, Runnable undoCommand) {
        command.run();
        undoStack.push(undoCommand);
        redoStack.clear(); // Any new action clears the redo stack
    }

    public void undo() throws ValidacaoException {
        if (undoStack.isEmpty()) {
            throw new ValidacaoException("Nao ha comando a desfazer.");
        }
        Runnable undoCommand = undoStack.pop();

        undoCommand.run();
    }

    public void redo() throws ValidacaoException {
        if (redoStack.isEmpty()) {
            throw new ValidacaoException("Nao ha comando a refazer.");
        }
        Runnable redoCommand = redoStack.pop();
        redoCommand.run();
    }

    public void pushRedo(Runnable redoAction) {
        redoStack.push(redoAction);
    }
    }