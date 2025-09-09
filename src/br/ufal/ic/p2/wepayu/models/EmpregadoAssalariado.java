package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Services.ConsultaService;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Classe que representa um empregado que recebe um salário mensal fixo.
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
     * @param salario Salário mensal fixo.
     */
    public EmpregadoAssalariado(String id, String nome, String endereco, String tipo, String salario) {
        super(id, nome, endereco, tipo, salario);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Para um empregado assalariado, o salário bruto é simplesmente o seu salário mensal fixo.
     */
    @Override
    public double calcularSalarioBruto(LocalDate dataFolha, ConsultaService consultaService) {
        return Double.parseDouble(getSalarioSemFormato().replace(',', '.'));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Empregado clone() {
        EmpregadoAssalariado cloned = new EmpregadoAssalariado(this.getId(), this.getNome(), this.getEndereco(), this.getTipo(), this.getSalario());
        super.copy(cloned);
        return cloned;
    }
}