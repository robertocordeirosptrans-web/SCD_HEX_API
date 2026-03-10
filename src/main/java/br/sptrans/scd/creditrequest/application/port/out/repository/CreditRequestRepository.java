package br.sptrans.scd.creditrequest.application.port.out.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestEJpa;
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestEJpaKey;

public interface CreditRequestRepository extends JpaRepository<CreditRequestEJpa, CreditRequestEJpaKey>,
        JpaSpecificationExecutor<CreditRequestEJpa> {

    /**
     * Busca por número de solicitação e código de canal.
     */
    Optional< CreditRequestEJpa> findByNumSolicitacaoAndCodCanal(
            Long numSolicitacao,
            String codCanal);

    /**
     * Lista por código de canal e situação.
     */
    List<CreditRequestEJpa> findByCanalAndSituacao(
            String codCanal,
            String codSituacao);

    /**
     * Verifica se já existe um pedido com o numSolicitacao informado em
     * qualquer canal.
     */
    boolean existsByNumSolicitacao(Long numSolicitacao);

    /**
     * Verifica se já existe um lote com o numLote informado para um dado canal.
     */
    boolean existsByNumLoteAndCodCanal(@Param("numLote") String numLote, String codCanal);

    /**
     * Busca solicitações elegíveis para liberação de recarga.
     */
    CreditRequestEJpa findElegiveisParaLiberacao(
            String codSituacao,
            LocalDateTime dtInicio,
            LocalDateTime dtFim);

    /**
     * Busca solicitações elegíveis para processamento de recarga.
     */
    CreditRequestEJpa findElegiveisParaProcessamento(String codSituacao);

    /**
     * Busca solicitações elegíveis para confirmação de retorno do HM.
     */
    CreditRequestEJpa findElegiveisParaConfirmacao(String codSituacao);
}
