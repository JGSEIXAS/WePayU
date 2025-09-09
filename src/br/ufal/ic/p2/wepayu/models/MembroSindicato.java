package br.ufal.ic.p2.wepayu.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe que representa a afiliação de um empregado a um sindicato.
 * Armazena o ID do membro, a taxa sindical regular e as taxas de serviço adicionais.
 */
public class MembroSindicato {
    private String idMembro;
    private double taxaSindical;
    private Map<String, TaxaServico> taxasDeServico;

    /**
     * Construtor padrão que inicializa a lista de taxas de serviço.
     */
    public MembroSindicato() { this.taxasDeServico = new HashMap<>(); }

    /**
     * Constrói uma instância de MembroSindicato.
     * @param idMembro O ID único do membro no sindicato.
     * @param taxaSindical A taxa sindical a ser cobrada.
     */
    public MembroSindicato(String idMembro, double taxaSindical) {
        this.idMembro = idMembro;
        this.taxaSindical = taxaSindical;
        this.taxasDeServico = new HashMap<>();
    }

    /**
     * Cria uma cópia profunda (clone) do objeto MembroSindicato.
     * @return Uma nova instância de {@link MembroSindicato} com os mesmos dados.
     */
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

    /**
     * Adiciona uma nova taxa de serviço para este membro.
     * @param taxa O objeto {@link TaxaServico} a ser adicionado.
     */
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
