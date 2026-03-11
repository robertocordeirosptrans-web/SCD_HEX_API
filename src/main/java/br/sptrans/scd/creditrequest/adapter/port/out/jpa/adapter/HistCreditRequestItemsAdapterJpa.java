package br.sptrans.scd.creditrequest.adapter.port.out.jpa.adapter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.creditrequest.application.port.out.repository.HistCreditRequestItemsRepository;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestItems;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestItemsKey;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HistCreditRequestItemsAdapterJpa implements HistCreditRequestItemsRepository {

    private final JdbcTemplate jdbc;

    private static final String SQL_SELECT_BASE = """
            SELECT NUM_SOLICITACAO, NUM_SOLICITACAO_ITEM, COD_CANAL, SEQ_HIST_SDIS,
                   COD_TIPO_DOCUMENTO, COD_SITUACAO, DT_TRANSICAO, ID_ORIGEM_TRANSICAO,
                   DT_CADASTRO, DT_MANUTENCAO, DT_PGTO_ECONOMICA, SQ_PID,
                   DT_INIC_PROCESSO, DT_FIM_PROCESSO, ID_USUARIO_TRANSICAO
            FROM SPTRANSDBA.HIS_SOL_ITEM_SITUACOES
            """;

    private static final String SQL_INSERT = """
            INSERT INTO SPTRANSDBA.HIS_SOL_ITEM_SITUACOES
                (NUM_SOLICITACAO, NUM_SOLICITACAO_ITEM, COD_CANAL, SEQ_HIST_SDIS,
                 COD_TIPO_DOCUMENTO, COD_SITUACAO, DT_TRANSICAO, ID_ORIGEM_TRANSICAO,
                 DT_CADASTRO, DT_MANUTENCAO, DT_PGTO_ECONOMICA, SQ_PID,
                 DT_INIC_PROCESSO, DT_FIM_PROCESSO, ID_USUARIO_TRANSICAO)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    @Override
    public HistCreditRequestItems save(HistCreditRequestItems h) {
        jdbc.update(SQL_INSERT,
                h.getId().getNumSolicitacao(),
                h.getId().getNumSolicitacaoItem(),
                h.getId().getCodCanal(),
                h.getId().getSeqHistSdis(),
                h.getCodTipoDocumento(),
                h.getCodSituacao(),
                h.getDtTransicao(),
                h.getIdOrigemTransicao(),
                h.getDtCadastro(),
                h.getDtManutencao(),
                h.getDtPgtoEconomica(),
                h.getSqPID(),
                h.getDtInicProcesso(),
                h.getDtFimProcesso(),
                h.getIdUsuarioTransicao() != null ? h.getIdUsuarioTransicao().getIdUsuario() : null);
        return h;
    }

    @Override
    public List<HistCreditRequestItems> saveAll(List<HistCreditRequestItems> items) {
        items.forEach(this::save);
        return items;
    }

    @Override
    public Optional<HistCreditRequestItems> findById(HistCreditRequestItemsKey id) {
        String sql = SQL_SELECT_BASE +
                "WHERE NUM_SOLICITACAO = ? AND NUM_SOLICITACAO_ITEM = ? AND COD_CANAL = ? AND SEQ_HIST_SDIS = ?";
        List<HistCreditRequestItems> result = jdbc.query(sql, this::mapRow,
                id.getNumSolicitacao(), id.getNumSolicitacaoItem(), id.getCodCanal(), id.getSeqHistSdis());
        return result.stream().findFirst();
    }

    @Override
    public List<HistCreditRequestItems> findByNumSolicitacaoAndCodCanal(Long numSolicitacao, String codCanal) {
        String sql = SQL_SELECT_BASE + "WHERE NUM_SOLICITACAO = ? AND COD_CANAL = ? ORDER BY SEQ_HIST_SDIS DESC";
        return jdbc.query(sql, this::mapRow, numSolicitacao, codCanal);
    }

    @Override
    public List<HistCreditRequestItems> findAll() {
        return jdbc.query(SQL_SELECT_BASE + "ORDER BY NUM_SOLICITACAO, NUM_SOLICITACAO_ITEM, SEQ_HIST_SDIS DESC",
                this::mapRow);
    }

    @Override
    public boolean existsById(HistCreditRequestItemsKey id) {
        String sql = """
                SELECT COUNT(1) FROM SPTRANSDBA.HIS_SOL_ITEM_SITUACOES
                WHERE NUM_SOLICITACAO = ? AND NUM_SOLICITACAO_ITEM = ? AND COD_CANAL = ? AND SEQ_HIST_SDIS = ?
                """;
        Integer count = jdbc.queryForObject(sql, Integer.class,
                id.getNumSolicitacao(), id.getNumSolicitacaoItem(), id.getCodCanal(), id.getSeqHistSdis());
        return count != null && count > 0;
    }

    @Override
    public long count() {
        Long total = jdbc.queryForObject("SELECT COUNT(1) FROM SPTRANSDBA.HIS_SOL_ITEM_SITUACOES", Long.class);
        return total != null ? total : 0L;
    }

    @Override
    public Long findMaxSeqHistSdis(Long numSolicitacao, Long numSolicitacaoItem, String codCanal) {
        String sql = """
                SELECT COALESCE(MAX(SEQ_HIST_SDIS), 0)
                FROM SPTRANSDBA.HIS_SOL_ITEM_SITUACOES
                WHERE NUM_SOLICITACAO = ? AND NUM_SOLICITACAO_ITEM = ? AND COD_CANAL = ?
                """;
        Long max = jdbc.queryForObject(sql, Long.class, numSolicitacao, numSolicitacaoItem, codCanal);
        return max != null ? max : 0L;
    }

    @Override
    public List<HistCreditRequestItems> findLatestByItem(Long numSolicitacao, Long numSolicitacaoItem, String codCanal) {
        String sql = SQL_SELECT_BASE +
                "WHERE NUM_SOLICITACAO = ? AND NUM_SOLICITACAO_ITEM = ? AND COD_CANAL = ? ORDER BY SEQ_HIST_SDIS DESC";
        return jdbc.query(sql, this::mapRow, numSolicitacao, numSolicitacaoItem, codCanal);
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private HistCreditRequestItems mapRow(ResultSet rs, int rowNum) throws SQLException {
        var key = new HistCreditRequestItemsKey();
        key.setNumSolicitacao(getLong(rs, "NUM_SOLICITACAO"));
        key.setNumSolicitacaoItem(getLong(rs, "NUM_SOLICITACAO_ITEM"));
        key.setCodCanal(rs.getString("COD_CANAL"));
        key.setSeqHistSdis(getLong(rs, "SEQ_HIST_SDIS"));

        var h = new HistCreditRequestItems();
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
