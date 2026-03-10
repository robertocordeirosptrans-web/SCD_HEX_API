package br.sptrans.scd.creditrequest.domain.enums;

/**
 * Define os modos de execução de busca baseados nos filtros fornecidos.
 *
 * <p>
 * A classificação dinâmica do modo de busca permite que o backend otimize a
 * estratégia de execução sem expor essa complexidade ao cliente.</p>
 *
 * <h3>SPECIFIC (Busca Específica)</h3>
 * <ul>
 * <li>Acionado quando filtros de alta seletividade são fornecidos
 * (numSolicitacao)</li>
 * <li>Retorna no máximo 1 registro</li>
 * <li>Paginação por cursor desabilitada</li>
 * <li>Caminho de execução rápido e determinístico</li>
 * </ul>
 *
 * <h3>OPERATIONAL (Busca Operacional)</h3>
 * <ul>
 * <li>Cenário mais comum: listagens recentes, filtros por negócio</li>
 * <li>Requer pelo menos um filtro de alta seletividade</li>
 * <li>Paginação por cursor habilitada</li>
 * <li>Filtros padrão: codCanal, codSituacao, dataSolicitacao</li>
 * <li>Limite padrão: 100 registros por página</li>
 * </ul>
 *
 * <h3>ANALYTICAL (Busca Analítica)</h3>
 * <ul>
 * <li>Para auditorias, investigações e análises pontuais</li>
 * <li>Requer múltiplos filtros seletivos</li>
 * <li>Limite reduzido: 50 registros por página</li>
 * <li>Políticas de timeout mais restritivas</li>
 * <li>Filtros complexos: numLote, codProduto, codLogin, intervalos de valores e
 * datas</li>
 * </ul>
 */
public enum SearchMode {
    /**
     * Busca Específica: Identificadores de alta seletividade (numSolicitacao).
     * Retorna registro único, sem paginação.
     */
    SPECIFIC,
    /**
     * Busca Operacional: Filtros de negócio padrão (canal, situação, datas).
     * Paginação habilitada, requer filtro de alta seletividade.
     */
    OPERATIONAL,
    /**
     * Busca Analítica: Consultas complexas com múltiplos filtros. Limites
     * reduzidos e políticas restritivas.
     */
    ANALYTICAL;

    /**
     * Retorna o limite máximo de registros por página para cada modo.
     */
    public int getMaxPageSize() {
        return switch (this) {
            case SPECIFIC ->
                1;
            case OPERATIONAL ->
                100;
            case ANALYTICAL ->
                50;
        };
    }

    /**
     * Indica se o modo suporta paginação por cursor.
     */
    public boolean supportsCursorPagination() {
        return this != SPECIFIC;
    }

    /**
     * Retorna descrição legível do modo de busca.
     */
    public String getDescription() {
        return switch (this) {
            case SPECIFIC ->
                "Busca Específica (Fast Path)";
            case OPERATIONAL ->
                "Busca Operacional (Padrão)";
            case ANALYTICAL ->
                "Busca Analítica (Complexa)";
        };
    }

}
