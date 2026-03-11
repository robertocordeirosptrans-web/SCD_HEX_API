package br.sptrans.scd.creditrequest.application.port.in.dto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Codificador/decodificador de cursores opacos para paginação.
 * O cursor encapsula numSolicitacao e codCanal em Base64.
 */
public final class CursorCodec {

    private static final String SEPARATOR = "|";

    private CursorCodec() {
    }

    /**
     * Cria um cursor opaco a partir dos identificadores.
     */
    public static String createCursor(Long numSolicitacao, String codCanal) {
        if (numSolicitacao == null) {
            throw new IllegalArgumentException("numSolicitacao é obrigatório para criar cursor");
        }
        String raw = numSolicitacao + SEPARATOR + (codCanal != null ? codCanal : "");
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodifica um cursor opaco.
     */
    public static CursorState decode(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            throw new IllegalArgumentException("Cursor não pode ser nulo ou vazio");
        }
        try {
            String raw = new String(
                    Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            String[] parts = raw.split("\\|", -1);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Formato de cursor inválido");
            }
            Long numSolicitacao = Long.parseLong(parts[0]);
            String codCanal = parts[1].isEmpty() ? null : parts[1];
            return new CursorState(numSolicitacao, codCanal);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cursor contém numSolicitacao inválido", e);
        }
    }

    /**
     * Estado decodificado do cursor.
     */
    public record CursorState(Long numSolicitacao, String codCanal) {
    }
}
