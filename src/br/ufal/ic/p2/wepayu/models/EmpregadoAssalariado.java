package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Services.ConsultaService;
import java.io.Serializable;
import java.time.LocalDate;

public class EmpregadoAssalariado extends Empregado implements Serializable {

    public EmpregadoAssalariado() {
    }

    public EmpregadoAssalariado(String id, String nome, String endereco, String tipo, String salario) {
        super(id, nome, endereco, tipo, salario);
    }

    @Override
    public double calcularSalarioBruto(LocalDate dataFolha, ConsultaService consultaService) {
        return Double.parseDouble(getSalarioSemFormato().replace(',', '.'));
    }

    @Override
    public Empregado clone() {
        EmpregadoAssalariado cloned = new EmpregadoAssalariado(this.getId(), this.getNome(), this.getEndereco(), this.getTipo(), this.getSalario());
        super.copy(cloned);
        return cloned;
    }
}