package br.sptrans.scd.auth.domain.vo;

import java.time.LocalDateTime;


import lombok.RequiredArgsConstructor;


/**
 * Objeto de Valor: regras de acesso por jornada. Encapsula a lógica de
 * validação de DT_JORNADA_INI, DT_JORNADA_FIM e NUM_DIAS_SEMANAS_PERMITIDOS da
 * tabela USUARIOS.
 *
 * Dias da semana no padrão Calendar: 1=Domingo, 2=Segunda, ..., 7=Sábado.
 */

@RequiredArgsConstructor
public class AccessPolicy {

    private static final int DAYS_IN_WEEK = 7;
    private static final char ALLOWED = '1';
    private static final char BLOCKED = '0';
    
    private final DayPattern allowedDays;
    private final TimeRange journeyHours;
    
    public boolean isAccessAllowedAt(LocalDateTime dateTime) {
        return allowedDays.contains(dateTime.getDayOfWeek())
            && journeyHours.contains(dateTime.toLocalTime());
    }
}
