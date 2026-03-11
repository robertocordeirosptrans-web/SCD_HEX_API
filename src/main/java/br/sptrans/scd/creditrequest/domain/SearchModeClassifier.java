package br.sptrans.scd.creditrequest.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.sptrans.scd.creditrequest.application.port.in.dto.CursorPageRequest;
import br.sptrans.scd.creditrequest.domain.enums.SearchMode;

@Component
public class SearchModeClassifier {

    private static final Logger log = LoggerFactory.getLogger(SearchModeClassifier.class);

    /**
     * Classifica o modo de busca baseado nos filtros do request.
     *
     * @param request Request contendo os filtros de busca
     * @return O modo de busca apropriado
     */
    public SearchMode classify(CursorPageRequest request) {

        // SPECIFIC: Quando numSolicitacao é fornecido
        if (request.getNumSolicitacao() != null) {
            log.debug("Classificado como SPECIFIC: numSolicitacao={}", request.getNumSolicitacao());
            return SearchMode.SPECIFIC;
        }

        // Contar filtros complexos para ANALYTICAL
        int complexFilterCount = countComplexFilters(request);

        // ANALYTICAL: Quando há múltiplos filtros complexos
        if (complexFilterCount >= 2) {
            log.debug("Classificado como ANALYTICAL: {} filtros complexos", complexFilterCount);
            return SearchMode.ANALYTICAL;
        }

        // Verificar se há filtros operacionais
        boolean hasOperationalFilters = hasOperationalFilters(request);

        // OPERATIONAL: Quando há pelo menos um filtro operacional
        if (hasOperationalFilters || complexFilterCount == 1) {
            log.debug("Classificado como OPERATIONAL: filtros operacionais ou 1 filtro complexo");
            return SearchMode.OPERATIONAL;
        }

        // Default: OPERATIONAL (com validação posterior de filtros mínimos)
        log.debug("Classificado como OPERATIONAL (default)");
        return SearchMode.OPERATIONAL;
    }

    /**
     * Verifica se há filtros operacionais padrão.
     */
    private boolean hasOperationalFilters(CursorPageRequest request) {
        return request.getCodCanal() != null
                || request.getCodSituacao() != null
                || request.getDtInicio() != null
                || request.getDtFim() != null;
    }

    /**
     * Conta quantos filtros complexos (analíticos) estão presentes.
     */
    private int countComplexFilters(CursorPageRequest request) {
        int count = 0;

        // Filtros de identificação complexos
        if (request.getNumLote() != null) {
            count++;
        }
        if (request.getCodProduto() != null) {
            count++;
        }
        if (request.getCodLogin() != null) {
            count++;
        }
        if (request.getCodFormaPagto() != null) {
            count++;
        }

        // Filtros de valor
        if (request.getVlTotalMin() != null || request.getVlTotalMax() != null) {
            count++;
        }

        // Filtros de datas adicionais (cada par conta como 1)
        if (request.getDtLiberacaoEfetivaInicio() != null || request.getDtLiberacaoEfetivaFim() != null) {
            count++;
        }
        if (request.getDtPagtoEconomicaInicio() != null || request.getDtPagtoEconomicaFim() != null) {
            count++;
        }
        if (request.getDtFinanceiraInicio() != null || request.getDtFinanceiraFim() != null) {
            count++;
        }
        if (request.getDtAlteracaoInicio() != null || request.getDtAlteracaoFim() != null) {
            count++;
        }

        return count;
    }

    /**
     * Valida se o request possui filtros mínimos para o modo classificado.
     *
     * @param request Request contendo os filtros
     * @param mode Modo de busca classificado
     * @throws IllegalArgumentException se os filtros mínimos não forem
     * atendidos
     */
    public void validateMinimumFilters(CursorPageRequest request, SearchMode mode) {
        switch (mode) {
            case SPECIFIC:
                // Já validado na classificação
                if (request.getNumSolicitacao() == null) {
                    throw new IllegalArgumentException("Busca específica requer numSolicitacao");
                }
                break;

            case OPERATIONAL:
                // Requer pelo menos um filtro de alta seletividade
                if (!hasOperationalFilters(request) && countComplexFilters(request) == 0) {
                    throw new IllegalArgumentException(
                            "Busca operacional requer pelo menos um filtro: "
                            + "codCanal, codSituacao, dtInicio/dtFim, ou outro filtro seletivo"
                    );
                }
                break;

            case ANALYTICAL:
                // Requer múltiplos filtros seletivos
                int filterCount = countComplexFilters(request);
                if (hasOperationalFilters(request)) {
                    filterCount++;
                }

                if (filterCount < 2) {
                    throw new IllegalArgumentException(
                            "Busca analítica requer pelo menos 2 filtros seletivos para evitar "
                            + "consultas muito amplas que comprometem performance"
                    );
                }
                break;
        }
    }
}
