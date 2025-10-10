package br.ufal.ic.p2.wepayu.models;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;

public class AgendaPagamento {

    private String descricao;

    public AgendaPagamento() {}

    public AgendaPagamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

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

    @Override
    public AgendaPagamento clone() {
        return new AgendaPagamento(this.descricao);
    }
}