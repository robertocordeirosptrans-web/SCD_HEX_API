package br.sptrans.scd.creditrequest.domain.service;

import br.sptrans.scd.creditrequest.domain.enums.SearchMode;

/**
 * Classificador de modo de busca sem dependências de DTOs ou Spring.
 */
public class SearchModeClassifier {

    /**
     * Classifica o modo de busca baseado nos filtros primitivos.
     *
     * @return O modo de busca apropriado
     */
    public SearchMode classify(Long numSolicitacao, String codCanal, String codSituacao,
                               String numLote, String codProduto, String codLogin,
                               String codFormaPagto, Number vlTotalMin, Number vlTotalMax,
                               Object dtInicio, Object dtFim,
                               Object dtLiberacaoEfetivaInicio, Object dtLiberacaoEfetivaFim,
                               Object dtPagtoEconomicaInicio, Object dtPagtoEconomicaFim,
                               Object dtFinanceiraInicio, Object dtFinanceiraFim,
                               Object dtAlteracaoInicio, Object dtAlteracaoFim) {
        // SPECIFIC
        if (numSolicitacao != null) {
            return SearchMode.SPECIFIC;
        }
        int complexFilterCount = 0;
        if (numLote != null) complexFilterCount++;
        if (codProduto != null) complexFilterCount++;
        if (codLogin != null) complexFilterCount++;
        if (codFormaPagto != null) complexFilterCount++;
        if (vlTotalMin != null || vlTotalMax != null) complexFilterCount++;
        if (dtLiberacaoEfetivaInicio != null || dtLiberacaoEfetivaFim != null) complexFilterCount++;
        if (dtPagtoEconomicaInicio != null || dtPagtoEconomicaFim != null) complexFilterCount++;
        if (dtFinanceiraInicio != null || dtFinanceiraFim != null) complexFilterCount++;
        if (dtAlteracaoInicio != null || dtAlteracaoFim != null) complexFilterCount++;
        boolean hasOperationalFilters = codCanal != null || codSituacao != null || dtInicio != null || dtFim != null;
        if (complexFilterCount >= 2) {
            return SearchMode.ANALYTICAL;
        }
        if (hasOperationalFilters || complexFilterCount == 1) {
            return SearchMode.OPERATIONAL;
        }
        return SearchMode.OPERATIONAL;
    }
}
