-- ============================================================
-- Tabela: SPTRANSDBA.IDEMPOTENCY_LOG
-- Objetivo: registrar chaves de idempotência para garantir
--           que requisições duplicadas não sejam reprocessadas.
--
-- Estratégia de concorrência:
--   - INSERT tentativo com controle por PRIMARY KEY (IDEMPOTENCY_KEY)
--   - Violação de PK indica requisição duplicada → avaliação do status
--
-- Estados (STATUS):
--   PROCESSING — requisição em andamento
--   SUCCESS    — processamento concluído com sucesso; resposta armazenada
--   FAILED     — processamento falhou; permite retry com a mesma chave
--
-- TTL: registros com CREATED_AT mais antigos que o período configurado
--      são removidos pelo job IdempotencyCleanupJob.
-- ============================================================

CREATE TABLE SPTRANSDBA.IDEMPOTENCY_LOG (
    IDEMPOTENCY_KEY   VARCHAR2(255)   NOT NULL,
    REQUEST_HASH      VARCHAR2(64)    NULL,      -- SHA-256 hex do payload
    STATUS            VARCHAR2(20)    NOT NULL,  -- PROCESSING | SUCCESS | FAILED
    RESPONSE_BODY     CLOB            NULL,      -- JSON serializado da resposta
    HTTP_STATUS       NUMBER(3)       NULL,      -- código HTTP da resposta
    CREATED_AT        TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UPDATED_AT        TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT PK_IDEMPOTENCY_LOG PRIMARY KEY (IDEMPOTENCY_KEY),
    CONSTRAINT CK_IDEMPOTENCY_STATUS CHECK (STATUS IN ('PROCESSING', 'SUCCESS', 'FAILED'))
);

-- Índice para limpeza por TTL (varredura por data de criação)
CREATE INDEX IDX_IDEMPOTENCY_CREATED_AT
    ON SPTRANSDBA.IDEMPOTENCY_LOG (CREATED_AT);

-- Índice para localização de registros travados em PROCESSING
CREATE INDEX IDX_IDEMPOTENCY_STATUS
    ON SPTRANSDBA.IDEMPOTENCY_LOG (STATUS, UPDATED_AT);

-- Comentários
COMMENT ON TABLE  SPTRANSDBA.IDEMPOTENCY_LOG IS 'Registro de chaves de idempotência da API SCD';
COMMENT ON COLUMN SPTRANSDBA.IDEMPOTENCY_LOG.IDEMPOTENCY_KEY IS 'Chave enviada pelo cliente via header Idempotency-Key (PK)';
COMMENT ON COLUMN SPTRANSDBA.IDEMPOTENCY_LOG.REQUEST_HASH    IS 'SHA-256 hex do payload da requisição — detecta payload divergente';
COMMENT ON COLUMN SPTRANSDBA.IDEMPOTENCY_LOG.STATUS          IS 'Estado atual: PROCESSING | SUCCESS | FAILED';
COMMENT ON COLUMN SPTRANSDBA.IDEMPOTENCY_LOG.RESPONSE_BODY   IS 'JSON serializado da resposta (preenchido apenas em SUCCESS)';
COMMENT ON COLUMN SPTRANSDBA.IDEMPOTENCY_LOG.HTTP_STATUS     IS 'Código HTTP da resposta original';
COMMENT ON COLUMN SPTRANSDBA.IDEMPOTENCY_LOG.CREATED_AT      IS 'Data/hora de criação do registro';
COMMENT ON COLUMN SPTRANSDBA.IDEMPOTENCY_LOG.UPDATED_AT      IS 'Data/hora da última atualização de status';
