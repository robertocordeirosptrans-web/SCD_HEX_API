package br.sptrans.scd.auth.domain.vo;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AccessPolicy - Validação de Jornada")
public class AccessPolicyTest {

    private DayPattern businessDays;
    private TimeRange businessHours;
    private AccessPolicy accessPolicy;

    @BeforeEach
    void setUp() {
        businessDays = DayPattern.diasUteis();        // "0111110"
        businessHours = TimeRange.horarioComercial(); // 08:00-18:00
        accessPolicy = new AccessPolicy(businessDays, businessHours);
    }

    @Test
    @DisplayName("✓ Deve permitir acesso em dia útil dentro do horário")
    void shouldAllowAccessOnBusinessHoursDuringWeekday() {
        LocalDateTime wednesdayAt10am = LocalDateTime.of(
                2026, 4, 1, 10, 0 // Quarta-feira 10:00
        );

        assertTrue(accessPolicy.isAcessoPermitidoEm(wednesdayAt10am));
    }

    @Test
    @DisplayName("✗ Deve bloquear acesso no fim de semana")
    void shouldBlockAccessOnWeekend() {
        LocalDateTime sundayAt10am = LocalDateTime.of(
                2026, 3, 29, 10, 0 // Domingo 10:00
        );

        assertFalse(accessPolicy.isAcessoPermitidoEm(sundayAt10am));
    }

    @Test
    @DisplayName("✗ Deve bloquear acesso fora do horário")
    void shouldBlockAccessOutsideBusinessHours() {
        LocalDateTime wednesdayAt11pm = LocalDateTime.of(
                2026, 4, 1, 23, 0 // Quarta 23:00
        );

        assertFalse(accessPolicy.isAcessoPermitidoEm(wednesdayAt11pm));
    }

    @Test
    @DisplayName("✗ Deve bloquear acesso antes do horário")
    void shouldBlockAccessBeforeBusinessHours() {
        LocalDateTime wednesdayAt7am = LocalDateTime.of(
                2026, 4, 1, 7, 0 // Quarta 07:00
        );

        assertFalse(accessPolicy.isAcessoPermitidoEm(wednesdayAt7am));
    }
}
