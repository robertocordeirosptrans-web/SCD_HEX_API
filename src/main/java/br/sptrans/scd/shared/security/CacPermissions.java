package br.sptrans.scd.shared.security;

/**
 * Constantes de permissão do módulo CAC (Controle de Acesso e Cadastro).
 * Formato: SCD_WEB_API_{COD_MODULO}_{COD_ROTINA}_{COD_FUNCIONALIDADE}
 *
 * Fonte: permissoes_abreviadas.csv — COD_MODULO = CAC
 */
public final class CacPermissions {

    private static final String PREFIX = "SCD_WEB_API_CAC_";

    // Gerenciamento de sessões de usuarios
    /** Revogar sessão específica */
    public static final String REVSSESS = PREFIX + "SES_REVSSESS";
    /** Revogar todas as sessões de um usuário */
    public static final String REVSALLUSERSESS = PREFIX + "SES_REVSALLUSERSESS";
    /** Listar sessões ativas de um usuário */
    public static final String LISTACTSESS = PREFIX + "SES_LISTACTSESS";

    // ── COD_ROTINA: GRP — Grupos ────────────────────────────────────────────
    /** Listar grupos */
    public static final String LISGRU = PREFIX + "GRP_LISGRU";
    /** Buscar grupo por código */
    public static final String BUSGRUPORCOD = PREFIX + "GRP_BUSGRUPORCOD";
    /** Cadastrar grupo */
    public static final String CADGRU = PREFIX + "GRP_CADGRU";
    /** Atualizar grupo */
    public static final String ATUGRU = PREFIX + "GRP_ATUGRU";
    /** Remover grupo */
    public static final String REMGRU = PREFIX + "GRP_REMGRU";

    // ── COD_ROTINA: USE — Usuários ────────────────────────────────────────────
    /** Listar usuários */
    public static final String LISUSU          = PREFIX + "USE_LISUSU";
    /** Reset de senha (administrativo) */
    public static final String RESET_PASSWORD  = PREFIX + "USE_RESETPASS";

    // ── COD_ROTINA: USU — Associações Usuário-Perfil ─────────────────────────
    /** Listar associações usuário-perfil */
    public static final String LISASSUSUPER    = PREFIX + "USU_LISASSUSUPER";
    /** Associar perfil ao usuário */
    public static final String ASSPERAOUSU     = PREFIX + "USU_ASSPERAOUSU";

    // ── COD_ROTINA: PER — Perfis e Funcionalidades ───────────────────────────
    /** Criar perfil */
    public static final String CRIPER          = PREFIX + "PER_CRIPER";
    /** Listar associações perfil-funcionalidade */
    public static final String LISASSPERFUN    = PREFIX + "PER_LISASSPERFUN";
    /** Associar funcionalidade ao perfil */
    public static final String ASSFUNAOPER     = PREFIX + "PER_ASSFUNAOPER";

    private CacPermissions() {}
}
