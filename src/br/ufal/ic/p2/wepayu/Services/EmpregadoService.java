package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;
import java.util.ArrayList;
import java.util.List;

public class EmpregadoService extends BaseService {

    public EmpregadoService(EmpregadoRepository repository) {
        super(repository);
    }

    // --- Métodos de Criação e Remoção ---
    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws ValidacaoException {
        validarCamposBase(nome, endereco, salario);
        if ("comissionado".equals(tipo)) throw new TipoNaoAplicavelException();
        if (!"horista".equals(tipo) && !"assalariado".equals(tipo)) throw new TipoInvalidoException();
        String id = repository.getNextId();
        Empregado e = "horista".equals(tipo) ? new EmpregadoHorista(id, nome, endereco, tipo, salario) : new EmpregadoAssalariado(id, nome, endereco, tipo, salario);
        repository.save(e);
        return id;
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws ValidacaoException {
        validarCamposBase(nome, endereco, salario);
        validarComissao(comissao);
        if (!"comissionado".equals(tipo)) throw new TipoNaoAplicavelException();
        String id = repository.getNextId();
        Empregado e = new EmpregadoComissionado(id, nome, endereco, tipo, salario, comissao);
        repository.save(e);
        return id;
    }

    public void removerEmpregado(String id) throws ValidacaoException, EmpregadoNaoExisteException {
        if (id == null || id.isEmpty()) throw new IdentificacaoNulaException();
        if (repository.deleteById(id) == null) throw new EmpregadoNaoExisteException("Empregado nao existe.");
    }

    // --- Métodos de Alteração ---
    public void alteraEmpregado(String id, String atributo, String valor) throws ValidacaoException, EmpregadoNaoExisteException {
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
            case "tipo":
                alterarTipo(id, valor, null, null);
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
                    // Esta versão do comando não pode sindicalizar, pois falta o idSindicato
                    throw new ValidacaoException("Identificacao do sindicato nao pode ser nula.");
                } else {
                    throw new ValidacaoException("Valor deve ser true ou false.");
                }
                break;
            default:
                throw new AtributoNaoExisteException();
        }
    }


    public void alteraEmpregado(String id, String atributo, String valor, String banco, String agencia, String contaCorrente) throws ValidacaoException, EmpregadoNaoExisteException {
        Empregado empregado = getEmpregadoValido(id);
        if ("metodopagamento".equalsIgnoreCase(atributo) && "banco".equalsIgnoreCase(valor)) {
            if (banco == null || banco.isEmpty()) throw new ValidacaoException("Banco nao pode ser nulo.");
            if (agencia == null || agencia.isEmpty()) throw new ValidacaoException("Agencia nao pode ser nulo.");
            if (contaCorrente == null || contaCorrente.isEmpty()) throw new ValidacaoException("Conta corrente nao pode ser nulo.");
            empregado.setMetodoPagamento(new Banco(banco, agencia, contaCorrente));
        } else {
            throw new AtributoNaoExisteException();
        }
    }

    public void alteraEmpregado(String id, String atributo, boolean status, String idSindicato, String taxaSindical) throws ValidacaoException, EmpregadoNaoExisteException {
        Empregado empregado = getEmpregadoValido(id);
        if (!"sindicalizado".equalsIgnoreCase(atributo)) throw new AtributoNaoExisteException();

        if (status) { // Tornando membro
            if (idSindicato == null || idSindicato.isEmpty()) throw new ValidacaoException("Identificacao do sindicato nao pode ser nula.");
            if (taxaSindical == null || taxaSindical.isEmpty()) throw new ValidacaoException("Taxa sindical nao pode ser nula.");

            for (Empregado e : repository.findAll()) {
                if (e.getId().equals(id)) continue;
                if (e.isSindicalizado() && e.getMembroSindicato().getIdMembro().equals(idSindicato)) {
                    throw new ValidacaoException("Ha outro empregado com esta identificacao de sindicato");
                }
            }
            try {
                double taxa = Double.parseDouble(taxaSindical.replace(',', '.'));
                if (taxa < 0) throw new ValidacaoException("Taxa sindical deve ser nao-negativa.");
                empregado.setMembroSindicato(new MembroSindicato(idSindicato, taxa));
            } catch (NumberFormatException e) {
                throw new ValidacaoException("Taxa sindical deve ser numerica.");
            }
        } else { // Removendo filiação
            empregado.setMembroSindicato(null);
        }
    }

    // --- Métodos de Consulta ---
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

    // --- Métodos Privados ---
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
    public void alteraEmpregado(String id, String atributo, String tipo, String comissaoOuSalario) throws ValidacaoException, EmpregadoNaoExisteException {
        if (!"tipo".equalsIgnoreCase(atributo)) {
            throw new AtributoNaoExisteException();
        }

        // O método 'alterarTipo' agora decide se o último parâmetro é comissão ou salário
        if ("comissionado".equalsIgnoreCase(tipo)) {
            alterarTipo(id, tipo, comissaoOuSalario, null);
        } else {
            alterarTipo(id, tipo, null, comissaoOuSalario);
        }
    }
}