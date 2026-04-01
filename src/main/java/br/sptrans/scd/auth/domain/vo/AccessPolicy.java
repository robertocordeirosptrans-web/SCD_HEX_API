package br.sptrans.scd.auth.domain.vo;

import java.time.LocalDateTime;

import lombok.Value;

/**
 * Value Object imutável: política de acesso por jornada de trabalho.
 * Encapsula dias permitidos ({@link DayPattern}) e intervalo de horário ({@link TimeRange}).
 */
@Value
public class AccessPolicy {

    DayPattern diasPermitidos;
    TimeRange jornadaHoraria;

    /**
     * Verifica se o acesso é permitido para o momento informado.
     */
    public boolean isAcessoPermitidoEm(LocalDateTime dataHora) {
        return diasPermitidos.contemDia(dataHora.getDayOfWeek())
                && jornadaHoraria.contemHora(dataHora.toLocalTime());
    }

    /** Política sem nenhuma restrição (todos os dias, qualquer horário). */
    public static AccessPolicy semRestricao() {
        return new AccessPolicy(DayPattern.todosDias(), TimeRange.semRestricao());
    }

    /** Política padrão: dias úteis em horário comercial. */
    public static AccessPolicy diasUteis() {
        return new AccessPolicy(DayPattern.diasUteis(), TimeRange.horarioComercial());
    }
}
