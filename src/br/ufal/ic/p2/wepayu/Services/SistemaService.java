package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;
import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;
import br.ufal.ic.p2.wepayu.models.Empregado;

import java.util.Map;

/**
 * Serviço responsável por operações globais do sistema, como zerar e encerrar.
 */
public class SistemaService extends BaseService {

    private final CommandHistoryService commandHistoryService;

    /**
     * Constrói uma instância de SistemaService.
     * @param repository O repositório para acesso aos dados.
     * @param commandHistoryService O serviço de histórico de comandos.
     */
    public SistemaService(EmpregadoRepository repository, CommandHistoryService commandHistoryService) {
        super(repository);
        this.commandHistoryService = commandHistoryService;
    }

    /**
     * Zera o estado do sistema, removendo todos os dados e resetando as agendas de pagamento.
     * @throws ValidacaoException se ocorrer um erro de validação.
     * @throws EmpregadoNaoExisteException se um empregado esperado não for encontrado.
     */
    public void zerarSistema() throws ValidacaoException, EmpregadoNaoExisteException {
        Map.Entry<Map<String, Empregado>, Integer> estadoAnterior = repository.getState();
        Runnable undoAction = () -> repository.setState(estadoAnterior);

        Runnable commandAction = () -> {
            repository.zerarDados();
            EmpregadoService.resetAgendasDisponiveis();
        };

        commandHistoryService.execute(commandAction, undoAction);
    }

    /**
     * Encerra o sistema, salvando os dados.
     */
    public void encerrarSistema() {
        repository.salvarDados();
    }
}