package br.sptrans.scd.shared.security;

/**
 * Constantes de permissões do módulo Channel (CAD)
 * Formato: SCD_WEB_API_CAD_{ROTINA}_{FUNCIONALIDADE}
 */
public final class CadPermissions {

  

    private static final String PREFIX = "SCD_WEB_API_CAD_";

    // TYP - Tipo de Atividade
    public static final String TYP_CADTIPDEATI = PREFIX + "TYP_CADTIPDEATI";
    public static final String TYP_ATUTIPDEATI = PREFIX + "TYP_ATUTIPDEATI";
    public static final String TYP_BUSTIPDEATIPORCOD = PREFIX + "TYP_BUSTIPDEATIPORCOD";
    public static final String TYP_LISTIPDEATI = PREFIX + "TYP_LISTIPDEATI";
    public static final String TYP_ATITIPDEATI = PREFIX + "TYP_ATITIPDEATI";
    public static final String TYP_INATIPDEATI = PREFIX + "TYP_INATIPDEATI";
    public static final String TYP_REMTIPDEATI = PREFIX + "TYP_REMTIPDEATI";

    // SAL - Canal de Venda
    public static final String SAL_CADCANDEVEN = PREFIX + "SAL_CADCANDEVEN";
    public static final String SAL_ATUCANDEVEN = PREFIX + "SAL_ATUCANDEVEN";
    public static final String SAL_BUSCANDEVENPORCOD = PREFIX + "SAL_BUSCANDEVENPORCOD";
    public static final String SAL_LISCANDEVEN = PREFIX + "SAL_LISCANDEVEN";
    public static final String SAL_ATICANDEVEN = PREFIX + "SAL_ATICANDEVEN";
    public static final String SAL_INACANDEVEN = PREFIX + "SAL_INACANDEVEN";
    public static final String SAL_REMCANDEVEN = PREFIX + "SAL_REMCANDEVEN";

    // REC - Limite de Recarga
    public static final String REC_CADLIMDEREC = PREFIX + "REC_CADLIMDEREC";

    // PRO - Produto
    public static final String PRO_CADPRO = PREFIX + "PRO_CADPRO";
    public static final String PRO_ATUPRO = PREFIX + "PRO_ATUPRO";
    public static final String PRO_BUSPROPORCOD = PREFIX + "PRO_BUSPROPORCOD";
    public static final String PRO_LISPRO = PREFIX + "PRO_LISPRO";
    public static final String PRO_ATIPRO = PREFIX + "PRO_ATIPRO";
    public static final String PRO_INAPRO = PREFIX + "PRO_INAPRO";

    // PRO - Tipo de Produto
    public static final String PRO_CADTIPDEPRO = PREFIX + "PRO_CADTIPDEPRO";
    public static final String PRO_ATUTIPDEPRO = PREFIX + "PRO_ATUTIPDEPRO";
    public static final String PRO_BUSTIPDEPROPORCOD = PREFIX + "PRO_BUSTIPDEPROPORCOD";
    public static final String PRO_LISTIPDEPRO = PREFIX + "PRO_LISTIPDEPRO";
    public static final String PRO_ATITIPDEPRO = PREFIX + "PRO_ATITIPDEPRO";
    public static final String PRO_INATIPDEPRO = PREFIX + "PRO_INATIPDEPRO";
    public static final String PRO_REMTIPDEPRO = PREFIX + "PRO_REMTIPDEPRO";

    // TEC - Tecnologia
    public static final String TEC_CADTEC = PREFIX + "TEC_CADTEC";
    public static final String TEC_ATUTEC = PREFIX + "TEC_ATUTEC";
    public static final String TEC_BUSTECPORCOD = PREFIX + "TEC_BUSTECPORCOD";
    public static final String TEC_LISTEC = PREFIX + "TEC_LISTEC";
    public static final String TEC_ATITEC = PREFIX + "TEC_ATITEC";
    public static final String TEC_INATEC = PREFIX + "TEC_INATEC";
    public static final String TEC_REMTEC = PREFIX + "TEC_REMTEC";

    // SPE - Espécie
    public static final String SPE_CADESP = PREFIX + "SPE_CADESP";
    public static final String SPE_ATUESP = PREFIX + "SPE_ATUESP";
    public static final String SPE_BUSESPPORCOD = PREFIX + "SPE_BUSESPPORCOD";
    public static final String SPE_LISESP = PREFIX + "SPE_LISESP";
    public static final String SPE_ATIESP = PREFIX + "SPE_ATIESP";
    public static final String SPE_INAESP = PREFIX + "SPE_INAESP";
    public static final String SPE_REMESP = PREFIX + "SPE_REMESP";

    // MOD - Modalidade
    public static final String MOD_CADMOD = PREFIX + "MOD_CADMOD";
    public static final String MOD_ATUMOD = PREFIX + "MOD_ATUMOD";
    public static final String MOD_BUSMODPORCOD = PREFIX + "MOD_BUSMODPORCOD";
    public static final String MOD_LISMOD = PREFIX + "MOD_LISMOD";
    public static final String MOD_ATIMOD = PREFIX + "MOD_ATIMOD";
    public static final String MOD_INAMOD = PREFIX + "MOD_INAMOD";
    public static final String MOD_REMMOD = PREFIX + "MOD_REMMOD";
    // FAM - Família
    public static final String FAM_CADFAM = PREFIX + "FAM_CADFAM";
    public static final String FAM_ATUFAM = PREFIX + "FAM_ATUFAM";
    public static final String FAM_BUSFAMPORCOD = PREFIX + "FAM_BUSFAMPORCOD";
    public static final String FAM_LISFAM = PREFIX + "FAM_LISFAM";
    public static final String FAM_ATIFAM = PREFIX + "FAM_ATIFAM";
    public static final String FAM_INAFAM = PREFIX + "FAM_INAFAM";
    public static final String FAM_REMFAM = PREFIX + "FAM_REMFAM";

      // FEE - Tarifa e Taxa
    public static final String FEE_CADTAR = PREFIX + "FEE_CADTAR";
    public static final String FEE_ATUTAR = PREFIX + "FEE_ATUTAR";
    public static final String FEE_BUSTAR = PREFIX + "FEE_BUSTAR";
    public static final String FEE_REMTAR = PREFIX + "FEE_REMTAR";
    public static final String FEE_CADTAX = PREFIX + "FEE_CADTAX";
    public static final String FEE_ATUTAX = PREFIX + "FEE_ATUTAX";
    public static final String FEE_BUSTAX = PREFIX + "FEE_BUSTAX";
    public static final String FEE_REMTAX = PREFIX + "FEE_REMTAX";

        // END - Endereço do Canal
    public static final String END_CADEND = PREFIX + "END_CADEND";
    public static final String END_ATUEND = PREFIX + "END_ATUEND";
    public static final String END_BUSENDPORCOD = PREFIX + "END_BUSENDPORCOD";
    public static final String END_LISEND = PREFIX + "END_LISEND";
    public static final String END_REMEND = PREFIX + "END_REMEND";

    // VIG - Vigência de Convênio
    public static final String VIG_CADVIG = PREFIX + "VIG_CADVIG";
    public static final String VIG_ATUVIG = PREFIX + "VIG_ATUVIG";
    public static final String VIG_BUSVIGPORCOD = PREFIX + "VIG_BUSVIGPORCOD";
    public static final String VIG_LISVIG = PREFIX + "VIG_LISVIG";
    public static final String VIG_REMVIG = PREFIX + "VIG_REMVIG";

    // CON - Contato do Canal
    public static final String CON_CADCON = PREFIX + "CON_CADCON";
    public static final String CON_ATUCON = PREFIX + "CON_ATUCON";
    public static final String CON_BUSCONPORCOD = PREFIX + "CON_BUSCONPORCOD";
    public static final String CON_LISCON = PREFIX + "CON_LISCON";
    public static final String CON_REMCON = PREFIX + "CON_REMCON";

    // ASSCOMERDIST - Comercialização/Distribuição
    public static final String ASSCOMDIST_CAD = PREFIX + "ASSCOMDIST_CAD";
    public static final String ASSCOMDIST_ATU = PREFIX + "ASSCOMDIST_ATU";
    public static final String ASSCOMDIST_BUSCOD = PREFIX + "ASSCOMDIST_BUSCOD";
    public static final String ASSCOMDIST_LIS = PREFIX + "ASSCOMDIST_LIS";
    public static final String ASSCOMDIST_REM = PREFIX + "ASSCOMDIST_REM";

    // ASS - Canal de Produto
    public static final String ASS_CADASS = PREFIX + "ASS_CADASS";
    public static final String ASS_ATUASS = PREFIX + "ASS_ATUASS";
    public static final String ASS_BUSASSPORCOD = PREFIX + "ASS_BUSASSPORCOD";
    public static final String ASS_LISASS = PREFIX + "ASS_LISASS";
    public static final String ASS_REMASS = PREFIX + "ASS_REMASS";

    // LIMIT - Limite de Recarga
    public static final String LIMIT_CADLIMIT = PREFIX + "LIMIT_CADLIMIT";
    public static final String LIMIT_ATULIMIT = PREFIX + "LIMIT_ATULIMIT";
    public static final String LIMIT_BUSLIMITCOD = PREFIX + "LIMIT_BUSLIMITCOD";
    public static final String LIMIT_LISLIMIT = PREFIX + "LIMIT_LISLIMIT";
    public static final String LIMIT_REMLIMIT = PREFIX + "LIMIT_REMLIMIT";
}
