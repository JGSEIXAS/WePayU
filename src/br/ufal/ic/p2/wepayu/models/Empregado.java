package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;
import br.ufal.ic.p2.wepayu.Services.ConsultaService;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Classe abstrata que representa um Empregado.
 * Serve como base para todos os tipos de empregados do sistema,
 * definindo atributos e comportamentos comuns.
 */
public abstract class Empregado implements Serializable {
    private String id;
    private String nome;
    private String endereco;
    private String tipo;
    private String salario;
    private MembroSindicato membroSindicato;
    private MetodoPagamento metodoPagamento;
    private LocalDate dataContratacao;
    private LocalDate dataUltimoPagamento;

    /**
     * Construtor padrão para persistência.
     */
    public Empregado() {}

    /**
     * Cria uma cópia profunda (clone) do objeto Empregado.
     * @return Uma nova instância de {@link Empregado} com os mesmos dados.
     */
    public abstract Empregado clone();

    /**
     * Constrói uma instância de Empregado com os dados básicos.
     * @param id O ID único do empregado.
     * @param nome O nome do empregado.
     * @param endereco O endereço do empregado.
     * @param tipo O tipo de empregado (horista, assalariado, comissionado).
     * @param salario O salário base do empregado.
     */
    public Empregado(String id, String nome, String endereco, String tipo, String salario) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
        this.salario = salario;
        this.membroSindicato = null;
        this.metodoPagamento = new EmMaos();

        if ("horista".equals(tipo)) {
            this.dataContratacao = null;
        } else {
            this.dataContratacao = LocalDate.of(2005, 1, 1);
        }

        this.dataUltimoPagamento = (this.dataContratacao != null)
                ? this.dataContratacao.minusDays(1)
                : LocalDate.of(2004, 12, 31);
    }

    /**
     * Método abstrato para calcular o salário bruto do empregado.
     * A implementação é específica para cada tipo de empregado (Polimorfismo).
     * @param dataFolha A data de referência para o cálculo.
     * @param consultaService O serviço de consulta para obter dados adicionais.
     * @return O valor do salário bruto.
     * @throws ValidacaoException Se houver erro de validação.
     * @throws EmpregadoNaoExisteException Se houver referência a um empregado inexistente.
     */
    public abstract double calcularSalarioBruto(LocalDate dataFolha, ConsultaService consultaService) throws ValidacaoException, EmpregadoNaoExisteException;

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getSalario() {
        if (this.salario == null) return "0,00";
        double valor = Double.parseDouble(this.salario.replace(',', '.'));
        return String.format("%.2f", valor).replace('.', ',');
    }
    public String getSalarioSemFormato() { return this.salario; }
    public void setSalario(String salario) { this.salario = salario; }
    public boolean isSindicalizado() { return this.membroSindicato != null; }
    public MembroSindicato getMembroSindicato() { return membroSindicato; }
    public void setMembroSindicato(MembroSindicato membroSindicato) { this.membroSindicato = membroSindicato; }
    public MetodoPagamento getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(MetodoPagamento metodoPagamento) { this.metodoPagamento = metodoPagamento; }
    public LocalDate getDataContratacao() { return dataContratacao; }
    public void setDataContratacao(LocalDate dataContratacao) { this.dataContratacao = dataContratacao; }
    public LocalDate getDataUltimoPagamento() { return dataUltimoPagamento; }
    public void setDataUltimoPagamento(LocalDate dataUltimoPagamento) { this.dataUltimoPagamento = dataUltimoPagamento; }

    /**
     * Copia as propriedades deste empregado para uma instância clonada.
     * Garante a cópia profunda de objetos aninhados.
     * @param clone A instância de destino para a cópia.
     */
    protected void copy(Empregado clone) {
        clone.setId(this.id);
        clone.setNome(this.nome);
        clone.setEndereco(this.endereco);
        clone.setTipo(this.tipo);
        clone.setSalario(this.salario);

        if (this.membroSindicato != null) {
            clone.setMembroSindicato(this.membroSindicato.clone());
        } else {
            clone.setMembroSindicato(null);
        }

        if (this.metodoPagamento != null) {
            if (this.metodoPagamento instanceof Banco) {
                clone.setMetodoPagamento(((Banco) this.metodoPagamento).clone());
            } else if (this.metodoPagamento instanceof Correios) {
                clone.setMetodoPagamento(new Correios());
            } else if (this.metodoPagamento instanceof EmMaos) {
                clone.setMetodoPagamento(new EmMaos());
            }
        }

        clone.setDataContratacao(this.dataContratacao);
        clone.setDataUltimoPagamento(this.dataUltimoPagamento);
    }
}