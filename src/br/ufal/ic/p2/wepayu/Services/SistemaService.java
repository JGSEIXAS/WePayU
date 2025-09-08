package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;
import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;
import br.ufal.ic.p2.wepayu.models.Empregado;

import java.util.Map;

public class SistemaService extends br.ufal.ic.p2.wepayu.Services.BaseService {

    private final CommandHistoryService commandHistoryService;

    public SistemaService(EmpregadoRepository repository, CommandHistoryService commandHistoryService) {
        super(repository);
        this.commandHistoryService = commandHistoryService;
    }


    public void zerarSistema() throws ValidacaoException, EmpregadoNaoExisteException {
        Map.Entry<Map<String, Empregado>, Integer> estadoAnterior = repository.getState();
        Runnable undoAction = () -> repository.setState(estadoAnterior);
        commandHistoryService.execute(repository::zerarDados, undoAction);
    }

    public void encerrarSistema() {
        repository.salvarDados();
    }
}