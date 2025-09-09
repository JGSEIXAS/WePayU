package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.models.Empregado;

import br.ufal.ic.p2.wepayu.models.EmpregadoAssalariado;
import br.ufal.ic.p2.wepayu.models.EmpregadoComissionado;
import br.ufal.ic.p2.wepayu.models.EmpregadoHorista;
import br.ufal.ic.p2.wepayu.Exception.TipoInvalidoException;

public class EmpregadoFactory {

    public static Empregado criarEmpregado(String tipo, String id, String nome, String endereco, String salario) throws TipoInvalidoException {
        if ("horista".equalsIgnoreCase(tipo)) {
            return new EmpregadoHorista(id, nome, endereco, tipo, salario);
        } else if ("assalariado".equalsIgnoreCase(tipo)) {
            return new EmpregadoAssalariado(id, nome, endereco, tipo, salario);
        } else {
            throw new TipoInvalidoException();
        }
    }

    public static Empregado criarEmpregado(String tipo, String id, String nome, String endereco, String salario, String comissao) throws TipoInvalidoException {
        if ("comissionado".equalsIgnoreCase(tipo)) {
            return new EmpregadoComissionado(id, nome, endereco, tipo, salario, comissao);
        } else {
            throw new TipoInvalidoException();
        }
    }
}