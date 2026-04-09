package br.sptrans.scd.creditrequest.adapter.out.jpa.adapter;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.creditrequest.application.port.out.repository.EventoFinanceiroPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Implementação de {@link EventoFinanceiroPort} que invoca a função Oracle
 * {@code PCK_MVE_EVENTO_FINANCEIRO.ProcessarLancamento} via native query.
 *
 * <p>Em caso de falha na chamada, retorna {@link BigDecimal#ZERO} replicando o
 * comportamento original de {@code EXCEPTION WHEN OTHERS THEN NULL}.</p>
 */
@Repository
public class EventoFinanceiroGatewayJpa implements EventoFinanceiroPort {

    private static final Logger log = LoggerFactory.getLogger(EventoFinanceiroGatewayJpa.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public BigDecimal processarLancamento(Long numSolicitacao, String codCanal, Long numSolicitacaoItem) {
        try {
            @SuppressWarnings("unchecked")
            List<Object> result = em.createNativeQuery(
                    "SELECT PCK_MVE_EVENTO_FINANCEIRO.ProcessarLancamento(:numSolicitacao, :codCanal, :numSolicitacaoItem) FROM DUAL")
                    .setParameter("numSolicitacao", numSolicitacao)
                    .setParameter("codCanal", codCanal)
                    .setParameter("numSolicitacaoItem", numSolicitacaoItem)
                    .getResultList();

            if (result != null && !result.isEmpty() && result.get(0) != null) {
                return new BigDecimal(result.get(0).toString());
            }
            return BigDecimal.ZERO;

        } catch (Exception e) {
            log.warn("Falha ao processar evento financeiro para solicitação={}, canal={}, item={}. Usando valor 0.",
                    numSolicitacao, codCanal, numSolicitacaoItem, e);
            return BigDecimal.ZERO;
        }
    }
}
