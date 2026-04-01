package br.sptrans.scd.auth.domain.vo;

import java.time.LocalTime;

import lombok.Value;

/**
 * Value Object imutável: intervalo de horário (inicio/fim).
 */
@Value
public class TimeRange {

    LocalTime inicio;
    LocalTime fim;

    public TimeRange(LocalTime inicio, LocalTime fim) {
        if (inicio != null && fim != null && !inicio.isBefore(fim)) {
            throw new IllegalArgumentException(
                    String.format("Hora de início (%s) deve ser anterior ao fim (%s)", inicio, fim));
        }
        this.inicio = inicio;
        this.fim = fim;
    }

    /**
     * Verifica se o horário está dentro do intervalo (inclusive).
     * Se inicio ou fim forem nulos, sem restrição.
     */
    public boolean contemHora(LocalTime hora) {
        if (inicio == null || fim == null) {
            return true;
        }
        return !hora.isBefore(inicio) && !hora.isAfter(fim);
    }

    /** Sem restrição de horário (dia todo). */
    public static TimeRange semRestricao() {
        return new TimeRange(null, null);
    }

    /** Horário comercial padrão 08h-18h. */
    public static TimeRange horarioComercial() {
        return new TimeRange(LocalTime.of(8, 0), LocalTime.of(18, 0));
    }
}
