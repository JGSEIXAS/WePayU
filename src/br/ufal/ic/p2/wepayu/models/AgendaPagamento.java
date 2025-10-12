package br.ufal.ic.p2.wepayu.models;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;

/**
 * Representa a agenda de pagamento de um empregado, definindo a frequência e o dia do pagamento.
 */
public class AgendaPagamento {

    private String descricao;

    /**
     * Construtor padrão.
     */
    public AgendaPagamento() {}

    /**
     * Constrói uma AgendaPagamento com uma descrição.
     * @param descricao A descrição da agenda (ex: "semanal 5", "mensal $").
     */
    public AgendaPagamento(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Retorna a descrição da agenda.
     * @return A descrição da agenda.
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Define a descrição da agenda.
     * @param descricao A nova descrição.
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Verifica se uma data específica é um dia de pagamento de acordo com esta agenda.
     * @param data A data a ser verificada.
     * @return {@code true} se for um dia de pagamento, {@code false} caso contrário.
     */
    public boolean isDiaDePagamento(LocalDate data) { // LÓGICA ATUALIZADA
        String[] parts = descricao.split(" ");
        String tipo = parts[0];

        if (tipo.equalsIgnoreCase("mensal")) {
            String dia = parts[1];
            if (dia.equals("$")) {
                LocalDate ultimoDiaUtil = data.with(TemporalAdjusters.lastDayOfMonth());
                while (ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SATURDAY || ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    ultimoDiaUtil = ultimoDiaUtil.minusDays(1);
                }
                return data.equals(ultimoDiaUtil);
            } else {
                return data.getDayOfMonth() == Integer.parseInt(dia);
            }
        } else if (tipo.equalsIgnoreCase("semanal")) {
            int frequencia, diaDaSemana;
            if (parts.length == 2) { // Ex: "semanal 5"
                frequencia = 1;
                diaDaSemana = Integer.parseInt(parts[1]);
            } else { // Ex: "semanal 2 5"
                frequencia = Integer.parseInt(parts[1]);
                diaDaSemana = Integer.parseInt(parts[2]);
            }

            if (data.getDayOfWeek().getValue() != diaDaSemana) {
                return false;
            }

            // Lógica Unificada: usa uma data de referência consistente
            LocalDate dataReferencia = LocalDate.of(2005, 1, 7).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(diaDaSemana)));

            if (frequencia > 1) {
                dataReferencia = dataReferencia.plusWeeks(frequencia - 1);
            }

            if (data.isBefore(dataReferencia)) return false;

            long semanasDesdeReferencia = ChronoUnit.WEEKS.between(dataReferencia, data);
            return semanasDesdeReferencia % frequencia == 0;
        }
        return false;
    }

    /**
     * Clona o objeto AgendaPagamento.
     * @return Uma nova instância de {@link AgendaPagamento} com a mesma descrição.
     */
    @Override
    public AgendaPagamento clone() {
        return new AgendaPagamento(this.descricao);
    }
}