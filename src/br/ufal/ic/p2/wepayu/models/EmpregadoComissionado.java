package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;
import br.ufal.ic.p2.wepayu.Services.ConsultaService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @Override
    public double calcularSalarioBruto(LocalDate dataFolha, ConsultaService consultaService) throws ValidacaoException, EmpregadoNaoExisteException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        String dataInicialStr = getDataUltimoPagamento().plusDays(1).format(formatter);
        String dataFinalStr = dataFolha.plusDays(1).format(formatter);

        double salarioFixo = consultaService.getSalarioFixoComissionado(this);
        double vendas = Double.parseDouble(consultaService.getVendasRealizadas(getId(), dataInicialStr, dataFinalStr).replace(',', '.'));
        double comissao = consultaService.getComissaoSobreVendas(this, vendas);

        double salario = salarioFixo + comissao;
        return Math.floor((salario * 100) + 1e-9) / 100.0;
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

    @Override
    public Empregado clone() {
        EmpregadoComissionado cloned = new EmpregadoComissionado();
        super.copy(cloned);
        cloned.setComissao(this.getComissao());
        Map<String, ResultadoVenda> clonedVendas = new HashMap<>();
        for (Map.Entry<String, ResultadoVenda> entry : this.vendas.entrySet()) {
            clonedVendas.put(entry.getKey(), entry.getValue().clone());
        }
        cloned.setVendas(clonedVendas);
        return cloned;
    }
}