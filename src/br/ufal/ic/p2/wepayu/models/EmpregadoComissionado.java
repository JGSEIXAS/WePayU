package br.ufal.ic.p2.wepayu.models;

import java.util.HashMap;
import java.util.Map;

public class EmpregadoComissionado extends Empregado {
    private Map<String, ResultadoVenda> vendas;
    private String comissao;

    public EmpregadoComissionado() {
        this.vendas = new HashMap<>();
    }

    public EmpregadoComissionado(String id, String nome, String endereco, String tipo, String salario, String comissao) {
        super(id, nome, endereco, tipo, salario);
        this.comissao = comissao;
        this.vendas = new HashMap<>();
    }

    public void lancaVenda(ResultadoVenda venda) {
        this.vendas.put(venda.getData(), venda);
    }

    public Map<String, ResultadoVenda> getVendas() {
        return vendas;
    }

    public void setVendas(Map<String, ResultadoVenda> vendas) {
        this.vendas = vendas;
    }


    public String getComissao() {
        if (this.comissao == null) return "0,00";
        String comissaoParaParse = this.comissao.replace(',', '.');
        double valorNumerico = Double.parseDouble(comissaoParaParse);
        return String.format("%.2f", valorNumerico).replace('.', ',');
    }

    public void setComissao(String comissao) {
        this.comissao = comissao;
    }
}