package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEncontradoException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;
import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;
import br.ufal.ic.p2.wepayu.Services.*;

/**
 * Fachada para o sistema WePayU.
 * Ponto de entrada único que simplifica a interação com os diversos serviços do sistema,
 * como gerenciamento de empregados, lançamentos e folha de pagamento.
 */
public class Facade {

    private final EmpregadoRepository repository = new EmpregadoRepository();
    private final CommandHistoryService commandHistoryService = new CommandHistoryService();
    private final ConsultaService consultaService = new ConsultaService(repository);
    private final SistemaService sistemaService = new SistemaService(repository, commandHistoryService);
    private final EmpregadoService empregadoService = new EmpregadoService(repository, commandHistoryService);
    private final LancamentoService lancamentoService = new LancamentoService(repository, commandHistoryService);
    private final FolhaPagamentoService folhaPagamentoService = new FolhaPagamentoService(repository, consultaService, commandHistoryService);

    private boolean sistemaEncerrado = false;

    /**
     * Verifica se o sistema foi encerrado, lançando uma exceção se for o caso.
     * @throws ValidacaoException se o sistema já tiver sido encerrado.
     */
    private void verificarSistemaEncerrado() throws ValidacaoException {
        if (sistemaEncerrado) {
            throw new ValidacaoException("Nao pode dar comandos depois de encerrarSistema.");
        }
    }

    /**
     * Zera o estado do sistema, removendo todos os dados.
     * @throws ValidacaoException se ocorrer um erro de validação.
     * @throws EmpregadoNaoExisteException se um empregado esperado não for encontrado.
     */
    public void zerarSistema() throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        sistemaService.zerarSistema();
    }

    /**
     * Encerra o sistema, salvando os dados e impedindo novas operações.
     */
    public void encerrarSistema() {
        if (!sistemaEncerrado) {
            sistemaService.encerrarSistema();
            sistemaEncerrado = true;
        }
    }

    /**
     * Cria um novo empregado no sistema.
     * @param nome Nome do empregado.
     * @param endereco Endereço do empregado.
     * @param tipo Tipo do empregado (horista, assalariado).
     * @param salario Salário do empregado.
     * @return O ID do empregado criado.
     * @throws ValidacaoException se ocorrer um erro de validação.
     * @throws EmpregadoNaoExisteException se um empregado esperado não for encontrado.
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        return empregadoService.criarEmpregado(nome, endereco, tipo, salario);
    }

    /**
     * Cria um novo empregado comissionado.
     * @param nome Nome do empregado.
     * @param endereco Endereço do empregado.
     * @param tipo Tipo do empregado (deve ser comissionado).
     * @param salario Salário base do empregado.
     * @param comissao Taxa de comissão do empregado.
     * @return O ID do empregado criado.
     * @throws ValidacaoException se ocorrer um erro de validação.
     * @throws EmpregadoNaoExisteException se um empregado esperado não for encontrado.
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        return empregadoService.criarEmpregado(nome, endereco, tipo, salario, comissao);
    }

    /**
     * Cria uma nova agenda de pagamentos customizada.
     * @param descricao A descrição da agenda (ex: "semanal 2", "mensal 15").
     * @throws ValidacaoException se a descrição da agenda for inválida.
     */
    public void criarAgendaDePagamentos(String descricao) throws ValidacaoException {
        verificarSistemaEncerrado();
        empregadoService.criarAgendaDePagamentos(descricao);
    }

    /**
     * Remove um empregado do sistema.
     * @param emp O ID do empregado a ser removido.
     * @throws ValidacaoException se ocorrer um erro de validação.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     */
    public void removerEmpregado(String emp) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        empregadoService.removerEmpregado(emp);
    }

    /**
     * Altera um atributo de um empregado.
     * @param emp O ID do empregado.
     * @param atributo O atributo a ser alterado.
     * @param valor O novo valor para o atributo.
     * @throws ValidacaoException se ocorrer um erro de validação.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     */
    public void alteraEmpregado(String emp, String atributo, String valor) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        empregadoService.alteraEmpregado(emp, atributo, valor);
    }

    /**
     * Altera o status de sindicalização de um empregado.
     * @param emp O ID do empregado.
     * @param atributo O atributo (deve ser "sindicalizado").
     * @param valor O novo status de sindicalização.
     * @param idSindicato O ID do sindicato (se aplicável).
     * @param taxaSindical A taxa sindical (se aplicável).
     * @throws ValidacaoException se ocorrer um erro de validação.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     */
    public void alteraEmpregado(String emp, String atributo, boolean valor, String idSindicato, String taxaSindical) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        empregadoService.alteraEmpregado(emp, atributo, valor, idSindicato, taxaSindical);
    }

    /**
     * Altera o tipo de um empregado.
     * @param emp O ID do empregado.
     * @param atributo O atributo (deve ser "tipo").
     * @param valor O novo tipo do empregado.
     * @param salario O novo salário do empregado.
     * @throws ValidacaoException se ocorrer um erro de validação.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     */
    public void alteraEmpregado(String emp, String atributo, String valor, String salario) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        empregadoService.alteraEmpregado(emp, atributo, valor, salario);
    }
    /**
     * Altera o método de pagamento de um empregado para banco.
     * @param emp O ID do empregado.
     * @param atributo O atributo (deve ser "metodoPagamento").
     * @param valor1 O valor (deve ser "banco").
     * @param banco O nome do banco.
     * @param agencia O número da agência.
     * @param contaCorrente O número da conta corrente.
     * @throws ValidacaoException se ocorrer um erro de validação.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     */
    public void alteraEmpregado(String emp, String atributo, String valor1, String banco, String agencia, String contaCorrente) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        empregadoService.alteraEmpregado(emp, atributo, valor1, banco, agencia, contaCorrente);
    }

    /**
     * Obtém o valor de um atributo de um empregado.
     * @param emp O ID do empregado.
     * @param atributo O atributo a ser consultado.
     * @return O valor do atributo.
     * @throws ValidacaoException se ocorrer um erro de validação.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     */
    public String getAtributoEmpregado(String emp, String atributo) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        return empregadoService.getAtributoEmpregado(emp, atributo);
    }

    /**
     * Busca um empregado pelo nome.
     * @param nome O nome do empregado.
     * @param indice O índice do empregado (caso haja múltiplos com o mesmo nome).
     * @return O ID do empregado encontrado.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     * @throws ValidacaoException se ocorrer um erro de validação.
     * @throws EmpregadoNaoEncontradoException se não houver empregado com o nome especificado.
     */
    public String getEmpregadoPorNome(String nome, int indice) throws EmpregadoNaoExisteException, ValidacaoException, EmpregadoNaoEncontradoException {
        verificarSistemaEncerrado();
        return empregadoService.getEmpregadoPorNome(nome, indice);
    }

    /**
     * Lança um cartão de ponto para um empregado horista.
     * @param emp O ID do empregado.
     * @param data A data do registro.
     * @param horas As horas trabalhadas.
     * @throws ValidacaoException se ocorrer um erro de validação.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     */
    public void lancaCartao(String emp, String data, String horas) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        lancamentoService.lancaCartao(emp, data, horas);
    }

    /**
     * Lança uma venda para um empregado comissionado.
     * @param emp O ID do empregado.
     * @param data A data da venda.
     * @param valor O valor da venda.
     * @throws ValidacaoException se ocorrer um erro de validação.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     */
    public void lancaVenda(String emp, String data, String valor) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        lancamentoService.lancaVenda(emp, data, valor);
    }

    /**
     * Lança uma taxa de serviço para um membro do sindicato.
     * @param membro O ID do membro do sindicato.
     * @param data A data da taxa.
     * @param valor O valor da taxa.
     * @throws ValidacaoException se ocorrer um erro de validação.
     * @throws EmpregadoNaoExisteException se o membro do sindicato não for encontrado.
     */
    public void lancaTaxaServico(String membro, String data, String valor) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        lancamentoService.lancaTaxaServico(membro, data, valor);
    }

    /**
     * Obtém o total de vendas realizadas por um empregado em um período.
     * @param emp O ID do empregado.
     * @param dataInicial A data inicial do período.
     * @param dataFinal A data final do período.
     * @return O valor total das vendas.
     * @throws ValidacaoException se ocorrer um erro de validação.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     */
    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        return consultaService.getVendasRealizadas(emp, dataInicial, dataFinal);
    }

    /**
     * Obtém o total de horas normais trabalhadas por um empregado em um período.
     * @param emp O ID do empregado.
     * @param dataInicial A data inicial do período.
     * @param dataFinal A data final do período.
     * @return O total de horas normais.
     * @throws ValidacaoException se ocorrer um erro de validação.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     */
    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        return consultaService.getHorasNormaisTrabalhadas(emp, dataInicial, dataFinal);
    }

    /**
     * Obtém o total de horas extras trabalhadas por um empregado em um período.
     * @param emp O ID do empregado.
     * @param dataInicial A data inicial do período.
     * @param dataFinal A data final do período.
     * @return O total de horas extras.
     * @throws ValidacaoException se ocorrer um erro de validação.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     */
    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        return consultaService.getHorasExtrasTrabalhadas(emp, dataInicial, dataFinal);
    }

    /**
     * Obtém o total de taxas de serviço de um empregado sindicalizado em um período.
     * @param emp O ID do empregado.
     * @param dataInicial A data inicial do período.
     * @param dataFinal A data final do período.
     * @return O valor total das taxas de serviço.
     * @throws ValidacaoException se ocorrer um erro de validação.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     */
    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws ValidacaoException, EmpregadoNaoExisteException {
        verificarSistemaEncerrado();
        return consultaService.getTaxasServico(emp, dataInicial, dataFinal);
    }

    /**
     * Calcula o valor total da folha de pagamento em uma data específica.
     * @param data A data para cálculo da folha.
     * @return O valor total da folha.
     * @throws Exception se ocorrer um erro durante o cálculo.
     */
    public String totalFolha(String data) throws Exception {
        verificarSistemaEncerrado();
        // CORREÇÃO: Chamada direcionada para o ConsultaService
        return consultaService.totalFolha(data);
    }

    /**
     * Roda a folha de pagamento para uma data específica e gera um arquivo de saída.
     * @param data A data para processamento da folha.
     * @param saida O nome do arquivo de saída a ser gerado.
     * @throws Exception se ocorrer um erro durante o processamento.
     */
    public void rodaFolha(String data, String saida) throws Exception {
        verificarSistemaEncerrado();
        folhaPagamentoService.rodaFolha(data, saida);
    }

    /**
     * Retorna o número total de empregados cadastrados.
     * @return O número de empregados.
     * @throws ValidacaoException se o sistema estiver encerrado.
     */
    public int getNumeroDeEmpregados() throws ValidacaoException {
        verificarSistemaEncerrado();
        return empregadoService.getNumeroDeEmpregados();
    }

    /**
     * Desfaz a última operação realizada no sistema.
     * @throws ValidacaoException se não houver operação para desfazer.
     */
    public void undo() throws ValidacaoException {
        verificarSistemaEncerrado();
        commandHistoryService.undo();
    }

    /**
     * Refaz a última operação desfeita.
     * @throws ValidacaoException se não houver operação para refazer.
     */
    public void redo() throws ValidacaoException {
        verificarSistemaEncerrado();
        commandHistoryService.redo();
    }
}