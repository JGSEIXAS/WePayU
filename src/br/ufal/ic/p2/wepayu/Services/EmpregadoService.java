package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;
import br.ufal.ic.p2.wepayu.models.EmpregadoFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

/**
 * Serviço responsável pela lógica de negócio relacionada a empregados.
 * Inclui criação, remoção, alteração e consulta de dados de empregados.
 */
public class EmpregadoService extends BaseService {
    private final CommandHistoryService commandHistoryService;
    // CORREÇÃO: A lista agora não é final para permitir o reset.
    private static List<String> agendasDisponiveis = new ArrayList<>(Arrays.asList("semanal 5", "mensal $", "semanal 2 5"));

    /**
     * Constrói uma instância de EmpregadoService.
     * @param repository O repositório para acesso aos dados.
     * @param commandHistoryService O serviço de histórico de comandos.
     */
    public EmpregadoService(EmpregadoRepository repository, CommandHistoryService commandHistoryService) {
        super(repository);
        this.commandHistoryService = commandHistoryService;
    }

    /**
     * CORREÇÃO: Novo método estático para resetar a lista de agendas.
     */
    public static void resetAgendasDisponiveis() {
        agendasDisponiveis.clear();
        agendasDisponiveis.addAll(Arrays.asList("semanal 5", "mensal $", "semanal 2 5"));
    }

    // ... (resto do código da classe permanece o mesmo) ...
    /**
     * Cria um novo empregado (horista ou assalariado).
     * @param nome Nome do empregado.
     * @param endereco Endereço do empregado.
     * @param tipo Tipo do empregado.
     * @param salario Salário ou valor da hora.
     * @return O ID do novo empregado.
     * @throws ValidacaoException se os dados forem inválidos.
     * @throws EmpregadoNaoExisteException se ocorrer um erro inesperado.
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws ValidacaoException, EmpregadoNaoExisteException {
        validarCamposBase(nome, endereco, salario);
        if ("comissionado".equals(tipo)) throw new TipoNaoAplicavelException();
        if (!"horista".equals(tipo) && !"assalariado".equals(tipo)) throw new TipoInvalidoException();
        Map.Entry<Map<String, Empregado>, Integer> estadoAnterior = repository.getState();
        Runnable undoAction = () -> repository.setState(estadoAnterior);
        String id = repository.getNextId();
        Empregado e = EmpregadoFactory.criarEmpregado(tipo, id, nome, endereco, salario);
        Runnable commandAction = () -> repository.save(e);
        commandHistoryService.execute(commandAction, undoAction);
        return id;
    }

    /**
     * Cria um novo empregado comissionado.
     * @param nome Nome do empregado.
     * @param endereco Endereço do empregado.
     * @param tipo Tipo do empregado (deve ser "comissionado").
     * @param salario Salário base.
     * @param comissao Taxa de comissão.
     * @return O ID do novo empregado.
     * @throws ValidacaoException se os dados forem inválidos.
     * @throws EmpregadoNaoExisteException se ocorrer um erro inesperado.
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws ValidacaoException, EmpregadoNaoExisteException {
        validarCamposBase(nome, endereco, salario);
        validarComissao(comissao);
        if (!"comissionado".equals(tipo)) throw new TipoNaoAplicavelException();
        Map.Entry<Map<String, Empregado>, Integer> estadoAnterior = repository.getState();
        Runnable undoAction = () -> repository.setState(estadoAnterior);
        String id = repository.getNextId();
        Empregado e = EmpregadoFactory.criarEmpregado(tipo, id, nome, endereco, salario, comissao);
        Runnable commandAction = () -> repository.save(e);
        commandHistoryService.execute(commandAction, undoAction);
        return id;
    }

    /**
     * Cria uma nova agenda de pagamentos.
     * @param descricao A descrição da agenda a ser criada.
     * @throws ValidacaoException se a descrição for inválida ou a agenda já existir.
     */
    public void criarAgendaDePagamentos(String descricao) throws ValidacaoException {
        String[] parts = descricao.split(" ");
        if (parts.length < 1 || parts.length > 3) throw new AgendaInvalidaException();

        String tipo = parts[0];
        try {
            if (tipo.equalsIgnoreCase("mensal")) {
                if (parts.length != 2) throw new AgendaInvalidaException();
                if (!parts[1].equals("$")) {
                    int dia = Integer.parseInt(parts[1]);
                    if (dia < 1 || dia > 28) throw new AgendaInvalidaException();
                }
            } else if (tipo.equalsIgnoreCase("semanal")) {
                if (parts.length == 2) {
                    int diaSemana = Integer.parseInt(parts[1]);
                    if (diaSemana < 1 || diaSemana > 7) throw new AgendaInvalidaException();
                } else if (parts.length == 3) {
                    int freq = Integer.parseInt(parts[1]);
                    int diaSemana = Integer.parseInt(parts[2]);
                    if (freq < 1 || freq > 52 || diaSemana < 1 || diaSemana > 7) throw new AgendaInvalidaException();
                } else {
                    throw new AgendaInvalidaException();
                }
            } else {
                throw new AgendaInvalidaException();
            }
        } catch (NumberFormatException e) {
            throw new AgendaInvalidaException();
        }

        if (agendasDisponiveis.contains(descricao)) {
            throw new AgendaJaExisteException();
        }
        agendasDisponiveis.add(descricao);
    }

    /**
     * Remove um empregado do sistema.
     * @param id O ID do empregado a ser removido.
     * @throws ValidacaoException se o ID for inválido.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     */
    public void removerEmpregado(String id) throws ValidacaoException, EmpregadoNaoExisteException {
        getEmpregadoValido(id);
        Map.Entry<Map<String, Empregado>, Integer> estadoAnterior = repository.getState();
        Runnable undoAction = () -> repository.setState(estadoAnterior);
        Runnable commandAction = () -> repository.deleteById(id);
        commandHistoryService.execute(commandAction, undoAction);
    }

    /**
     * Altera um atributo de um empregado.
     * @param id O ID do empregado.
     * @param atributo O atributo a ser alterado.
     * @param valor O novo valor do atributo.
     * @throws ValidacaoException se os dados forem inválidos.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
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
                    case "agendapagamento":
                        if (!agendasDisponiveis.contains(valor)) {
                            throw new AgendaNaoDisponivelException();
                        }
                        empregado.setAgendaPagamento(new AgendaPagamento(valor));
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
     * Altera o método de pagamento de um empregado para banco.
     * @param id O ID do empregado.
     * @param atributo O atributo (deve ser "metodoPagamento").
     * @param valor O valor (deve ser "banco").
     * @param banco O nome do banco.
     * @param agencia O número da agência.
     * @param contaCorrente O número da conta corrente.
     * @throws ValidacaoException se os dados forem inválidos.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
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
     * @param atributo O atributo (deve ser "sindicalizado").
     * @param status O novo status de sindicalização.
     * @param idSindicato O ID do sindicato.
     * @param taxaSindical A taxa sindical.
     * @throws ValidacaoException se os dados forem inválidos.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
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
     * Altera o tipo de um empregado.
     * @param id O ID do empregado.
     * @param atributo O atributo (deve ser "tipo").
     * @param tipo O novo tipo do empregado.
     * @param comissaoOuSalario A nova comissão ou salário.
     * @throws ValidacaoException se os dados forem inválidos.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
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

    /**
     * Obtém o valor de um atributo de um empregado.
     * @param id O ID do empregado.
     * @param atributo O atributo a ser consultado.
     * @return O valor do atributo.
     * @throws ValidacaoException se os dados forem inválidos.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     */
    public String getAtributoEmpregado(String id, String atributo) throws ValidacaoException, EmpregadoNaoExisteException {
        Empregado empregado = getEmpregadoValido(id);
        return switch (atributo.toLowerCase()) {
            case "nome" -> empregado.getNome();
            case "endereco" -> empregado.getEndereco();
            case "tipo" -> empregado.getTipo();
            case "salario" -> empregado.getSalario();
            case "sindicalizado" -> String.valueOf(empregado.isSindicalizado());
            case "agendapagamento" -> empregado.getAgendaPagamento().getDescricao();
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
     * Busca um empregado pelo nome e índice.
     * @param nome O nome do empregado.
     * @param indice O índice (para casos de nomes duplicados).
     * @return O ID do empregado encontrado.
     * @throws EmpregadoNaoEncontradoException se o empregado não for encontrado.
     */
    public String getEmpregadoPorNome(String nome, int indice) throws EmpregadoNaoEncontradoException {
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
     * Retorna o número total de empregados.
     * @return O número de empregados.
     */
    public int getNumeroDeEmpregados() {
        return repository.findAll().size();
    }

    /**
     * Altera o tipo de um empregado.
     * @param id O ID do empregado.
     * @param novoTipo O novo tipo.
     * @param comissao A nova comissão (se aplicável).
     * @param novoSalario O novo salário.
     * @throws ValidacaoException se os dados forem inválidos.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
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
     * Valida os campos base de um empregado.
     * @param n Nome.
     * @param e Endereço.
     * @param s Salário.
     * @throws ValidacaoException se algum campo for inválido.
     */
    private void validarCamposBase(String n, String e, String s) throws ValidacaoException {
        if (n == null || n.isEmpty()) throw new NomeNuloException();
        if (e == null || e.isEmpty()) throw new EnderecoNuloException();
        validarSalario(s);
    }

    /**
     * Valida o salário.
     * @param s Salário.
     * @throws ValidacaoException se o salário for inválido.
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
     * Valida a comissão.
     * @param comissao A comissão.
     * @throws ValidacaoException se a comissão for inválida.
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