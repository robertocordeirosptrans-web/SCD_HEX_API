package br.sptrans.scd.auth.application.cache;

/**
 * Chave de cache imutável para listagem de usuários com filtros.
 * O toString() é determinístico e gerado automaticamente pelo record.
 */
public record UserListCacheKey(
        String codStatus,
        String nomUsuario,
        String nomEmail,
        String codPerfil,
        int page,
        int size,
        String sortBy,
        String sortDir) {

    // Record gera automaticamente:
    // - equals()
    // - hashCode()
    // - toString() determinístico
}
