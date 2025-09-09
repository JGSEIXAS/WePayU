package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmpregadoService extends BaseService {
    private final CommandHistoryService commandHistoryService;

    public EmpregadoService(EmpregadoRepository repository, CommandHistoryService commandHistoryService) {
        super(repository);
        this.commandHistoryService = commandHistoryService;
    }

    // --- MÉTODOS DE CRIAÇÃO E REMOÇÃO ---
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

    public void removerEmpregado(String id) throws ValidacaoException, EmpregadoNaoExisteException {
        getEmpregadoValido(id);
        Map.Entry<Map<String, Empregado>, Integer> estadoAnterior = repository.getState();
        Runnable undoAction = () -> repository.setState(estadoAnterior);
        Runnable commandAction = () -> repository.deleteById(id);
        commandHistoryService.execute(commandAction, undoAction);
    }

    // --- MÉTODOS DE ALTERAÇÃO ---
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

    public int getNumeroDeEmpregados() {
        return repository.findAll().size();
    }

    // --- MÉTODOS PRIVADOS ---
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

    private void validarCamposBase(String n, String e, String s) throws ValidacaoException {
        if (n == null || n.isEmpty()) throw new NomeNuloException();
        if (e == null || e.isEmpty()) throw new EnderecoNuloException();
        validarSalario(s);
    }

    private void validarSalario(String s) throws ValidacaoException {
        if (s == null || s.isEmpty()) throw new SalarioNuloException();
        try {
            if (Double.parseDouble(s.replace(',', '.')) < 0) throw new SalarioNaoNegativoException();
        } catch (NumberFormatException ex) {
            throw new SalarioNumericoException();
        }
    }

    private void validarComissao(String comissao) throws ValidacaoException {
        if (comissao == null || comissao.isEmpty()) throw new ComissaoNulaException();
        try {
            if (Double.parseDouble(comissao.replace(',', '.')) < 0) throw new ComissaoNaoNegativaException();
        } catch (NumberFormatException ex) {
            throw new ComissaoNumericaException();
        }
    }
}