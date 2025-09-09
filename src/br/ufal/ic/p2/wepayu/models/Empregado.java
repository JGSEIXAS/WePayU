package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;
import br.ufal.ic.p2.wepayu.Services.ConsultaService;
import java.io.Serializable;
import java.time.LocalDate;

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

    public Empregado() {}
    public abstract Empregado clone();

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

    // **CORREÇÃO ADICIONADA AQUI**
    // Adiciona o método abstrato que estava faltando.
    public abstract double calcularSalarioBruto(LocalDate dataFolha, ConsultaService consultaService) throws ValidacaoException, EmpregadoNaoExisteException;

    // Getters e Setters (permanecem os mesmos)
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
     * Garante a cópia profunda de todos os campos aninhados.
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