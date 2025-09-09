package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class EmpregadoHorista extends Empregado implements Serializable {

    private Map<String, CartaoDePonto> cartoesDePonto;

    public EmpregadoHorista() {
        this.cartoesDePonto = new HashMap<>();
    }

    public EmpregadoHorista(String id, String nome, String endereco, String tipo, String salario) {
        super(id, nome, endereco, tipo, salario);
        this.cartoesDePonto = new HashMap<>();
    }

    public void lancaCartao(CartaoDePonto cartao) {
        this.cartoesDePonto.put(cartao.getData(), cartao);
    }

    public Map<String, CartaoDePonto> getCartoesDePonto() {
        return this.cartoesDePonto;
    }

    public void setCartoesDePonto(Map<String, CartaoDePonto> cartoesDePonto) {
        this.cartoesDePonto = cartoesDePonto;
    }

    // Dentro da classe EmpregadoHorista

    @Override
    public Empregado clone() {
        EmpregadoHorista cloned = new EmpregadoHorista(this.getId(), this.getNome(), this.getEndereco(), this.getTipo(), this.getSalario());
        super.copy(cloned);

        // --- Início da Cópia Profunda do Mapa de Cartões de Ponto ---
        Map<String, CartaoDePonto> newCartoesMap = new HashMap<>();
        if (this.cartoesDePonto != null) {
            for (Map.Entry<String, CartaoDePonto> entry : this.cartoesDePonto.entrySet()) {
                CartaoDePonto originalCartao = entry.getValue();
                // Cria nova instância de CartaoDePonto.
                newCartoesMap.put(entry.getKey(), new CartaoDePonto(originalCartao.getData(), originalCartao.getHoras()));
            }
        }
        cloned.setCartoesDePonto(newCartoesMap);
        // --- Fim da Cópia Profunda ---

        return cloned;
    }
}