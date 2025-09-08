package br.ufal.ic.p2.wepayu.models;

import java.util.HashMap;
import java.util.Map;

public class EmpregadoHorista extends Empregado {

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

    @Override
    public Empregado clone() {
        EmpregadoHorista cloned = new EmpregadoHorista(this.getId(), this.getNome(), this.getEndereco(), this.getTipo(), this.getSalario());
        super.copy(cloned);
        cloned.setCartoesDePonto(new HashMap<>(this.getCartoesDePonto()));
        return cloned;
    }
}