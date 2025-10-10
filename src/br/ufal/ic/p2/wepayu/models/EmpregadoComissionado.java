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
        // CORREÇÃO: Define a data de contratação e o último pagamento inicial.
        setDataContratacao(LocalDate.of(2005, 1, 1));
        setDataUltimoPagamento(LocalDate.of(2004, 12, 31));
    }

    @Override
    public double calcularSalarioBruto(LocalDate dataFolha, ConsultaService consultaService) throws ValidacaoException, EmpregadoNaoExisteException {
        String agenda = getAgendaPagamento().getDescricao();
        double salarioBase = Double.parseDouble(getSalarioSemFormato().replace(',', '.'));

        LocalDate dataInicialPagamento = getDataUltimoPagamento().plusDays(1);
        LocalDate dataFinalPagamento = dataFolha.plusDays(1);

        double valorDasVendas = Double.parseDouble(consultaService.getVendasRealizadas(getId(), dataInicialPagamento.format(DateTimeFormatter.ofPattern("d/M/yyyy")), dataFinalPagamento.format(DateTimeFormatter.ofPattern("d/M/yyyy"))).replace(',', '.'));
        double valorDaComissao = consultaService.getComissaoSobreVendas(this, valorDasVendas);

        double salarioBruto;

        if (agenda.startsWith("semanal")) {
            // CORREÇÃO: Cálculo do salário fixo proporcional à frequência semanal.
            int frequencia = 1;
            if (agenda.split(" ").length == 3) {
                frequencia = Integer.parseInt(agenda.split(" ")[1]);
            } else if (getAgendaPagamento().getDescricao().equals("semanal 2 5")) {
                frequencia = 2; // Caso específico da agenda padrão quinzenal
            }
            double fixoProporcional = (salarioBase * 12 / 52.0) * frequencia;
            salarioBruto = fixoProporcional + valorDaComissao;
        } else { // mensal
            salarioBruto = salarioBase + valorDaComissao;
        }

        return Math.floor((salarioBruto * 100) + 1e-9) / 100.0;
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