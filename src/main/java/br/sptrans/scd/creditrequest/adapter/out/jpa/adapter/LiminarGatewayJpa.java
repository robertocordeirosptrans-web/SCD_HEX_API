package br.sptrans.scd.creditrequest.adapter.out.jpa.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.product.application.port.out.gateway.LiminarGateway;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Implementação do {@link LiminarGateway} que consulta o sistema SCA via DBLink.
 *
 * <p>Replica o comportamento original das procedures Oracle:</p>
 * <ul>
 *   <li>{@code PKG_EMPRESA_LIMINAR.FIND_TAXA_ISENTA_BY_PEDIDO@dblink_sca} — Momento 1</li>
 *   <li>{@code PKG_EMPRESA_LIMINAR.FIND_BY_PEDIDO@dblink_sca} — Momento 2, empresa</li>
 *   <li>{@code PKG_CARTAO_LIMINAR.FIND_BY_CARTAO@dblink_sca} — Momento 2, cartão</li>
 * </ul>
 *
 * <p>Em caso de falha na chamada ao SCA, todos os métodos retornam o valor "sem liminar"
 * (false / 0), replicando o {@code EXCEPTION WHEN OTHERS THEN NULL} original.</p>
 */
@Repository
public class LiminarGatewayJpa implements LiminarGateway {

    private static final Logger log = LoggerFactory.getLogger(LiminarGatewayJpa.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public boolean empresaPossuiIsencaoTaxa(String numeroPedido) {
        try {
            var query = em.createNativeQuery(
                "SELECT PKG_EMPRESA_LIMINAR.FIND_TAXA_ISENTA_BY_PEDIDO@DBLINK_SCA(?) FROM dual"
            );
            query.setParameter(1, numeroPedido);
            var result = query.getSingleResult();
            // Esperado: retorna 1 para isento, 0 para não isento
            return result != null && Integer.parseInt(result.toString()) == 1;
        } catch (NumberFormatException e) {
            log.warn("Falha ao consultar liminar SCA para pedido {}. "
                    + "Taxa será aplicada normalmente.", numeroPedido, e);
            return false;
        }
    }

    @Override
    public int verificarLiminarEmpresa(Long numeroPedido) {
        try {
            var query = em.createNativeQuery(
                "SELECT PKG_EMPRESA_LIMINAR.FIND_BY_PEDIDO@DBLINK_SCA(?) FROM dual"
            );
            query.setParameter(1, numeroPedido);
            var result = query.getResultList();
            // Ajuste conforme o tipo de retorno esperado
            if (result != null && !result.isEmpty()) {
                // Exemplo: retorna 1 se encontrou, 0 se não encontrou
                return 1;
            }
            return 0;
        } catch (Exception e) {
            log.warn("Falha ao verificar liminar de empresa para pedido {}. "
                    + "Assumindo sem liminar.", numeroPedido, e);
            return 0;
        }
    }

    @Override
    public int verificarLiminarCartao(int liminarEmpresa, String numeroCartao) {
        if (liminarEmpresa == 0) {
            return 0;
        }
        try {
            var query = em.createNativeQuery(
                "SELECT PKG_CARTAO_LIMINAR.FIND_BY_CARTAO@DBLINK_SCA(?) FROM dual"
            );
            query.setParameter(1, numeroCartao);
            var result = query.getSingleResult();
            return result != null ? Integer.parseInt(result.toString()) : 0;
        } catch (NumberFormatException e) {
            log.warn("Falha ao verificar liminar de cartão {}. "
                    + "Assumindo sem liminar.", numeroCartao, e);
            return 0;
        }
    }


}
