package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Services.ConsultaService;
import java.io.Serializable;
import java.time.LocalDate;

public class EmpregadoAssalariado extends Empregado implements Serializable {

    public EmpregadoAssalariado() {
    }

    public EmpregadoAssalariado(String id, String nome, String endereco, String tipo, String salario) {
        super(id, nome, endereco, tipo, salario);
        setDataContratacao(LocalDate.of(2005, 1, 1));
        setDataUltimoPagamento(LocalDate.of(2004, 12, 31));
    }

    @Override
    public double calcularSalarioBruto(LocalDate dataFolha, ConsultaService consultaService) {
        String agenda = getAgendaPagamento().getDescricao();
        double salarioBase = Double.parseDouble(getSalarioSemFormato().replace(',', '.'));

        if (agenda.startsWith("semanal")) {
            int frequencia = 1;
            if (agenda.split(" ").length == 3) {
                frequencia = Integer.parseInt(agenda.split(" ")[1]);
            } else if (getAgendaPagamento().getDescricao().equals("semanal 2 5")) {
                frequencia = 2;
            }

            double result = (salarioBase * 12 / 52.0) * frequencia;
            return Math.floor(result * 100) / 100.0;
        }

        return salarioBase;
    }

    @Override
    public Empregado clone() {
        EmpregadoAssalariado cloned = new EmpregadoAssalariado();
        super.copy(cloned);
        return cloned;
    }
}