package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;
import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository; // Verifique o nome do pacote se necessário
import br.ufal.ic.p2.wepayu.Services.ConsultaService;
import br.ufal.ic.p2.wepayu.Services.EmpregadoService;
import br.ufal.ic.p2.wepayu.Services.FolhaPagamentoService;
import br.ufal.ic.p2.wepayu.Services.LancamentoService;
import br.ufal.ic.p2.wepayu.Services.SistemaService;

public class Facade {

    private final EmpregadoRepository repository = new EmpregadoRepository();
    private final SistemaService sistemaService = new SistemaService(repository);
    private final EmpregadoService empregadoService = new EmpregadoService(repository);
    private final LancamentoService lancamentoService = new LancamentoService(repository);
    private final ConsultaService consultaService = new ConsultaService(repository);

    // CORREÇÃO: Passa o 'repository' E o 'consultaService' para o construtor
    private final FolhaPagamentoService folhaPagamentoService = new FolhaPagamentoService(repository, consultaService);

    // --- Métodos de Controle ---
    public void zerarSistema() { sistemaService.zerarSistema(); }
    public void encerrarSistema() { sistemaService.encerrarSistema(); }

    // --- Gerenciamento de Empregados ---
    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws ValidacaoException {
        return empregadoService.criarEmpregado(nome, endereco, tipo, salario);
    }
    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws ValidacaoException {
        return empregadoService.criarEmpregado(nome, endereco, tipo, salario, comissao);
    }
    public void removerEmpregado(String emp) throws ValidacaoException, EmpregadoNaoExisteException {
        empregadoService.removerEmpregado(emp);
    }

    // --- Métodos 'alteraEmpregado' (Corrigidos e sem duplicatas) ---
    public void alteraEmpregado(String emp, String atributo, String valor) throws ValidacaoException, EmpregadoNaoExisteException {
        empregadoService.alteraEmpregado(emp, atributo, valor);
    }
    public void alteraEmpregado(String emp, String atributo, boolean valor, String idSindicato, String taxaSindical) throws ValidacaoException, EmpregadoNaoExisteException {
        empregadoService.alteraEmpregado(emp, atributo, valor, idSindicato, taxaSindical);
    }
    public void alteraEmpregado(String emp, String atributo, String valor, String comissaoOuSalario) throws ValidacaoException, EmpregadoNaoExisteException {
        empregadoService.alteraEmpregado(emp, atributo, valor, comissaoOuSalario);
    }
    public void alteraEmpregado(String emp, String atributo, String valor1, String banco, String agencia, String contaCorrente) throws ValidacaoException, EmpregadoNaoExisteException {
        empregadoService.alteraEmpregado(emp, atributo, valor1, banco, agencia, contaCorrente);
    }

    // --- Consultas e Lançamentos ---
    public String getAtributoEmpregado(String emp, String atributo) throws ValidacaoException, EmpregadoNaoExisteException {
        return empregadoService.getAtributoEmpregado(emp, atributo);
    }
    public String getEmpregadoPorNome(String nome, int indice) throws EmpregadoNaoExisteException {
        return empregadoService.getEmpregadoPorNome(nome, indice);
    }
    public void lancaCartao(String emp, String data, String horas) throws ValidacaoException, EmpregadoNaoExisteException {
        lancamentoService.lancaCartao(emp, data, horas);
    }
    public void lancaVenda(String emp, String data, String valor) throws ValidacaoException, EmpregadoNaoExisteException {
        lancamentoService.lancaVenda(emp, data, valor);
    }
    public void lancaTaxaServico(String membro, String data, String valor) throws ValidacaoException, EmpregadoNaoExisteException {
        lancamentoService.lancaTaxaServico(membro, data, valor);
    }
    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws ValidacaoException, EmpregadoNaoExisteException {
        return consultaService.getVendasRealizadas(emp, dataInicial, dataFinal);
    }
    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws ValidacaoException, EmpregadoNaoExisteException {
        return consultaService.getHorasNormaisTrabalhadas(emp, dataInicial, dataFinal);
    }
    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws ValidacaoException, EmpregadoNaoExisteException {
        return consultaService.getHorasExtrasTrabalhadas(emp, dataInicial, dataFinal);
    }
    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws ValidacaoException, EmpregadoNaoExisteException {
        return consultaService.getTaxasServico(emp, dataInicial, dataFinal);
    }

    // --- Métodos da Folha de Pagamento ---
    public String totalFolha(String data) throws Exception {
        return consultaService.totalFolha(data);
    }
    public void rodaFolha(String data, String saida) throws Exception {
        folhaPagamentoService.rodaFolha(data, saida);
    }
}