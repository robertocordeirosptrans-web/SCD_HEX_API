package br.sptrans.scd.creditrequest.adapter.port.out.jpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.creditrequest.application.port.out.repository.HistCreditRequestRepository;
import br.sptrans.scd.creditrequest.domain.HistCreditRequest;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestKey;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HistCreditRequestAdapterJpa implements HistCreditRequestRepository {

    private final JdbcTemplate jdbc;

    private static final String SQL_SELECT_BASE = """
            SELECT NUM_SOLICITACAO, COD_CANAL, SEQ_HIST_SDS,
                   COD_TIPO_DOCUMENTO, COD_SITUACAO, DT_TRANSICAO, ID_ORIGEM_TRANSICAO,
                   DT_CADASTRO, DT_MANUTENCAO, DT_PGTO_ECONOMICA, DT_LIBERACAO_EFETIVA,
                   DT_FINANCEIRA, SQ_PID, DT_INIC_PROCESSO, DT_FIM_PROCESSO,
                   ID_USUARIO_TRANSICAO
            FROM SPTRANSDBA.HIS_SOL_SITUACOES
            """;

    private static final String SQL_INSERT = """
            INSERT INTO SPTRANSDBA.HIS_SOL_SITUACOES
                (NUM_SOLICITACAO, COD_CANAL, SEQ_HIST_SDS,
                 COD_TIPO_DOCUMENTO, COD_SITUACAO, DT_TRANSICAO, ID_ORIGEM_TRANSICAO,
                 DT_CADASTRO, DT_MANUTENCAO, DT_PGTO_ECONOMICA, DT_LIBERACAO_EFETIVA,
                 DT_FINANCEIRA, SQ_PID, DT_INIC_PROCESSO, DT_FIM_PROCESSO,
                 ID_USUARIO_TRANSICAO)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    @Override
    public HistCreditRequest save(HistCreditRequest h) {
        jdbc.update(SQL_INSERT,
                h.getId().getNumSolicitacao(),
                h.getId().getCodCanal(),
                h.getId().getSeqHistSdis(),
                h.getCodTipoDocumento(),
                h.getCodSituacao(),
                h.getDtTransicao(),
                h.getIdOrigemTransicao(),
                h.getDtCadastro(),
                h.getDtManutencao(),
                h.getDtPgtoEconomica(),
                null, // DT_LIBERACAO_EFETIVA — preenchido se disponível no domínio
                null, // DT_FINANCEIRA — preenchido se disponível no domínio
                h.getSqPID(),
                h.getDtInicProcesso(),
                h.getDtFimProcesso(),
                h.getIdUsuarioTransicao() != null ? h.getIdUsuarioTransicao().getIdUsuario() : null);
        return h;
    }

    @Override
    public List<HistCreditRequest> saveAll(List<HistCreditRequest> items) {
        items.forEach(this::save);
        return items;
    }

    @Override
    public Optional<HistCreditRequest> findById(HistCreditRequestKey id) {
        String sql = SQL_SELECT_BASE + "WHERE NUM_SOLICITACAO = ? AND COD_CANAL = ? AND SEQ_HIST_SDS = ?";
        List<HistCreditRequest> result = jdbc.query(sql, this::mapRow,
                id.getNumSolicitacao(), id.getCodCanal(), id.getSeqHistSdis());
        return result.stream().findFirst();
    }

    @Override
    public List<HistCreditRequest> findByNumSolicitacaoAndCodCanal(Long numSolicitacao, String codCanal) {
        String sql = SQL_SELECT_BASE + "WHERE NUM_SOLICITACAO = ? AND COD_CANAL = ? ORDER BY SEQ_HIST_SDS DESC";
        return jdbc.query(sql, this::mapRow, numSolicitacao, codCanal);
    }

    @Override
    public List<HistCreditRequest> findAll() {
        return jdbc.query(SQL_SELECT_BASE + "ORDER BY NUM_SOLICITACAO, SEQ_HIST_SDS DESC", this::mapRow);
    }

    @Override
    public boolean existsById(HistCreditRequestKey id) {
        String sql = """
                SELECT COUNT(1) FROM SPTRANSDBA.HIS_SOL_SITUACOES
                WHERE NUM_SOLICITACAO = ? AND COD_CANAL = ? AND SEQ_HIST_SDS = ?
                """;
        Integer count = jdbc.queryForObject(sql, Integer.class,
                id.getNumSolicitacao(), id.getCodCanal(), id.getSeqHistSdis());
        return count != null && count > 0;
    }

    @Override
    public long count() {
        Long total = jdbc.queryForObject("SELECT COUNT(1) FROM SPTRANSDBA.HIS_SOL_SITUACOES", Long.class);
        return total != null ? total : 0L;
    }

    @Override
    public Long findMaxSeqHistSdis(Long numSolicitacao, String codCanal) {
        String sql = """
                SELECT COALESCE(MAX(SEQ_HIST_SDS), 0)
                FROM SPTRANSDBA.HIS_SOL_SITUACOES
                WHERE NUM_SOLICITACAO = ? AND COD_CANAL = ?
                """;
        Long max = jdbc.queryForObject(sql, Long.class, numSolicitacao, codCanal);
        return max != null ? max : 0L;
    }

    @Override
    public List<HistCreditRequest> findLatestBySolicitacao(Long numSolicitacao, String codCanal) {
        String sql = SQL_SELECT_BASE + "WHERE NUM_SOLICITACAO = ? AND COD_CANAL = ? ORDER BY SEQ_HIST_SDS DESC";
        return jdbc.query(sql, this::mapRow, numSolicitacao, codCanal);
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private HistCreditRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
        var key = new HistCreditRequestKey();
        key.setNumSolicitacao(getLong(rs, "NUM_SOLICITACAO"));
        key.setCodCanal(rs.getString("COD_CANAL"));
        key.setSeqHistSdis(getLong(rs, "SEQ_HIST_SDS"));

        var h = new HistCreditRequest();
        h.setId(key);
        h.setCodTipoDocumento(rs.getString("COD_TIPO_DOCUMENTO"));
        h.setCodSituacao(rs.getString("COD_SITUACAO"));
        h.setDtTransicao(toLocalDateTime(rs.getTimestamp("DT_TRANSICAO")));
        h.setIdOrigemTransicao(rs.getString("ID_ORIGEM_TRANSICAO"));
        h.setDtCadastro(toLocalDateTime(rs.getTimestamp("DT_CADASTRO")));
        h.setDtManutencao(toLocalDateTime(rs.getTimestamp("DT_MANUTENCAO")));
        h.setDtPgtoEconomica(toLocalDateTime(rs.getTimestamp("DT_PGTO_ECONOMICA")));
        h.setSqPID(getLong(rs, "SQ_PID"));
        h.setDtInicProcesso(toLocalDateTime(rs.getTimestamp("DT_INIC_PROCESSO")));
        h.setDtFimProcesso(toLocalDateTime(rs.getTimestamp("DT_FIM_PROCESSO")));
        return h;
    }

    private LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }

    private Long getLong(ResultSet rs, String column) throws SQLException {
        long val = rs.getLong(column);
        return rs.wasNull() ? null : val;
    }
}
