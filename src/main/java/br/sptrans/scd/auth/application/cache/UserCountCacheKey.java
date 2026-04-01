package br.sptrans.scd.auth.application.cache;

/**
 * Chave de cache imutável para contagem de usuários com filtros.
 * O toString() é determinístico e gerado automaticamente pelo record.
 */
public record UserCountCacheKey(
        String codStatus,
        String nomUsuario,
        String nomEmail,
        String codPerfil) {

    // Record gera automaticamente:
    // - equals()
    // - hashCode()
    // - toString() determinístico
}
