package br.ufal.ic.p2.wepayu.models;

public class EmpregadoAssalariado extends Empregado {

    public EmpregadoAssalariado() {
    }

    public EmpregadoAssalariado(String id, String nome, String endereco, String tipo, String salario) {
        super(id, nome, endereco, tipo, salario);
    }
}