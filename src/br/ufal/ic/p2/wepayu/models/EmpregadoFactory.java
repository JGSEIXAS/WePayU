package br.ufal.ic.p2.wepayu.models.factory;

import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoAssalariado;
import br.ufal.ic.p2.wepayu.models.EmpregadoComissionado;
import br.ufal.ic.p2.wepayu.models.EmpregadoHorista;
import br.ufal.ic.p2.wepayu.Exception.TipoInvalidoException;

/**
 * Fábrica para a criação de objetos {@link Empregado}.
 * Centraliza a lógica de instanciação dos diferentes tipos de empregados,
 * aplicando o padrão de projeto Factory Method.
 */
public class EmpregadoFactory {

    /**
     * Cria uma instância de um tipo de empregado (horista ou assalariado).
     * @param tipo O tipo de empregado ("horista" ou "assalariado").
     * @param id ID único do empregado.
     * @param nome Nome do empregado.
     * @param endereco Endereço do empregado.
     * @param salario Salário do empregado.
     * @return Uma nova instância de {@link Empregado}.
     * @throws TipoInvalidoException Se o tipo fornecido for inválido.
     */
    public static Empregado criarEmpregado(String tipo, String id, String nome, String endereco, String salario) throws TipoInvalidoException {
        if ("horista".equalsIgnoreCase(tipo)) {
            return new EmpregadoHorista(id, nome, endereco, tipo, salario);
        } else if ("assalariado".equalsIgnoreCase(tipo)) {
            return new EmpregadoAssalariado(id, nome, endereco, tipo, salario);
        } else {
            throw new TipoInvalidoException();
        }
    }

    /**
     * Cria uma instância de um empregado comissionado.
     * @param tipo O tipo de empregado (deve ser "comissionado").
     * @param id ID único do empregado.
     * @param nome Nome do empregado.
     * @param endereco Endereço do empregado.
     * @param salario Salário base do empregado.
     * @param comissao Taxa de comissão do empregado.
     * @return Uma nova instância de {@link EmpregadoComissionado}.
     * @throws TipoInvalidoException Se o tipo fornecido for diferente de "comissionado".
     */
    public static Empregado criarEmpregado(String tipo, String id, String nome, String endereco, String salario, String comissao) throws TipoInvalidoException {
        if ("comissionado".equalsIgnoreCase(tipo)) {
            return new EmpregadoComissionado(id, nome, endereco, tipo, salario, comissao);
        } else {
            throw new TipoInvalidoException();
        }
    }
}