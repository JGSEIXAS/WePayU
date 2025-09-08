package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;
import java.util.ArrayList;
import java.util.List;

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

        String id = repository.getNextId();
        Empregado e = "horista".equals(tipo)
                ? new EmpregadoHorista(id, nome, endereco, tipo, salario)
                : new EmpregadoAssalariado(id, nome, endereco, tipo, salario);

        Runnable command = () -> repository.save(e);
        Runnable undo = () -> repository.deleteById(id);
        commandHistoryService.execute(command, undo);
        return id;
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws ValidacaoException, EmpregadoNaoExisteException {
        validarCamposBase(nome, endereco, salario);
        validarComissao(comissao);
        if (!"comissionado".equals(tipo)) throw new TipoNaoAplicavelException();

        String id = repository.getNextId();
        Empregado e = new EmpregadoComissionado(id, nome, endereco, tipo, salario, comissao);

        Runnable command = () -> repository.save(e);
        Runnable undo = () -> repository.deleteById(id);
        commandHistoryService.execute(command, undo);
        return id;
    }

    public void removerEmpregado(String id) throws ValidacaoException, EmpregadoNaoExisteException {
        Empregado empregado = getEmpregadoValido(id);
        Empregado empregadoOriginal = empregado.clone();

        Runnable command = () -> repository.deleteById(id);
        Runnable undo = () -> repository.save(empregadoOriginal);
        commandHistoryService.execute(command, undo);
    }

    // --- MÉTODOS DE ALTERAÇÃO (AGORA TOTALMENTE INTEGRADOS) ---
    public void alteraEmpregado(String id, String atributo, String valor) throws ValidacaoException, EmpregadoNaoExisteException {
        Empregado empregadoOriginal = getEmpregadoValido(id).clone();
        Runnable undoAction = () -> repository.save(empregadoOriginal);

        Runnable commandAction = () -> {
            try {
                Empregado empregado = getEmpregadoValido(id);
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
                    case "comissao":
                        if (!(empregado instanceof EmpregadoComissionado)) throw new EmpregadoNaoComissionadoException();
                        validarComissao(valor);
                        ((EmpregadoComissionado) empregado).setComissao(valor);
                        break;
                    case "metodopagamento":
                        if ("emmaos".equalsIgnoreCase(valor)) empregado.setMetodoPagamento(new EmMaos());
                        else if ("correios".equalsIgnoreCase(valor)) empregado.setMetodoPagamento(new Correios());
                        else if ("banco".equalsIgnoreCase(valor)) throw new ValidacaoException("Dados bancarios devem ser fornecidos.");
                        else throw new ValidacaoException("Metodo de pagamento invalido.");
                        break;
                    case "sindicalizado":
                        if ("false".equalsIgnoreCase(valor)) {
                            empregado.setMembroSindicato(null);
                        } else if ("true".equalsIgnoreCase(valor)) {
                            throw new ValidacaoException("Identificacao do sindicato nao pode ser nula.");
                        } else {
                            throw new ValidacaoException("Valor deve ser true ou false.");
                        }
                        break;
                    default:
                        throw new AtributoNaoExisteException();
                }
            } catch (ValidacaoException | EmpregadoNaoExisteException e) {
                throw new RuntimeException(e);
            }
        };
        commandHistoryService.execute(commandAction, undoAction);
    }


    public void alteraEmpregado(String id, String atributo, String valor, String banco, String agencia, String contaCorrente) throws ValidacaoException, EmpregadoNaoExisteException {
        Empregado empregadoOriginal = getEmpregadoValido(id).clone();
        Runnable undoAction = () -> repository.save(empregadoOriginal);

        Runnable commandAction = () -> {
            try {
                Empregado empregado = getEmpregadoValido(id);
                if ("metodopagamento".equalsIgnoreCase(atributo) && "banco".equalsIgnoreCase(valor)) {
                    if (banco == null || banco.isEmpty()) throw new ValidacaoException("Banco nao pode ser nulo.");
                    if (agencia == null || agencia.isEmpty()) throw new ValidacaoException("Agencia nao pode ser nulo.");
                    if (contaCorrente == null || contaCorrente.isEmpty()) throw new ValidacaoException("Conta corrente nao pode ser nulo.");
                    empregado.setMetodoPagamento(new Banco(banco, agencia, contaCorrente));
                } else {
                    throw new AtributoNaoExisteException();
                }
            } catch (ValidacaoException | EmpregadoNaoExisteException e) {
                throw new RuntimeException(e);
            }
        };
        commandHistoryService.execute(commandAction, undoAction);
    }

    public void alteraEmpregado(String id, String atributo, boolean status, String idSindicato, String taxaSindical) throws ValidacaoException, EmpregadoNaoExisteException {
        Empregado empregadoOriginal = getEmpregadoValido(id).clone();
        Runnable undoAction = () -> repository.save(empregadoOriginal);

        Runnable commandAction = () -> {
            try {
                Empregado empregado = getEmpregadoValido(id);
                if (!"sindicalizado".equalsIgnoreCase(atributo)) throw new AtributoNaoExisteException();

                if (status) {
                    if (idSindicato == null || idSindicato.isEmpty()) throw new ValidacaoException("Identificacao do sindicato nao pode ser nula.");
                    if (taxaSindical == null || taxaSindical.isEmpty()) throw new ValidacaoException("Taxa sindical nao pode ser nula.");

                    for (Empregado e : repository.findAll()) {
                        if (e.getId().equals(id)) continue;
                        if (e.isSindicalizado() && e.getMembroSindicato().getIdMembro().equals(idSindicato)) {
                            throw new ValidacaoException("Ha outro empregado com esta identificacao de sindicato");
                        }
                    }
                    double taxa = Double.parseDouble(taxaSindical.replace(',', '.'));
                    if (taxa < 0) throw new ValidacaoException("Taxa sindical deve ser nao-negativa.");
                    empregado.setMembroSindicato(new MembroSindicato(idSindicato, taxa));
                } else {
                    empregado.setMembroSindicato(null);
                }
            } catch (ValidacaoException | EmpregadoNaoExisteException | NumberFormatException e) {
                throw new RuntimeException(e);
            }
        };
        commandHistoryService.execute(commandAction, undoAction);
    }

    public void alteraEmpregado(String id, String atributo, String tipo, String comissaoOuSalario) throws ValidacaoException, EmpregadoNaoExisteException {
        Empregado empregadoOriginal = getEmpregadoValido(id).clone();
        Runnable undoAction = () -> repository.save(empregadoOriginal);

        Runnable commandAction = () -> {
            try {
                if (!"tipo".equalsIgnoreCase(atributo)) throw new AtributoNaoExisteException();
                alterarTipo(id, tipo, comissaoOuSalario, comissaoOuSalario);
            } catch (ValidacaoException | EmpregadoNaoExisteException e) {
                throw new RuntimeException(e);
            }
        };
        commandHistoryService.execute(commandAction, undoAction);
    }

    // --- MÉTODOS DE CONSULTA E PRIVADOS ---
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
                throw new ValidacaoException("Empregado nao recebe em banco.");
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
                throw new ValidacaoException("Empregado nao eh sindicalizado.");
            }
            default -> throw new AtributoNaoExisteException();
        };
    }

    public String getEmpregadoPorNome(String nome, int indice) throws EmpregadoNaoExisteException {
        List<Empregado> empregadosEncontrados = new ArrayList<>();
        for (Empregado e : repository.findAll()) if (e.getNome().equals(nome)) empregadosEncontrados.add(e);
        int index = indice - 1;
        if (empregadosEncontrados.isEmpty() || index < 0 || index >= empregadosEncontrados.size()) throw new EmpregadoNaoExisteException("Nao ha empregado com esse nome.");
        return empregadosEncontrados.get(index).getId();
    }

    public int getNumeroDeEmpregados() {
        return repository.findAll().size();
    }

    private void alterarTipo(String id, String novoTipo, String comissao, String novoSalario) throws ValidacaoException, EmpregadoNaoExisteException {
        Empregado eAntigo = getEmpregadoValido(id);
        String salario = (novoSalario != null) ? novoSalario : eAntigo.getSalarioSemFormato();
        Empregado eNovo;

        switch (novoTipo.toLowerCase()) {
            case "horista" -> {
                validarSalario(salario);
                eNovo = new EmpregadoHorista(id, eAntigo.getNome(), eAntigo.getEndereco(), novoTipo, salario);
            }
            case "assalariado" -> {
                validarSalario(salario);
                eNovo = new EmpregadoAssalariado(id, eAntigo.getNome(), eAntigo.getEndereco(), novoTipo, salario);
            }
            case "comissionado" -> {
                if (comissao == null) throw new ComissaoNulaException();
                validarComissao(comissao);
                eNovo = new EmpregadoComissionado(id, eAntigo.getNome(), eAntigo.getEndereco(), novoTipo, salario, comissao);
            }
            default -> throw new TipoInvalidoException();
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