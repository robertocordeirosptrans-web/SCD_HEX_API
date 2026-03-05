package br.sptrans.scd.auth.domain;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Objeto de Valor: regras de acesso por jornada. Encapsula a lógica de
 * validação de DT_JORNADA_INI, DT_JORNADA_FIM e NUM_DIAS_SEMANAS_PERMITIDOS da
 * tabela USUARIOS.
 *
 * Dias da semana no padrão Calendar: 1=Domingo, 2=Segunda, ..., 7=Sábado.
 */
public class AccessPolicy {

    private final String diasPermitidos;  // ex: "2,3,4,5,6" = seg-sex
    private final Date jornadaInicio;
    private final Date jornadaFim;

    public AccessPolicy(String diasPermitidos, Date jornadaInicio, Date jornadaFim) {
        this.diasPermitidos = diasPermitidos;
        this.jornadaInicio = jornadaInicio;
        this.jornadaFim = jornadaFim;
    }

    public boolean validarAcesso() {
        return validarDiaSemana() && validarHorario();
    }

    /**
     * Valida se o momento atual está dentro da jornada permitida. Retorna true
     * se não houver restrição configurada (campos nulos).
     */
    private boolean validarDiaSemana() {
        if (diasPermitidos == null || diasPermitidos.isBlank()) {
            return true; // sem restrição de dia
        }
        Set<Integer> diasSet = Arrays.stream(diasPermitidos.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toSet());

        int diaHoje = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        return diasSet.contains(diaHoje);
    }

    private boolean validarHorario() {
        if (jornadaInicio == null || jornadaFim == null) {
            return true; // sem restrição de horário
        }
        Calendar agora = Calendar.getInstance();
        Calendar inicio = Calendar.getInstance();
        Calendar fim = Calendar.getInstance();

        inicio.setTime(jornadaInicio);
        fim.setTime(jornadaFim);

        // Comparação apenas de hora/minuto
        int minutosAgora = agora.get(Calendar.HOUR_OF_DAY) * 60 + agora.get(Calendar.MINUTE);
        int minutosInicio = inicio.get(Calendar.HOUR_OF_DAY) * 60 + inicio.get(Calendar.MINUTE);
        int minutosFim = fim.get(Calendar.HOUR_OF_DAY) * 60 + fim.get(Calendar.MINUTE);

        return minutosAgora >= minutosInicio && minutosAgora <= minutosFim;
    }
}
