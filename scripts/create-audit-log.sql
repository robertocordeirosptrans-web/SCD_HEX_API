-- ============================================================
-- Tabela: SPTRANSDBA.AUDIT_LOG
-- Objetivo: registrar todos os eventos auditáveis da API SCD.
--
-- Índices estratégicos para as consultas mais comuns:
--   - por usuário executor   (USER_ID)
--   - por usuário alvo       (TARGET_USER_ID)
--   - por sessão             (SESSION_ID)
--   - por tipo de evento     (ACTION)
--   - por data               (CREATED_AT)
-- ============================================================

-- Sequência para geração do ID primário
CREATE SEQUENCE SPTRANSDBA.SCD_AUDIT_LOG_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Tabela principal
CREATE TABLE SPTRANSDBA.AUDIT_API (
    ID              NUMBER          DEFAULT SPTRANSDBA.SCD_AUDIT_LOG_SEQ.NEXTVAL
                                    NOT NULL,
    USER_ID         NUMBER          NULL,           -- quem executou (null = sistema/anônimo)
    TARGET_USER_ID  NUMBER          NULL,           -- usuário afetado pela ação
    SESSION_ID      VARCHAR2(100)   NULL,           -- sessão corrente
    ACTION          VARCHAR2(50)    NOT NULL,       -- AuditEventType (LOGIN_SUCCESS, etc.)
    EVENT_DETAILS   VARCHAR2(4000)  NULL,           -- JSON com detalhes adicionais
    IP_ADDRESS      VARCHAR2(50)    NULL,           -- IP de origem
    USER_AGENT      VARCHAR2(500)   NULL,           -- User-Agent do cliente
    CREATED_AT      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT PK_AUDIT_LOG PRIMARY KEY (ID)
);

-- Índices (removido o DESC que causava o erro)
CREATE INDEX IDX_AUDIT_LOG_USER_ID
    ON SPTRANSDBA.AUDIT_API (USER_ID);

CREATE INDEX IDX_AUDIT_LOG_TARGET_USER_ID
    ON SPTRANSDBA.AUDIT_API (TARGET_USER_ID);

CREATE INDEX IDX_AUDIT_LOG_SESSION_ID
    ON SPTRANSDBA.AUDIT_API (SESSION_ID);

CREATE INDEX IDX_AUDIT_LOG_ACTION
    ON SPTRANSDBA.AUDIT_API (ACTION);

CREATE INDEX IDX_AUDIT_LOG_CREATED_AT
    ON SPTRANSDBA.AUDIT_API (CREATED_AT);  -- DESC removido

-- Comentários
COMMENT ON TABLE  SPTRANSDBA.AUDIT_API IS 'Registro de eventos auditáveis da API SCD';
COMMENT ON COLUMN SPTRANSDBA.AUDIT_API.ID             IS 'PK gerada pela sequência SCD_AUDIT_LOG_SEQ';
COMMENT ON COLUMN SPTRANSDBA.AUDIT_API.USER_ID        IS 'ID do usuário executor (null = sistema)';
COMMENT ON COLUMN SPTRANSDBA.AUDIT_API.TARGET_USER_ID IS 'ID do usuário afetado pela ação';
COMMENT ON COLUMN SPTRANSDBA.AUDIT_API.SESSION_ID     IS 'ID da sessão no momento do evento';
COMMENT ON COLUMN SPTRANSDBA.AUDIT_API.ACTION         IS 'Tipo do evento (enum AuditEventType)';
COMMENT ON COLUMN SPTRANSDBA.AUDIT_API.EVENT_DETAILS  IS 'Detalhes adicionais em JSON — sem dados sensíveis';
COMMENT ON COLUMN SPTRANSDBA.AUDIT_API.IP_ADDRESS     IS 'IP de origem da requisição';
COMMENT ON COLUMN SPTRANSDBA.AUDIT_API.USER_AGENT     IS 'User-Agent do cliente HTTP';
COMMENT ON COLUMN SPTRANSDBA.AUDIT_API.CREATED_AT     IS 'Data/hora do evento';