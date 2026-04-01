package br.sptrans.scd.auth.domain.vo;

import java.time.DayOfWeek;

import jakarta.validation.constraints.Pattern;
import lombok.Value;

/**
 * Value Object imutável: padrão de dias da semana permitidos.
 * Formato: String de 7 caracteres '0'/'1' — índice 0=Domingo ... 6=Sábado.
 * Exemplo: "0111110" = Segunda a Sexta.
 */
@Value
public class DayPattern {

    private static final int DIAS_NA_SEMANA = 7;
    private static final char PERMITIDO = '1';

    @Pattern(regexp = "[01]{7}", message = "padrão de dias deve conter exatamente 7 caracteres 0 ou 1")
    String padrao;

    public DayPattern(String padrao) {
        if (padrao == null || padrao.length() != DIAS_NA_SEMANA) {
            throw new IllegalArgumentException(
                    String.format("Padrão de dias deve ter exatamente %d caracteres, recebido: '%s'",
                            DIAS_NA_SEMANA, padrao));
        }
        this.padrao = padrao;
    }

    /**
     * Verifica se o dia da semana informado está permitido.
     * {@link DayOfWeek}: MONDAY=1 ... SUNDAY=7. Índice no padrão: (valor % 7).
     */
    public boolean contemDia(DayOfWeek dia) {
        int indice = dia.getValue() % DIAS_NA_SEMANA;
        return padrao.charAt(indice) == PERMITIDO;
    }

    /** Segunda a Sexta. */
    public static DayPattern diasUteis() {
        return new DayPattern("0111110");
    }

    /** Todos os dias da semana. */
    public static DayPattern todosDias() {
        return new DayPattern("1111111");
    }
}
