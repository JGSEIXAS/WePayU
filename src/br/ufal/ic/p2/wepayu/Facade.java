package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEncontradoException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;
import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;
import br.ufal.ic.p2.wepayu.Services.*;

public class Facade {

    // --- DECLARAÇÕES DE SERVIÇOS E ESTADO ---
    private final EmpregadoRepository repository = new EmpregadoRepository();
    private final CommandHistoryService commandHistoryService = new CommandHistoryService();
    private final ConsultaService consultaService = new ConsultaService(repository);
    private final SistemaService sistemaService = new SistemaService(repository, commandHistoryService);
    private final EmpregadoService empregadoService = new EmpregadoService(repository, commandHistoryService);
    private final LancamentoService lancamentoService = new LancamentoService(repository, commandHistoryService);
    private final FolhaPagamentoService folhaPagamentoService = new FolhaPagamentoService(repository, consultaService, commandHistoryService);

    private boolean sistemaEncerrado = false;

    private void verificarSistemaEncerrado() throws ValidacaoException {
        if (sistemaEncerrado) {
            throw new ValidacaoException("Nao pode dar comandos depois de encerrarSistema.");
        }
    }

    // --- MÉTODOS DE CONTROLO ---
    public void zerarSistema() throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        sistemaService.zerarSistema();
    }

    public void encerrarSistema() {
        if (!sistemaEncerrado) {
            sistemaService.encerrarSistema();
            sistemaEncerrado = true;
        }
    }

    // --- GESTÃO DE EMPREGADOS ---
    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        return empregadoService.criarEmpregado(nome, endereco, tipo, salario);
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        return empregadoService.criarEmpregado(nome, endereco, tipo, salario, comissao);
    }

    public void removerEmpregado(String emp) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        empregadoService.removerEmpregado(emp);
    }

    // --- MÉTODOS 'alteraEmpregado' ---
    public void alteraEmpregado(String emp, String atributo, String valor) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        empregadoService.alteraEmpregado(emp, atributo, valor);
    }

    public void alteraEmpregado(String emp, String atributo, boolean valor, String idSindicato, String taxaSindical) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        empregadoService.alteraEmpregado(emp, atributo, valor, idSindicato, taxaSindical);
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String salario) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        empregadoService.alteraEmpregado(emp, atributo, valor, salario);
    }
    public void alteraEmpregado(String emp, String atributo, String valor1, String banco, String agencia, String contaCorrente) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        empregadoService.alteraEmpregado(emp, atributo, valor1, banco, agencia, contaCorrente);
    }

    // --- CONSULTAS E LANÇAMENTOS ---
    public String getAtributoEmpregado(String emp, String atributo) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        return empregadoService.getAtributoEmpregado(emp, atributo);
    }

    public String getEmpregadoPorNome(String nome, int indice) throws EmpregadoNaoExisteException, ValidacaoException, EmpregadoNaoEncontradoException {
        verificarSistemaEncerrado();
        return empregadoService.getEmpregadoPorNome(nome, indice);
    }

    public void lancaCartao(String emp, String data, String horas) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        lancamentoService.lancaCartao(emp, data, horas);
    }

    public void lancaVenda(String emp, String data, String valor) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        lancamentoService.lancaVenda(emp, data, valor);
    }

    public void lancaTaxaServico(String membro, String data, String valor) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        lancamentoService.lancaTaxaServico(membro, data, valor);
    }

    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        return consultaService.getVendasRealizadas(emp, dataInicial, dataFinal);
    }

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        return consultaService.getHorasNormaisTrabalhadas(emp, dataInicial, dataFinal);
    }

    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        return consultaService.getHorasExtrasTrabalhadas(emp, dataInicial, dataFinal);
    }

    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        return consultaService.getTaxasServico(emp, dataInicial, dataFinal);
    }

    // --- MÉTODOS DA FOLHA DE PAGAMENTO ---
    public String totalFolha(String data) throws Exception {
        verificarSistemaEncerrado();
        return consultaService.totalFolha(data);
    }

    public void rodaFolha(String data, String saida) throws Exception {
        verificarSistemaEncerrado();
        folhaPagamentoService.rodaFolha(data, saida);
    }

    // --- MÉTODOS DE UNDO/REDO E CONSULTA DE CONTAGEM ---
    public int getNumeroDeEmpregados() throws ValidacaoException {
        verificarSistemaEncerrado();
        return empregadoService.getNumeroDeEmpregados();
    }

    public void undo() throws ValidacaoException {
        verificarSistemaEncerrado();
        commandHistoryService.undo();
    }

    public void redo() throws ValidacaoException {
        verificarSistemaEncerrado();
        commandHistoryService.redo();
    }
}