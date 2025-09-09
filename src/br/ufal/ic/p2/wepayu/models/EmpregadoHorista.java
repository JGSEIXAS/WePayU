package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;
import br.ufal.ic.p2.wepayu.Services.ConsultaService;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe que representa um empregado que recebe por hora trabalhada.
 * Armazena os cartões de ponto associados a este empregado.
 */
public class EmpregadoHorista extends Empregado implements Serializable {

    private Map<String, CartaoDePonto> cartoesDePonto;

    /**
     * Construtor padrão que inicializa a lista de cartões de ponto.
     */
    public EmpregadoHorista() {
        this.cartoesDePonto = new HashMap<>();
    }

    /**
     * Constrói uma instância de EmpregadoHorista.
     * @param id ID único do empregado.
     * @param nome Nome do empregado.
     * @param endereco Endereço do empregado.
     * @param tipo Tipo do empregado (deve ser "horista").
     * @param salario Valor da hora de trabalho.
     */
    public EmpregadoHorista(String id, String nome, String endereco, String tipo, String salario) {
        super(id, nome, endereco, tipo, salario);
        this.cartoesDePonto = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Calcula o salário bruto com base nas horas normais e extras trabalhadas no período.
     * Horas extras são pagas com um adicional de 50%.
     */
    @Override
    public double calcularSalarioBruto(LocalDate dataFolha, ConsultaService consultaService) throws ValidacaoException, EmpregadoNaoExisteException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        String dataInicialStr = getDataUltimoPagamento().plusDays(1).format(formatter);
        String dataFinalStr = dataFolha.plusDays(1).format(formatter);

        double horasNormais = Double.parseDouble(consultaService.getHorasNormaisTrabalhadas(getId(), dataInicialStr, dataFinalStr).replace(',', '.'));
        double horasExtras = Double.parseDouble(consultaService.getHorasExtrasTrabalhadas(getId(), dataInicialStr, dataFinalStr).replace(',', '.'));
        double taxaHoraria = Double.parseDouble(getSalarioSemFormato().replace(',', '.'));

        double salario = (horasNormais * taxaHoraria) + (horasExtras * taxaHoraria * 1.5);
        return Math.floor((salario * 100) + 1e-9) / 100.0;
    }

    /**
     * Adiciona um novo cartão de ponto para este empregado.
     * @param cartao O objeto {@link CartaoDePonto} a ser adicionado.
     */
    public void lancaCartao(CartaoDePonto cartao) {
        this.cartoesDePonto.put(cartao.getData(), cartao);
    }

    // Getters e Setters
    public Map<String, CartaoDePonto> getCartoesDePonto() {
        return this.cartoesDePonto;
    }

    public void setCartoesDePonto(Map<String, CartaoDePonto> cartoesDePonto) {
        this.cartoesDePonto = cartoesDePonto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Empregado clone() {
        EmpregadoHorista cloned = new EmpregadoHorista(this.getId(), this.getNome(), this.getEndereco(), this.getTipo(), this.getSalario());
        super.copy(cloned);
        Map<String, CartaoDePonto> newCartoesMap = new HashMap<>();
        if (this.cartoesDePonto != null) {
            for (Map.Entry<String, CartaoDePonto> entry : this.cartoesDePonto.entrySet()) {
                CartaoDePonto originalCartao = entry.getValue();
                newCartoesMap.put(entry.getKey(), new CartaoDePonto(originalCartao.getData(), originalCartao.getHoras()));
            }
        }
        cloned.setCartoesDePonto(newCartoesMap);
        return cloned;
    }
}
