package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.TipoInvalidoException;

public class EmpregadoFactory {

    public static Empregado criarEmpregado(String tipo, String id, String nome, String endereco, String salario) throws TipoInvalidoException {
        Empregado empregado;
        if ("horista".equalsIgnoreCase(tipo)) {
            empregado = new EmpregadoHorista(id, nome, endereco, tipo, salario);
            empregado.setAgendaPagamento(new AgendaPagamento("semanal 5")); // Padrão para Horista
        } else if ("assalariado".equalsIgnoreCase(tipo)) {
            empregado = new EmpregadoAssalariado(id, nome, endereco, tipo, salario);
            empregado.setAgendaPagamento(new AgendaPagamento("mensal $")); // Padrão para Assalariado
        } else {
            throw new TipoInvalidoException();
        }
        return empregado;
    }

    public static Empregado criarEmpregado(String tipo, String id, String nome, String endereco, String salario, String comissao) throws TipoInvalidoException {
        if ("comissionado".equalsIgnoreCase(tipo)) {
            EmpregadoComissionado empregado = new EmpregadoComissionado(id, nome, endereco, tipo, salario, comissao);
            empregado.setAgendaPagamento(new AgendaPagamento("semanal 2 5")); // Padrão para Comissionado
            return empregado;
        } else {
            throw new TipoInvalidoException();
        }
    }
}