package br.sptrans.scd.shared.security;

public final class CreditRequestPermissions {

    private static final String PREFIX = "SCD_WEB_API_PEDCR_";
    public static final String BUSCAR = PREFIX + "PEDIDOCREDITO_BUSCAR";
    public static final String CRIAR = PREFIX + "PEDIDOCREDITO_CRIAR";
    public static final String ALTERAR_STATUS_BLOQ = PREFIX + "PEDIDOCREDITO_ALTERAR_STATUS_BLOQ";
    public static final String ALTERAR_STATUS_CANC = PREFIX + "PEDIDOCREDITO_ALTERAR_STATUS_CANC";
    public static final String ALTERAR_STATUS_PAGO = PREFIX + "PEDIDOCREDITO_ALTERAR_STATUS_PAGO";
    public static final String SOBRE_CANAL = PREFIX + "PEDIDOCREDITO_SOBRE_CANAL";

    private CreditRequestPermissions() {
    }
}
