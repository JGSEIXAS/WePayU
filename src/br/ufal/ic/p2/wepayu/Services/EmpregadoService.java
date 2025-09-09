package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Serviço responsável por gerenciar a lógica de negócio relacionada a empregados.
 * Inclui operações de criação, remoção, alteração e consulta de dados de empregados,
 * utilizando o Command Pattern para suportar funcionalidades de undo/redo.
 */
public class EmpregadoService extends BaseService {
    private final CommandHistoryService commandHistoryService;

    /**
     * Constrói uma instância de EmpregadoService com as dependências necessárias.
     * @param repository O repositório para acesso aos dados dos empregados.
     * @param commandHistoryService O serviço de histórico de comandos para undo/redo.
     */
    public EmpregadoService(EmpregadoRepository repository, CommandHistoryService commandHistoryService) {
        super(repository);
        this.commandHistoryService = commandHistoryService;
    }

    // --- MÉTODOS DE CRIAÇÃO E REMOÇÃO ---

    /**
     * Cria um novo empregado horista ou assalariado.
     * @param nome Nome do empregado.
     * @param endereco Endereço do empregado.
     * @param tipo Tipo do empregado ("horista" ou "assalariado").
     * @param salario Salário por hora ou mensal.
     * @return O ID único do empregado criado.
     * @throws ValidacaoException Se os dados de entrada forem inválidos.
     * @throws EmpregadoNaoExisteException Se ocorrer um erro de referência a um empregado inexistente.
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws ValidacaoException, EmpregadoNaoExisteException {
        validarCamposBase(nome, endereco, salario);
        if ("comissionado".equals(tipo)) throw new TipoNaoAplicavelException();
        if (!"horista".equals(tipo) && !"assalariado".equals(tipo)) throw new TipoInvalidoException();
        Map.Entry<Map<String, Empregado>, Integer> estadoAnterior = repository.getState();
        Runnable undoAction = () -> repository.setState(estadoAnterior);
        String id = repository.getNextId();
        Empregado e = br.ufal.ic.p2.wepayu.models.factory.EmpregadoFactory.criarEmpregado(tipo, id, nome, endereco, salario);
        Runnable commandAction = () -> repository.save(e);
        commandHistoryService.execute(commandAction, undoAction);
        return id;
    }

    /**
     * Cria um novo empregado comissionado.
     * @param nome Nome do empregado.
     * @param endereco Endereço do empregado.
     * @param tipo Tipo do empregado (deve ser "comissionado").
     * @param salario Salário base mensal.
     * @param comissao Taxa de comissão sobre as vendas.
     * @return O ID único do empregado criado.
     * @throws ValidacaoException Se os dados de entrada forem inválidos.
     * @throws EmpregadoNaoExisteException Se ocorrer um erro de referência a um empregado inexistente.
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws ValidacaoException, EmpregadoNaoExisteException {
        validarCamposBase(nome, endereco, salario);
        validarComissao(comissao);
        if (!"comissionado".equals(tipo)) throw new TipoNaoAplicavelException();
        Map.Entry<Map<String, Empregado>, Integer> estadoAnterior = repository.getState();
        Runnable undoAction = () -> repository.setState(estadoAnterior);
        String id = repository.getNextId();
        Empregado e = br.ufal.ic.p2.wepayu.models.factory.EmpregadoFactory.criarEmpregado(tipo, id, nome, endereco, salario, comissao);
        Runnable commandAction = () -> repository.save(e);
        commandHistoryService.execute(commandAction, undoAction);
        return id;
    }

    /**
     * Remove um empregado do sistema com base no seu ID.
     * @param id O ID do empregado a ser removido.
     * @throws ValidacaoException Se o ID for nulo ou inválido.
     * @throws EmpregadoNaoExisteException Se nenhum empregado com o ID fornecido for encontrado.
     */
    public void removerEmpregado(String id) throws ValidacaoException, EmpregadoNaoExisteException {
        getEmpregadoValido(id);
        Map.Entry<Map<String, Empregado>, Integer> estadoAnterior = repository.getState();
        Runnable undoAction = () -> repository.setState(estadoAnterior);
        Runnable commandAction = () -> repository.deleteById(id);
        commandHistoryService.execute(commandAction, undoAction);
    }

    // --- MÉTODOS DE ALTERAÇÃO ---

    /**
     * Altera um atributo genérico de um empregado.
     * @param id O ID do empregado a ser alterado.
     * @param atributo O nome do atributo a ser modificado (ex: "nome", "endereco").
     * @param valor O novo valor para o atributo.
     * @throws ValidacaoException Se os dados de entrada forem inválidos.
     * @throws EmpregadoNaoExisteException Se o empregado não for encontrado.
     */
    public void alteraEmpregado(String id, String atributo, String valor) throws ValidacaoException, EmpregadoNaoExisteException {
        getEmpregadoValido(id);
        Map.Entry<Map<String, Empregado>, Integer> estadoAnterior = repository.getState();
        Runnable undoAction = () -> repository.setState(estadoAnterior);
        Runnable commandAction = () -> {
            try {
                Empregado empregado = repository.findById(id);
                switch (atributo.toLowerCase()) {
                    case "nome":
                        if (valor == null || valor.isEmpty()) throw new NomeNuloException();
                        empregado.setNome(valor);
                        break;
                    case "endereco":
                        if (valor == null || valor.isEmpty()) throw new EnderecoNuloException();
                        empregado.setEndereco(valor);
                        break;
                    case "salario":
                        validarSalario(valor);
                        empregado.setSalario(valor);
                        break;
                    case "tipo":
                        alterarTipo(id, valor, null, null);
                        break;
                    case "comissao":
                        if (!(empregado instanceof EmpregadoComissionado)) throw new EmpregadoNaoComissionadoException();
                        validarComissao(valor);
                        ((EmpregadoComissionado) empregado).setComissao(valor);
                        break;
                    case "metodopagamento":
                        if ("emmaos".equalsIgnoreCase(valor)) empregado.setMetodoPagamento(new EmMaos());
                        else if ("correios".equalsIgnoreCase(valor)) empregado.setMetodoPagamento(new Correios());
                        else if ("banco".equalsIgnoreCase(valor)) throw new DadosBancariosDevemSerFornecidosException();
                        else throw new MetodoPagamentoInvalidoException();
                        break;
                    case "sindicalizado":
                        if ("false".equalsIgnoreCase(valor)) empregado.setMembroSindicato(null);
                        else if ("true".equalsIgnoreCase(valor)) throw new IdSindicatoNuloException();
                        else throw new ValorTrueOrFalseException();
                        break;
                    default:
                        throw new AtributoNaoExisteException();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        commandHistoryService.execute(commandAction, undoAction);
    }

    /**
     * Altera o método de pagamento de um empregado para depósito bancário.
     * @param id O ID do empregado.
     * @param atributo O atributo a ser alterado (deve ser "metodoPagamento").
     * @param valor O novo método de pagamento (deve ser "banco").
     * @param banco O nome do banco.
     * @param agencia O número da agência.
     * @param contaCorrente O número da conta corrente.
     * @throws ValidacaoException Se os dados bancários forem inválidos.
     * @throws EmpregadoNaoExisteException Se o empregado não for encontrado.
     */
    public void alteraEmpregado(String id, String atributo, String valor, String banco, String agencia, String contaCorrente) throws ValidacaoException, EmpregadoNaoExisteException {
        getEmpregadoValido(id);
        Map.Entry<Map<String, Empregado>, Integer> estadoAnterior = repository.getState();
        Runnable undoAction = () -> repository.setState(estadoAnterior);
        Runnable commandAction = () -> {
            try {
                Empregado empregado = repository.findById(id);
                if ("metodopagamento".equalsIgnoreCase(atributo) && "banco".equalsIgnoreCase(valor)) {
                    if (banco == null || banco.isEmpty()) throw new BancoNuloException();
                    if (agencia == null || agencia.isEmpty()) throw new AgenciaNulaException();
                    if (contaCorrente == null || contaCorrente.isEmpty()) throw new ContaCorrenteNulaException();
                    empregado.setMetodoPagamento(new Banco(banco, agencia, contaCorrente));
                } else {
                    throw new AtributoNaoExisteException();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        commandHistoryService.execute(commandAction, undoAction);
    }

    /**
     * Altera o status de sindicalização de um empregado.
     * @param id O ID do empregado.
     * @param atributo O atributo a ser alterado (deve ser "sindicalizado").
     * @param status O novo status (true para sindicalizado, false para não sindicalizado).
     * @param idSindicato O ID do membro no sindicato (se status for true).
     * @param taxaSindical A taxa sindical a ser cobrada (se status for true).
     * @throws ValidacaoException Se os dados do sindicato forem inválidos.
     * @throws EmpregadoNaoExisteException Se o empregado não for encontrado.
     */
    public void alteraEmpregado(String id, String atributo, boolean status, String idSindicato, String taxaSindical) throws ValidacaoException, EmpregadoNaoExisteException {
        getEmpregadoValido(id);
        Map.Entry<Map<String, Empregado>, Integer> estadoAnterior = repository.getState();
        Runnable undoAction = () -> repository.setState(estadoAnterior);
        Runnable commandAction = () -> {
            try {
                Empregado empregado = repository.findById(id);
                if (!"sindicalizado".equalsIgnoreCase(atributo)) throw new AtributoNaoExisteException();
                if (status) {
                    if (idSindicato == null || idSindicato.isEmpty()) throw new IdSindicatoNuloException();
                    if (taxaSindical == null || taxaSindical.isEmpty()) throw new TaxaSindicalNulaException();
                    for (Empregado e : repository.findAll()) {
                        if (e.getId().equals(id)) continue;
                        if (e.isSindicalizado() && e.getMembroSindicato().getIdMembro().equals(idSindicato)) {
                            throw new SindicatoIdJaExisteException();
                        }
                    }
                    try {
                        double taxa = Double.parseDouble(taxaSindical.replace(',', '.'));
                        if (taxa < 0) throw new TaxaSindicalNaoNegativaException();
                        empregado.setMembroSindicato(new MembroSindicato(idSindicato, taxa));
                    } catch (NumberFormatException e) {
                        throw new TaxaSindicalNumericaException();
                    }
                } else {
                    empregado.setMembroSindicato(null);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        commandHistoryService.execute(commandAction, undoAction);
    }

    /**
     * Altera o tipo de um empregado, ajustando seu salário ou comissão.
     * @param id O ID do empregado.
     * @param atributo O atributo a ser alterado (deve ser "tipo").
     * @param tipo O novo tipo do empregado.
     * @param comissaoOuSalario O novo valor de comissão (se comissionado) ou salário (para outros tipos).
     * @throws ValidacaoException Se os dados forem inválidos.
     * @throws EmpregadoNaoExisteException Se o empregado não for encontrado.
     */
    public void alteraEmpregado(String id, String atributo, String tipo, String comissaoOuSalario) throws ValidacaoException, EmpregadoNaoExisteException {
        getEmpregadoValido(id);
        Map.Entry<Map<String, Empregado>, Integer> estadoAnterior = repository.getState();
        Runnable undoAction = () -> repository.setState(estadoAnterior);
        Runnable commandAction = () -> {
            try {
                if (!"tipo".equalsIgnoreCase(atributo)) throw new AtributoNaoExisteException();
                if ("comissionado".equalsIgnoreCase(tipo)) {
                    alterarTipo(id, tipo, comissaoOuSalario, null);
                } else {
                    alterarTipo(id, tipo, null, comissaoOuSalario);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        commandHistoryService.execute(commandAction, undoAction);
    }

    // --- MÉTODOS DE CONSULTA ---

    /**
     * Recupera o valor de um atributo específico de um empregado.
     * @param id O ID do empregado.
     * @param atributo O nome do atributo a ser recuperado.
     * @return Uma string representando o valor do atributo.
     * @throws ValidacaoException Se o atributo for inválido ou inacessível.
     * @throws EmpregadoNaoExisteException Se o empregado não for encontrado.
     */
    public String getAtributoEmpregado(String id, String atributo) throws ValidacaoException, EmpregadoNaoExisteException {
        Empregado empregado = getEmpregadoValido(id);
        return switch (atributo.toLowerCase()) {
            case "nome" -> empregado.getNome();
            case "endereco" -> empregado.getEndereco();
            case "tipo" -> empregado.getTipo();
            case "salario" -> empregado.getSalario();
            case "sindicalizado" -> String.valueOf(empregado.isSindicalizado());
            case "comissao" -> {
                if (empregado instanceof EmpregadoComissionado) yield ((EmpregadoComissionado) empregado).getComissao();
                throw new EmpregadoNaoComissionadoException();
            }
            case "metodopagamento" -> {
                MetodoPagamento metodo = empregado.getMetodoPagamento();
                if (metodo instanceof EmMaos) yield "emMaos";
                if (metodo instanceof Correios) yield "correios";
                if (metodo instanceof Banco) yield "banco";
                yield "";
            }
            case "banco", "agencia", "contacorrente" -> {
                if (empregado.getMetodoPagamento() instanceof Banco banco) {
                    yield switch (atributo.toLowerCase()) {
                        case "banco" -> banco.getBanco();
                        case "agencia" -> banco.getAgencia();
                        case "contacorrente" -> banco.getContaCorrente();
                        default -> "";
                    };
                }
                throw new EmpregadoNaoRecebeEmBancoException();
            }
            case "idsindicato", "taxasindical" -> {
                if (empregado.isSindicalizado()) {
                    MembroSindicato membro = empregado.getMembroSindicato();
                    yield switch (atributo.toLowerCase()) {
                        case "idsindicato" -> membro.getIdMembro();
                        case "taxasindical" -> String.format("%.2f", membro.getTaxaSindical()).replace('.', ',');
                        default -> "";
                    };
                }
                throw new EmpregadoNaoSindicalizadoException();
            }
            default -> throw new AtributoNaoExisteException();
        };
    }

    /**
     * Busca um empregado pelo nome e por um índice (para lidar com nomes duplicados).
     * @param nome O nome a ser buscado.
     * @param indice O índice do empregado na lista de resultados (1-based).
     * @return O ID do empregado encontrado.
     * @throws EmpregadoNaoExisteException Se nenhum empregado for encontrado com os critérios.
     */
    public String getEmpregadoPorNome(String nome, int indice) throws EmpregadoNaoExisteException, EmpregadoNaoEncontradoException {
        List<Empregado> empregadosEncontrados = new ArrayList<>();
        for (Empregado e : repository.findAll()) {
            if (e.getNome().equals(nome)) {
                empregadosEncontrados.add(e);
            }
        }
        int index = indice - 1;
        if (empregadosEncontrados.isEmpty() || index < 0 || index >= empregadosEncontrados.size()) {
            throw new EmpregadoNaoEncontradoException();
        }
        return empregadosEncontrados.get(index).getId();
    }

    /**
     * Retorna o número total de empregados cadastrados no sistema.
     * @return O total de empregados.
     */
    public int getNumeroDeEmpregados() {
        return repository.findAll().size();
    }

    // --- MÉTODOS PRIVADOS ---

    /**
     * Lógica interna para alterar o tipo de um empregado, recriando o objeto.
     */
    private void alterarTipo(String id, String novoTipo, String comissao, String novoSalario) throws ValidacaoException, EmpregadoNaoExisteException {
        Empregado eAntigo = getEmpregadoValido(id);
        String salario = (novoSalario != null && !novoSalario.isEmpty()) ? novoSalario : eAntigo.getSalarioSemFormato();
        Empregado eNovo;
        switch (novoTipo.toLowerCase()) {
            case "horista":
                validarSalario(salario);
                eNovo = new EmpregadoHorista(id, eAntigo.getNome(), eAntigo.getEndereco(), novoTipo, salario);
                break;
            case "assalariado":
                validarSalario(salario);
                eNovo = new EmpregadoAssalariado(id, eAntigo.getNome(), eAntigo.getEndereco(), novoTipo, salario);
                break;
            case "comissionado":
                if (comissao == null) throw new ComissaoNulaException();
                validarComissao(comissao);
                eNovo = new EmpregadoComissionado(id, eAntigo.getNome(), eAntigo.getEndereco(), novoTipo, salario, comissao);
                break;
            default:
                throw new TipoInvalidoException();
        }
        eNovo.setMembroSindicato(eAntigo.getMembroSindicato());
        eNovo.setMetodoPagamento(eAntigo.getMetodoPagamento());
        repository.save(eNovo);
    }

    /**
     * Valida os campos básicos de um empregado.
     */
    private void validarCamposBase(String n, String e, String s) throws ValidacaoException {
        if (n == null || n.isEmpty()) throw new NomeNuloException();
        if (e == null || e.isEmpty()) throw new EnderecoNuloException();
        validarSalario(s);
    }

    /**
     * Valida o formato e o valor do salário.
     */
    private void validarSalario(String s) throws ValidacaoException {
        if (s == null || s.isEmpty()) throw new SalarioNuloException();
        try {
            if (Double.parseDouble(s.replace(',', '.')) < 0) throw new SalarioNaoNegativoException();
        } catch (NumberFormatException ex) {
            throw new SalarioNumericoException();
        }
    }

    /**
     * Valida o formato e o valor da comissão.
     */
    private void validarComissao(String comissao) throws ValidacaoException {
        if (comissao == null || comissao.isEmpty()) throw new ComissaoNulaException();
        try {
            if (Double.parseDouble(comissao.replace(',', '.')) < 0) throw new ComissaoNaoNegativaException();
        } catch (NumberFormatException ex) {
            throw new ComissaoNumericaException();
        }
    }
}
