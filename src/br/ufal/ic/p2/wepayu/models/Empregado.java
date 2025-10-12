package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;
import br.ufal.ic.p2.wepayu.Services.ConsultaService;

import java.time.LocalDate;

/**
 * Classe abstrata que representa um empregado genérico no sistema.
 * Contém os atributos e métodos comuns a todos os tipos de empregados.
 */
public abstract class Empregado {

    private String id;
    private String nome;
    private String endereco;
    private String tipo;
    private String salario;
    private MetodoPagamento metodoPagamento;
    private MembroSindicato membroSindicato;
    private AgendaPagamento agendaPagamento;
    private LocalDate dataContratacao;
    private LocalDate dataUltimoPagamento = LocalDate.of(1, 1, 1);

    /**
     * Construtor padrão.
     */
    public Empregado() {}

    /**
     * Constrói uma instância de Empregado com os dados básicos.
     * @param id ID único do empregado.
     * @param nome Nome do empregado.
     * @param endereco Endereço do empregado.
     * @param tipo Tipo do empregado.
     * @param salario Salário ou valor da hora.
     */
    public Empregado(String id, String nome, String endereco, String tipo, String salario) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
        this.salario = salario;
        this.metodoPagamento = new EmMaos(); // Padrão
    }

    /**
     * Método abstrato para calcular o salário bruto de um empregado.
     * @param dataFolha A data da folha de pagamento.
     * @param consultaService O serviço de consulta para obter dados necessários.
     * @return O valor do salário bruto.
     * @throws ValidacaoException se ocorrer um erro de validação.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     */
    public abstract double calcularSalarioBruto(LocalDate dataFolha, ConsultaService consultaService) throws ValidacaoException, EmpregadoNaoExisteException;

    /**
     * Copia os atributos de um empregado para outro (usado no padrão Prototype).
     * @param cloned O objeto de destino da cópia.
     */
    public void copy(Empregado cloned) {
        cloned.setId(this.id);
        cloned.setNome(this.nome);
        cloned.setEndereco(this.endereco);
        cloned.setTipo(this.tipo);
        cloned.setSalario(this.salario);

        // CORREÇÃO: Garante a cópia profunda dos objetos mutáveis.
        if (this.metodoPagamento instanceof Banco) {
            cloned.setMetodoPagamento(((Banco) this.metodoPagamento).clone());
        } else {
            cloned.setMetodoPagamento(this.metodoPagamento);
        }

        if (this.membroSindicato != null) {
            cloned.setMembroSindicato(this.membroSindicato.clone());
        } else {
            cloned.setMembroSindicato(null);
        }

        if (this.agendaPagamento != null) {
            cloned.setAgendaPagamento(this.agendaPagamento.clone());
        }

        cloned.setDataContratacao(this.dataContratacao);
        cloned.setDataUltimoPagamento(this.dataUltimoPagamento);
    }

    /**
     * Método abstrato para clonar o objeto Empregado.
     * @return Uma nova instância clonada do empregado.
     */
    public abstract Empregado clone();


    // --- GETTERS E SETTERS ---
    /**
     * Retorna o ID do empregado.
     * @return O ID do empregado.
     */
    public String getId() { return id; }
    /**
     * Define o ID do empregado.
     * @param id O novo ID.
     */
    public void setId(String id) { this.id = id; }
    /**
     * Retorna o nome do empregado.
     * @return O nome do empregado.
     */
    public String getNome() { return nome; }
    /**
     * Define o nome do empregado.
     * @param nome O novo nome.
     */
    public void setNome(String nome) { this.nome = nome; }
    /**
     * Retorna o endereço do empregado.
     * @return O endereço do empregado.
     */
    public String getEndereco() { return endereco; }
    /**
     * Define o endereço do empregado.
     * @param endereco O novo endereço.
     */
    public void setEndereco(String endereco) { this.endereco = endereco; }
    /**
     * Retorna o tipo do empregado.
     * @return O tipo do empregado.
     */
    public String getTipo() { return tipo; }
    /**
     * Define o tipo do empregado.
     * @param tipo O novo tipo.
     */
    public void setTipo(String tipo) { this.tipo = tipo; }

    /**
     * Retorna o salário formatado como string.
     * @return O salário formatado.
     */
    public String getSalario() {
        if (this.salario == null) return "0,00";
        try {
            double valorNumerico = Double.parseDouble(this.salario.replace(',', '.'));
            return String.format("%.2f", valorNumerico).replace('.', ',');
        } catch (NumberFormatException e) {
            return "0,00";
        }
    }

    /**
     * Define o salário do empregado.
     * @param salario O novo salário.
     */
    public void setSalario(String salario) { this.salario = salario; }
    /**
     * Retorna o método de pagamento.
     * @return O método de pagamento.
     */
    public MetodoPagamento getMetodoPagamento() { return metodoPagamento; }
    /**
     * Define o método de pagamento.
     * @param metodoPagamento O novo método de pagamento.
     */
    public void setMetodoPagamento(MetodoPagamento metodoPagamento) { this.metodoPagamento = metodoPagamento; }
    /**
     * Verifica se o empregado é sindicalizado.
     * @return {@code true} se for sindicalizado, {@code false} caso contrário.
     */
    public boolean isSindicalizado() { return membroSindicato != null; }
    /**
     * Retorna o objeto MembroSindicato.
     * @return O objeto MembroSindicato.
     */
    public MembroSindicato getMembroSindicato() { return membroSindicato; }
    /**
     * Define o objeto MembroSindicato.
     * @param membroSindicato O novo objeto MembroSindicato.
     */
    public void setMembroSindicato(MembroSindicato membroSindicato) { this.membroSindicato = membroSindicato; }
    /**
     * Retorna a agenda de pagamento.
     * @return A agenda de pagamento.
     */
    public AgendaPagamento getAgendaPagamento() { return agendaPagamento; }
    /**
     * Define a agenda de pagamento.
     * @param agendaPagamento A nova agenda de pagamento.
     */
    public void setAgendaPagamento(AgendaPagamento agendaPagamento) { this.agendaPagamento = agendaPagamento; }
    /**
     * Retorna a data de contratação.
     * @return A data de contratação.
     */
    public LocalDate getDataContratacao() { return dataContratacao; }
    /**
     * Define a data de contratação.
     * @param dataContratacao A nova data de contratação.
     */
    public void setDataContratacao(LocalDate dataContratacao) { this.dataContratacao = dataContratacao; }
    /**
     * Retorna a data do último pagamento.
     * @return A data do último pagamento.
     */
    public LocalDate getDataUltimoPagamento() { return dataUltimoPagamento; }
    /**
     * Define a data do último pagamento.
     * @param dataUltimoPagamento A nova data do último pagamento.
     */
    public void setDataUltimoPagamento(LocalDate dataUltimoPagamento) { this.dataUltimoPagamento = dataUltimoPagamento; }

    /**
     * Retorna o salário sem formatação.
     * @return O salário sem formatação.
     */
    public String getSalarioSemFormato() {
        return this.salario;
    }
}