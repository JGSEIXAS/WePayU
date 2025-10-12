package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.TipoInvalidoException;

/**
 * Fábrica para a criação de diferentes tipos de empregados.
 * Utiliza o padrão de projeto Factory Method para encapsular a lógica de instanciação.
 */
public class EmpregadoFactory {

    /**
     * Cria uma instância de Empregado com base no tipo especificado.
     * @param tipo O tipo de empregado ("horista" ou "assalariado").
     * @param id ID único do empregado.
     * @param nome Nome do empregado.
     * @param endereco Endereço do empregado.
     * @param salario Salário ou valor da hora.
     * @return Uma nova instância de {@link Empregado}.
     * @throws TipoInvalidoException se o tipo de empregado for inválido.
     */
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

    /**
     * Cria uma instância de EmpregadoComissionado.
     * @param tipo O tipo de empregado (deve ser "comissionado").
     * @param id ID único do empregado.
     * @param nome Nome do empregado.
     * @param endereco Endereço do empregado.
     * @param salario Salário base.
     * @param comissao Taxa de comissão.
     * @return Uma nova instância de {@link EmpregadoComissionado}.
     * @throws TipoInvalidoException se o tipo de empregado não for "comissionado".
     */
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