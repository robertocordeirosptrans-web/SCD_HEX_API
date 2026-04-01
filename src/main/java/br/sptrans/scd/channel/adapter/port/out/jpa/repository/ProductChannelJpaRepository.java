package br.sptrans.scd.channel.adapter.port.out.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.channel.adapter.port.out.jpa.projection.ProductChannelProjection;
import br.sptrans.scd.channel.adapter.port.out.persistence.entity.ProductChannelEntityJpa;
import br.sptrans.scd.channel.adapter.port.out.persistence.entity.ProductChannelKeyEntityJpa;

public interface ProductChannelJpaRepository extends JpaRepository<ProductChannelEntityJpa, ProductChannelKeyEntityJpa>, JpaSpecificationExecutor<ProductChannelEntityJpa> {

    @Modifying
    @Transactional
    @Query("""
        UPDATE ProductChannelEntityJpa p SET
            p.qtdLimiteComercializacao = :qtdLimiteComercializacao,
            p.qtdMinimaEstoque = :qtdMinimaEstoque,
            p.qtdMaximaEstoque = :qtdMaximaEstoque,
            p.qtdMinimaRessuprimento = :qtdMinimaRessuprimento,
            p.qtdMaximaRessuprimento = :qtdMaximaRessuprimento,
            p.codOrgaoEmissor = :codOrgaoEmissor,
            p.vlFace = :vlFace,
            p.codStatus = :stCanaisProdutos,
            p.dtManutencao = CURRENT_TIMESTAMP,
            p.codConvenio = :codConvenio,
            p.codTipoOperHM = :tipoOperHm,
            p.flgCarac = :flgCarac,
            p.idUsuarioManutencao = :idUsuarioManutencao
        WHERE p.id.codCanal = :codCanal AND p.id.codProduto = :codProduto
    """)
    int updateProductChannel(
        @Param("qtdLimiteComercializacao") Integer qtdLimiteComercializacao,
        @Param("qtdMinimaEstoque") Integer qtdMinimaEstoque,
        @Param("qtdMaximaEstoque") Integer qtdMaximaEstoque,
        @Param("qtdMinimaRessuprimento") Integer qtdMinimaRessuprimento,
        @Param("qtdMaximaRessuprimento") Integer qtdMaximaRessuprimento,
        @Param("codOrgaoEmissor") String codOrgaoEmissor,
        @Param("vlFace") java.math.BigDecimal vlFace,
        @Param("stCanaisProdutos") String stCanaisProdutos,
        @Param("codConvenio") String codConvenio,
        @Param("tipoOperHm") String tipoOperHm,
        @Param("flgCarac") String flgCarac,
        @Param("idUsuarioManutencao") Long idUsuarioManutencao,
        @Param("codCanal") String codCanal,
        @Param("codProduto") String codProduto
    );

    @Query("SELECT DISTINCT cp FROM ProductChannelEntityJpa cp WHERE cp.id.codCanal = :codCanal AND cp.id.codProduto = :codProduto")
    Optional<ProductChannelEntityJpa> findByIdOptimized(@Param("codCanal") String codCanal, @Param("codProduto") String codProduto);

    @Query(value = """
  SELECT DISTINCT 
        -- Informações básicas do canal/produto
        cp.COD_CANAL as codCanal,
        cp.COD_PRODUTO as codProduto,
        p.DES_PRODUTO as desProduto,
        cp.ST_CANAIS_PRODUTOS as statusCanalProduto,
        
        -- Características do produto no canal
        cp.QTD_LIMITE_COMERCIALIZACAO as qtdLimiteComercializacao,
        cp.QTD_MINIMA_ESTOQUE as qtdMinimaEstoque,
        cp.QTD_MAXIMA_ESTOQUE as qtdMaximaEstoque,
        cp.QTD_MINIMA_RESSUPRIMENTO as qtdMinimaRessuprimento,
        cp.QTD_MAXIMA_RESSUPRIMENTO as qtdMaximaRessuprimento,
        cp.VL_FACE as vlFace,
        cp.COD_CONVENIO as codConvenio,
        cp.TIPO_OPER_HM as tipoOperHm,
        cp.FLG_CARAC as flgCarac,
        cp.COD_ORGAO_EMISSOR as codOrgaoEmissor,
        
        -- Vigência do produto
        cv.DT_INICIO_VALIDADE as inicioValidade,
        cv.DT_FIM_VALIDADE as fimValidade,
        cv.COD_STATUS as statusVigencia,
        
        -- Taxa principal
        t.ID_TAXA as idTaxa,
        t.DSC_TAXA as descricaoTaxa,
        t.DT_INICIAL as taxaInicio,
        t.DT_FINAL as taxaFim,
        
        -- Taxa administrativa
        ta.REC_INICIAL as taxaAdmRecInicial,
        ta.REC_FINAL as taxaAdmRecFinal,
        ta.VAL_FIXO as taxaAdmValFixo,
        ta.VAL_PERCENTUAL as taxaAdmPercentual,
        
        -- Taxa serviço
        ts.REC_INICIAL as taxaServRecInicial,
        ts.REC_FINAL as taxaServRecFinal,
        ts.VAL_FIXO as taxaServValFixo,
        ts.VAL_PERCENTUAL as taxaServPercentual,
        ts.VAL_MINIMO as taxaServValMinimo,
        
        -- Taxa por canal
        tc.VL_T_INICIO as taxaCanalVlInicio,
        tc.VL_T_FINAL as taxaCanalVlFinal,
        tc.VL_PERCENTUAL as taxaCanalPercentual,
        tc.DT_INICIO as taxaCanalInicio,
        tc.DT_FIM as taxaCanalFim,
        
        -- Canais destino (distribuição)
        td.COD_CANALDESTINO as canaisDestino,
        
        -- Limites de recarga
        lr.DT_INICIO_VALIDADE as limiteInicioValidade,
        lr.DT_FIM_VALIDADE as limiteFimValidade,
        lr.VL_MINIMO_RECARGA as vlMinimoRecarga,
        lr.VL_MAXIMO_RECARGA as vlMaximoRecarga,
        lr.VL_MAXIMO_SALDO as vlMaximoSaldo,
        lr.COD_STATUS as statusLimite
        
    FROM SPTRANSDBA.CANAIS_PRODUTOS cp
    
    -- Informações do produto
    INNER JOIN SPTRANSDBA.PRODUTOS p ON cp.COD_PRODUTO = p.COD_PRODUTO
    
    -- Vigência (se existir)
    LEFT JOIN SPTRANSDBA.CONVENIOS_VIGENCIAS cv ON cp.COD_CANAL = cv.COD_CANAL 
        AND cp.COD_PRODUTO = cv.COD_PRODUTO
    
    -- Taxas (todas as relacionadas)
    LEFT JOIN SPTRANSDBA.TAXAS t ON cp.COD_CANAL = t.COD_CANAL 
        AND cp.COD_PRODUTO = t.COD_PRODUTO
    
    -- Detalhes das taxas
    LEFT JOIN SPTRANSDBA.TAXAS_ADMINISTRATIVA ta ON t.ID_TAXA = ta.ID_TAXA
    LEFT JOIN SPTRANSDBA.TAXAS_SERVICO ts ON t.ID_TAXA = ts.ID_TAXA
    LEFT JOIN SPTRANSDBA.TAXA_SCANAL tc ON cp.COD_CANAL = tc.COD_CANAL
    
    -- Canais destino (distribuição)
    LEFT JOIN SPTRANSDBA.TAXAS_DESTINO td ON t.ID_TAXA = td.ID_TAXA
    
    -- Limites (se existirem)
    LEFT JOIN SPTRANSDBA.LIMITES_RECARGA lr ON cp.COD_CANAL = lr.COD_CANAL 
        AND cp.COD_PRODUTO = lr.COD_PRODUTO
    
    WHERE cp.COD_CANAL = :codCanal
        AND cp.ST_CANAIS_PRODUTOS = 'A'
    
    ORDER BY 
        cp.COD_PRODUTO,
        t.ID_TAXA,
        td.COD_CANALDESTINO
        """, nativeQuery = true)
    List<ProductChannelProjection> findCompletoByCanal(@Param("codCanal") String codCanal);

    @Query(value = """
       SELECT DISTINCT 
        -- Informações do CANAL DE DISTRIBUIÇÃO (receptor)
        :codCanalDistribuicao as codCanalDistribuicao,
        
        -- Informações do CANAL DE COMERCIALIZAÇÃO (origem)
        t.COD_CANAL as codCanalComercializacao,
        
        -- Informações do Produto
        t.COD_PRODUTO as codProduto,
        p.DES_PRODUTO as desProduto,
        
        -- Informações da TAXA (recebida do comercializador)
        t.ID_TAXA as idTaxa,
        t.DSC_TAXA as descricaoTaxa,
        t.DT_INICIAL as taxaInicio,
        t.DT_FINAL as taxaFim,
        
        -- Situação da taxa PARA ESTE CANAL DE DISTRIBUIÇÃO
        CASE 
            WHEN t.DT_INICIAL > SYSDATE THEN 'FUTURA'
            WHEN t.DT_INICIAL <= SYSDATE 
                AND (t.DT_FINAL IS NULL OR t.DT_FINAL >= SYSDATE) 
                THEN 'VIGENTE'
            ELSE 'EXPIRADA'
        END as situacaoTaxa,
        
        -- Taxa administrativa (recebida)
        ta.VAL_PERCENTUAL as taxaAdmPercentual,
        ta.VAL_FIXO as taxaAdmValFixo,
        ta.REC_INICIAL as taxaAdmRecInicial,
        ta.REC_FINAL as taxaAdmRecFinal,
        
        -- Taxa serviço (recebida)
        ts.VAL_PERCENTUAL as taxaServPercentual,
        ts.VAL_FIXO as taxaServValFixo,
        ts.REC_INICIAL as taxaServRecInicial,
        ts.REC_FINAL as taxaServRecFinal,
        ts.VAL_MINIMO as taxaServValMinimo,
        
        -- Informações do produto no canal comercializador (origem)
        cp.ST_CANAIS_PRODUTOS as statusProdutoOrigem,
        cp.VL_FACE as vlFace,
        
        -- Vigência do produto na origem
        cv.DT_INICIO_VALIDADE as inicioValidadeOrigem,
        cv.DT_FIM_VALIDADE as fimValidadeOrigem
        
    -- A PERSPECTIVA MUDA: Começa pela TAXAS_DESTINO
    FROM SPTRANSDBA.TAXAS_DESTINO td
    
    -- Junta com a TAXA que foi distribuída
    INNER JOIN SPTRANSDBA.TAXAS t ON td.ID_TAXA = t.ID_TAXA
    
    -- Informações do produto
    INNER JOIN SPTRANSDBA.PRODUTOS p ON t.COD_PRODUTO = p.COD_PRODUTO
    
    -- Informações do canal comercializador (ORIGEM)
    INNER JOIN SPTRANSDBA.CANAIS_PRODUTOS cp ON t.COD_CANAL = cp.COD_CANAL 
        AND t.COD_PRODUTO = cp.COD_PRODUTO
        AND cp.ST_CANAIS_PRODUTOS = 'A'
    
    -- Detalhes das taxas
    LEFT JOIN SPTRANSDBA.TAXAS_ADMINISTRATIVA ta ON t.ID_TAXA = ta.ID_TAXA
    LEFT JOIN SPTRANSDBA.TAXAS_SERVICO ts ON t.ID_TAXA = ts.ID_TAXA
    
    -- Vigência do produto na origem
    LEFT JOIN SPTRANSDBA.CONVENIOS_VIGENCIAS cv ON t.COD_CANAL = cv.COD_CANAL 
        AND td.COD_CANALDESTINO = cv.COD_PRODUTO
        AND cv.COD_STATUS = 'A'
    
    -- FILTRO CRÍTICO: Apenas taxas distribuídas PARA ESTE CANAL
    WHERE td.COD_CANALDESTINO = :codCanalDistribuicao
        AND p.ST_PRODUTOS = 'A'
    
    ORDER BY 
        t.COD_CANAL,  -- Agrupa por canal de origem
        t.COD_PRODUTO,
        t.DT_INICIAL DESC 
        """, nativeQuery = true)
    List<ProductChannelProjection> findCompletoByCanalDistrib(@Param("codCanalDistribuicao") Integer codCanalDistribuicao);
}
