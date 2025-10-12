package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Services.ConsultaService;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Representa um empregado assalariado, que recebe um salário fixo.
 */
public class EmpregadoAssalariado extends Empregado implements Serializable {

    /**
     * Construtor padrão.
     */
    public EmpregadoAssalariado() {
    }

    /**
     * Constrói uma instância de EmpregadoAssalariado.
     * @param id ID único do empregado.
     * @param nome Nome do empregado.
     * @param endereco Endereço do empregado.
     * @param tipo Tipo do empregado (deve ser "assalariado").
     * @param salario Salário fixo mensal.
     */
    public EmpregadoAssalariado(String id, String nome, String endereco, String tipo, String salario) {
        super(id, nome, endereco, tipo, salario);
        setDataContratacao(LocalDate.of(2005, 1, 1));
        setDataUltimoPagamento(LocalDate.of(2004, 12, 31));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Calcula o salário bruto com base na agenda de pagamento (mensal ou semanal).
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Empregado clone() {
        EmpregadoAssalariado cloned = new EmpregadoAssalariado();
        super.copy(cloned);
        return cloned;
    }
}