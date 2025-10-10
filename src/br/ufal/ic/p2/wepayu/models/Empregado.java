package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;
import br.ufal.ic.p2.wepayu.Services.ConsultaService;

import java.time.LocalDate;

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

    public Empregado() {}

    public Empregado(String id, String nome, String endereco, String tipo, String salario) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
        this.salario = salario;
        this.metodoPagamento = new EmMaos(); // Padrão
    }

    public abstract double calcularSalarioBruto(LocalDate dataFolha, ConsultaService consultaService) throws ValidacaoException, EmpregadoNaoExisteException;

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

    public abstract Empregado clone();


    // --- GETTERS E SETTERS ---
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
        try {
            double valorNumerico = Double.parseDouble(this.salario.replace(',', '.'));
            return String.format("%.2f", valorNumerico).replace('.', ',');
        } catch (NumberFormatException e) {
            return "0,00";
        }
    }

    public void setSalario(String salario) { this.salario = salario; }
    public MetodoPagamento getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(MetodoPagamento metodoPagamento) { this.metodoPagamento = metodoPagamento; }
    public boolean isSindicalizado() { return membroSindicato != null; }
    public MembroSindicato getMembroSindicato() { return membroSindicato; }
    public void setMembroSindicato(MembroSindicato membroSindicato) { this.membroSindicato = membroSindicato; }
    public AgendaPagamento getAgendaPagamento() { return agendaPagamento; }
    public void setAgendaPagamento(AgendaPagamento agendaPagamento) { this.agendaPagamento = agendaPagamento; }
    public LocalDate getDataContratacao() { return dataContratacao; }
    public void setDataContratacao(LocalDate dataContratacao) { this.dataContratacao = dataContratacao; }
    public LocalDate getDataUltimoPagamento() { return dataUltimoPagamento; }
    public void setDataUltimoPagamento(LocalDate dataUltimoPagamento) { this.dataUltimoPagamento = dataUltimoPagamento; }

    public String getSalarioSemFormato() {
        return this.salario;
    }
}