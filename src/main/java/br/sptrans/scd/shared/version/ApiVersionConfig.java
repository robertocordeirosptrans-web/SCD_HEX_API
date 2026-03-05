package br.sptrans.scd.shared.version;

/**
 * Configuração centralizada de versionamento da API
 * Define as versões disponíveis e padrões
 */
public final class ApiVersionConfig {
    
    private ApiVersionConfig() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Versão atual da API (mais recente)
     */
    public static final String CURRENT_VERSION = "v1";
    
    /**
     * Prefixo base para todas as APIs
     */
    public static final String API_BASE_PATH = "/api";
    
    /**
     * Path completo para API v1
     */
    public static final String API_V1_PATH = API_BASE_PATH + "/v1";
    
    /**
     * Versões suportadas da API
     */
    public static final String[] SUPPORTED_VERSIONS = {"v1"};
    
    /**
     * Versão padrão quando não especificada
     */
    public static final String DEFAULT_VERSION = "v1";
}

