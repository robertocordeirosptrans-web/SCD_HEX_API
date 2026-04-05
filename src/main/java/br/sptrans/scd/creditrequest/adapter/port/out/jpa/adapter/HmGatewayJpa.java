package br.sptrans.scd.creditrequest.adapter.port.out.jpa.adapter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.creditrequest.application.port.out.HmGateway;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Implementação do {@link HmGateway} que acessa as tabelas
 * {@code LNK_HM_TB_AUTORIZACAORECARGA} e {@code LNK_HM_TB_REDE} via DBLink Oracle.
 *
 * <p>Segue o mesmo padrão de acesso via {@link EntityManager} + native query
 * utilizado em {@link LiminarGatewayJpa}.</p>
 */
@Repository
public class HmGatewayJpa implements HmGateway {

    private static final Logger log = LoggerFactory.getLogger(HmGatewayJpa.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public boolean itemConfirmadoPeloHm(Long numSolicitacao, String codCanal, String numLogicoCartao) {
        String sql = """
                SELECT COUNT(1) FROM LNK_HM_TB_AUTORIZACAORECARGA@dblink_hm
                 WHERE NU_SOLICITACAO  = :numSolicitacao
                   AND CD_CANAL        = :codCanal
                   AND NU_LOGICOCARTAO = :numLogicoCartao
                   AND NI_STATUSSCD    IN (3, 15)
                """;

        Number count = (Number) em.createNativeQuery(sql)
                .setParameter("numSolicitacao", numSolicitacao)
                .setParameter("codCanal", codCanal)
                .setParameter("numLogicoCartao", numLogicoCartao)
                .getSingleResult();

        boolean confirmado = count.longValue() > 0;
        log.debug("itemConfirmadoPeloHm - numSolicitacao={}, codCanal={}, numLogicoCartao={}, confirmado={}",
                numSolicitacao, codCanal, numLogicoCartao, confirmado);
        return confirmado;
    }

    @Override
    @Transactional
    public void registrarAutorizacaoRecarga(Long numSolicitacao, String codCanal, String codCanalDistribuicao) {
        String sqlCheck = """
                SELECT COUNT(1) FROM LNK_HM_TB_REDE@dblink_hm
                 WHERE NU_SOLICITACAO       = :numSolicitacao
                   AND CD_CANAL             = :codCanal
                   AND CD_CANALDISTRIBUICAO = :codCanalDistribuicao
                """;

        Number count = (Number) em.createNativeQuery(sqlCheck)
                .setParameter("numSolicitacao", numSolicitacao)
                .setParameter("codCanal", codCanal)
                .setParameter("codCanalDistribuicao", codCanalDistribuicao)
                .getSingleResult();

        if (count.longValue() > 0) {
            log.debug("Autorização de recarga já registrada - numSolicitacao={}, codCanal={}",
                    numSolicitacao, codCanal);
            return;
        }

        String sqlInsert = """
                INSERT INTO LNK_HM_TB_REDE@dblink_hm
                       (NU_SOLICITACAO, CD_CANAL, CD_CANALDISTRIBUICAO, DT_CADASTRO)
                VALUES (:numSolicitacao, :codCanal, :codCanalDistribuicao, :dtCadastro)
                """;

        em.createNativeQuery(sqlInsert)
                .setParameter("numSolicitacao", numSolicitacao)
                .setParameter("codCanal", codCanal)
                .setParameter("codCanalDistribuicao", codCanalDistribuicao)
                .setParameter("dtCadastro", LocalDateTime.now())
                .executeUpdate();

        log.info("Autorização de recarga registrada em LNK_HM_TB_REDE - numSolicitacao={}, codCanal={}",
                numSolicitacao, codCanal);
    }

    @Override
    @Transactional
    public void enviarAutorizacaoRecarga(Long numSolicitacao, Long numSolicitacaoItem, String codCanal,
            String numLogicoCartao, String codAssinaturaHsm,
            LocalDateTime dtPagtoEconomica, Integer seqRecarga,
            BigDecimal valor, boolean liminar) {

        String sql = """
                INSERT INTO LNK_HM_TB_AUTORIZACAORECARGA@dblink_hm
                       (NU_SOLICITACAO, NU_SOLICITACAOITEM, CD_CANAL,
                        NU_LOGICOCARTAO, CD_ASSINATURAHSM, DT_AUTORIZACAO,
                        ID_AUTORIZACAO, VL_EVENTOFINANCEIRO, IN_LIMINAR,
                        NI_STATUSHM, NI_STATUSSCD, DT_CADASTRO)
                VALUES (:numSolicitacao, :numSolicitacaoItem, :codCanal,
                        :numLogicoCartao, :codAssinaturaHsm, :dtAutorizacao,
                        :seqRecarga, :valor, :liminar,
                        0, 6, :dtCadastro)
                """;

        LocalDateTime dtAutorizacao = dtPagtoEconomica != null ? dtPagtoEconomica : LocalDateTime.now();

        em.createNativeQuery(sql)
                .setParameter("numSolicitacao", numSolicitacao)
                .setParameter("numSolicitacaoItem", numSolicitacaoItem)
                .setParameter("codCanal", codCanal)
                .setParameter("numLogicoCartao", numLogicoCartao)
                .setParameter("codAssinaturaHsm", codAssinaturaHsm)
                .setParameter("dtAutorizacao", dtAutorizacao)
                .setParameter("seqRecarga", seqRecarga)
                .setParameter("valor", valor)
                .setParameter("liminar", liminar ? 1 : 0)
                .setParameter("dtCadastro", LocalDateTime.now())
                .executeUpdate();

        log.info("Autorização de recarga enviada ao HM - numSolicitacao={}, numItem={}, codCanal={}, "
                + "numLogicoCartao={}, valor={}, liminar={}",
                numSolicitacao, numSolicitacaoItem, codCanal, numLogicoCartao, valor, liminar);
    }
}
