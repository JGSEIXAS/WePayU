package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;
import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;
import br.ufal.ic.p2.wepayu.models.Empregado;

import java.util.Map;

/**
 * Serviço responsável por gerenciar operações globais do sistema,
 * como zerar e encerrar o estado da aplicação.
 */
public class SistemaService extends BaseService {

    private final CommandHistoryService commandHistoryService;

    /**
     * Constrói uma instância de SistemaService com as dependências necessárias.
     * @param repository O repositório para acesso aos dados dos empregados.
     * @param commandHistoryService O serviço de histórico de comandos para undo/redo.
     */
    public SistemaService(EmpregadoRepository repository, CommandHistoryService commandHistoryService) {
        super(repository);
        this.commandHistoryService = commandHistoryService;
    }

    /**
     * Limpa todos os dados de empregados do sistema, resetando-o para um estado inicial.
     * Esta operação pode ser desfeita (undo).
     * @throws ValidacaoException Se ocorrer um erro de validação durante a operação.
     * @throws EmpregadoNaoExisteException Se ocorrer um erro de referência a um empregado inexistente.
     */
    public void zerarSistema() throws ValidacaoException, EmpregadoNaoExisteException {
        Map.Entry<Map<String, Empregado>, Integer> estadoAnterior = repository.getState();
        Runnable undoAction = () -> repository.setState(estadoAnterior);
        commandHistoryService.execute(repository::zerarDados, undoAction);
    }

    /**
     * Salva o estado atual do sistema em um arquivo persistente.
     * Esta é a última operação que deve ser chamada antes de fechar a aplicação.
     */
    public void encerrarSistema() {
        repository.salvarDados();
    }
}
