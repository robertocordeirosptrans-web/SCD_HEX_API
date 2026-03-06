package br.sptrans.scd.auth.domain;

import java.util.Calendar;
import java.util.Date;

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
     * Valida se o dia atual é permitido.
     * diasPermitidos é uma string de 7 caracteres ('0' ou '1'),
     * onde cada posição representa um dia: índice 0=Domingo, 1=Segunda, ..., 6=Sábado.
     * Ex: "1111111" = todos os dias, "0111110" = segunda a sexta.
     */
    private boolean validarDiaSemana() {
        if (diasPermitidos == null || diasPermitidos.isBlank()) {
            return true; // sem restrição de dia
        }
        if (diasPermitidos.length() != 7) {
            return true; // formato inválido, sem restrição
        }
        // Calendar.DAY_OF_WEEK: 1=Domingo, 2=Segunda, ..., 7=Sábado → índice 0..6
        int indice = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        return diasPermitidos.charAt(indice) == '1';
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
