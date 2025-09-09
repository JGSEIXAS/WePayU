package br.ufal.ic.p2.wepayu.models;

import java.util.HashMap;
import java.util.Map;

public class MembroSindicato {
    private String idMembro;
    private double taxaSindical;
    private Map<String, TaxaServico> taxasDeServico;

    public MembroSindicato() { this.taxasDeServico = new HashMap<>(); }
    public MembroSindicato(String idMembro, double taxaSindical) {
        this.idMembro = idMembro;
        this.taxaSindical = taxaSindical;
        this.taxasDeServico = new HashMap<>();
    }

    public MembroSindicato clone() {
        MembroSindicato cloned = new MembroSindicato(this.idMembro, this.taxaSindical);
        // Cópia profunda do mapa de taxas de serviço
        Map<String, TaxaServico> taxasCopia = new HashMap<>();
        for (Map.Entry<String, TaxaServico> entry : this.taxasDeServico.entrySet()) {
            TaxaServico original = entry.getValue();
            taxasCopia.put(entry.getKey(), new TaxaServico(original.getData(), original.getValor()));
        }
        cloned.setTaxasDeServico(taxasCopia);
        return cloned;
    }

    public void lancaTaxaServico(TaxaServico taxa) {
        this.taxasDeServico.put(taxa.getData(), taxa);
    }

    // Getters e Setters
    public String getIdMembro() { return idMembro; }
    public void setIdMembro(String idMembro) { this.idMembro = idMembro; }
    public double getTaxaSindical() { return taxaSindical; }
    public void setTaxaSindical(double taxaSindical) { this.taxaSindical = taxaSindical; }
    public Map<String, TaxaServico> getTaxasDeServico() { return taxasDeServico; }
    public void setTaxasDeServico(Map<String, TaxaServico> taxasDeServico) { this.taxasDeServico = taxasDeServico; }
}