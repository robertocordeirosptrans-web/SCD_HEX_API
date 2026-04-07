package br.sptrans.scd.creditrequest.adapter.out.jpa.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.product.application.port.out.gateway.LiminarGateway;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

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
            StoredProcedureQuery query = em
                    .createStoredProcedureQuery(
                            "PKG_EMPRESA_LIMINAR.FIND_TAXA_ISENTA_BY_PEDIDO@DBLINK_SCA")
                    .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(2, Integer.class, ParameterMode.OUT)
                    .setParameter(1, numeroPedido);

            query.execute();
            Integer resultado = (Integer) query.getOutputParameterValue(2);
            return resultado != null && resultado == 1;

        } catch (Exception e) {
            log.warn("Falha ao consultar liminar SCA para pedido {}. "
                    + "Taxa será aplicada normalmente.", numeroPedido, e);
            return false;
        }
    }

    @Override
    public int verificarLiminarEmpresa(String numeroPedido) {
        try {
            StoredProcedureQuery query = em
                    .createStoredProcedureQuery(
                            "PKG_EMPRESA_LIMINAR.FIND_BY_PEDIDO@DBLINK_SCA")
                    .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(2, Integer.class, ParameterMode.OUT)
                    .setParameter(1, numeroPedido);

            query.execute();
            Integer resultado = (Integer) query.getOutputParameterValue(2);
            return resultado != null ? resultado : 0;

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
            StoredProcedureQuery query = em
                    .createStoredProcedureQuery(
                            "PKG_CARTAO_LIMINAR.FIND_BY_CARTAO@DBLINK_SCA")
                    .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(2, Integer.class, ParameterMode.OUT)
                    .setParameter(1, numeroCartao);

            query.execute();
            Integer resultado = (Integer) query.getOutputParameterValue(2);
            return resultado != null ? resultado : 0;

        } catch (Exception e) {
            log.warn("Falha ao verificar liminar de cartão {}. "
                    + "Assumindo sem liminar.", numeroCartao, e);
            return 0;
        }
    }

    @Override
    public boolean existeLiminar(String codCanal, String numLogicoCartao) {
        try {
            int liminarEmpresa = verificarLiminarEmpresa(codCanal);
            return verificarLiminarCartao(liminarEmpresa, numLogicoCartao) == 1;
        } catch (Exception e) {
            log.warn("Falha ao consultar liminar combinada para canal={}, cartao={}. "
                    + "Assumindo sem liminar.", codCanal, numLogicoCartao, e);
            return false;
        }
    }
}
