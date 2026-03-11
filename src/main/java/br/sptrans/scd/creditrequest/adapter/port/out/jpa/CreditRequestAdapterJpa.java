package br.sptrans.scd.creditrequest.adapter.port.out.jpa;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestRepository;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CreditRequestAdapterJpa implements CreditRequestRepository {

    private final JdbcTemplate jdbc;

    // ── Consultas existentes ─────────────────────────────────────────

    @Override
    public Optional<CreditRequest> findByNumSolicitacaoAndCodCanal(Long numSolicitacao, String codCanal) {
        String sql = """
                SELECT * FROM SPTRANSDBA.SOL_DISTRIBUICOES s
                WHERE s.NUM_SOLICITACAO = ? AND s.COD_CANAL = ?
                """;
        List<CreditRequest> result = jdbc.query(sql, this::mapRow, numSolicitacao, codCanal);
        return result.stream().findFirst();
    }

    @Override
    public List<CreditRequest> findByCanalAndSituacao(String codCanal, String codSituacao) {
        String sql = """
                SELECT * FROM SPTRANSDBA.SOL_DISTRIBUICOES s
                WHERE s.COD_CANAL = ? AND s.COD_SITUACAO = ?
                """;
        return jdbc.query(sql, this::mapRow, codCanal, codSituacao);
    }

    @Override
    public boolean existsByNumSolicitacao(Long numSolicitacao) {
        String sql = "SELECT COUNT(1) FROM SPTRANSDBA.SOL_DISTRIBUICOES WHERE NUM_SOLICITACAO = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, numSolicitacao);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByNumLoteAndCodCanal(String numLote, String codCanal) {
        String sql = "SELECT COUNT(1) FROM SPTRANSDBA.SOL_DISTRIBUICOES WHERE NUM_LOTE = ? AND COD_CANAL = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, numLote, codCanal);
        return count != null && count > 0;
    }

    @Override
    public CreditRequest findElegiveisParaLiberacao(String codSituacao, LocalDateTime dtInicio, LocalDateTime dtFim) {
        String sql = """
                SELECT * FROM SPTRANSDBA.SOL_DISTRIBUICOES s
                WHERE s.COD_SITUACAO = ?
                  AND s.DT_SOLICITACAO >= ? AND s.DT_SOLICITACAO <= ?
                """;
        List<CreditRequest> result = jdbc.query(sql, this::mapRow, codSituacao, dtInicio, dtFim);
        return result.stream().findFirst().orElse(null);
    }

    @Override
    public CreditRequest findElegiveisParaProcessamento(String codSituacao) {
        String sql = """
                SELECT * FROM SPTRANSDBA.SOL_DISTRIBUICOES s
                WHERE s.COD_SITUACAO = ?
                """;
        List<CreditRequest> result = jdbc.query(sql, this::mapRow, codSituacao);
        return result.stream().findFirst().orElse(null);
    }

    @Override
    public CreditRequest findElegiveisParaConfirmacao(String codSituacao) {
        String sql = """
                SELECT * FROM SPTRANSDBA.SOL_DISTRIBUICOES s
                WHERE s.COD_SITUACAO = ?
                """;
        List<CreditRequest> result = jdbc.query(sql, this::mapRow, codSituacao);
        return result.stream().findFirst().orElse(null);
    }

    // ── Busca paginada por cursor ────────────────────────────────────

    @Override
    public List<CreditRequest> findWithCursor(
            Long cursorNumSolicitacao,
            String cursorCodCanal,
            String codCanal,
            String codSituacao,
            String numLote,
            String codFormaPagto,
            LocalDateTime dtInicio,
            LocalDateTime dtFim,
            LocalDateTime dtLiberacaoEfetivaInicio,
            LocalDateTime dtLiberacaoEfetivaFim,
            LocalDateTime dtPagtoEconomicaInicio,
            LocalDateTime dtPagtoEconomicaFim,
            LocalDateTime dtFinanceiraInicio,
            LocalDateTime dtFinanceiraFim,
            LocalDateTime dtAlteracaoInicio,
            LocalDateTime dtAlteracaoFim,
            BigDecimal vlTotalMin,
            BigDecimal vlTotalMax,
            int limit) {

        var sql = new StringBuilder("""
                SELECT * FROM SPTRANSDBA.SOL_DISTRIBUICOES s
                WHERE 1=1
                """);
        var params = new ArrayList<>();

        // Cursor: busca a próxima página
        if (cursorNumSolicitacao != null) {
            sql.append("""
                      AND (s.NUM_SOLICITACAO < ?
                           OR (s.NUM_SOLICITACAO = ? AND s.COD_CANAL > ?))
                    """);
            params.add(cursorNumSolicitacao);
            params.add(cursorNumSolicitacao);
            params.add(cursorCodCanal);
        }

        // Filtros opcionais
        appendIfNotNull(sql, params, "AND s.COD_CANAL = ?", codCanal);
        appendIfNotNull(sql, params, "AND s.COD_SITUACAO = ?", codSituacao);
        appendIfNotNull(sql, params, "AND s.NUM_LOTE = ?", numLote);
        appendIfNotNull(sql, params, "AND s.COD_FORMA_PAGTO = ?", codFormaPagto);
        appendIfNotNull(sql, params, "AND s.DT_SOLICITACAO >= ?", dtInicio);
        appendIfNotNull(sql, params, "AND s.DT_SOLICITACAO <= ?", dtFim);
        appendIfNotNull(sql, params, "AND s.DT_LIBERACAO_EFETIVA >= ?", dtLiberacaoEfetivaInicio);
        appendIfNotNull(sql, params, "AND s.DT_LIBERACAO_EFETIVA <= ?", dtLiberacaoEfetivaFim);
        appendIfNotNull(sql, params, "AND s.DT_PAGTO_ECONOMICA >= ?", dtPagtoEconomicaInicio);
        appendIfNotNull(sql, params, "AND s.DT_PAGTO_ECONOMICA <= ?", dtPagtoEconomicaFim);
        appendIfNotNull(sql, params, "AND s.DT_FINANCEIRA >= ?", dtFinanceiraInicio);
        appendIfNotNull(sql, params, "AND s.DT_FINANCEIRA <= ?", dtFinanceiraFim);
        appendIfNotNull(sql, params, "AND s.DT_MANUTENCAO >= ?", dtAlteracaoInicio);
        appendIfNotNull(sql, params, "AND s.DT_MANUTENCAO <= ?", dtAlteracaoFim);
        appendIfNotNull(sql, params, "AND s.VL_TOTAL >= ?", vlTotalMin);
        appendIfNotNull(sql, params, "AND s.VL_TOTAL <= ?", vlTotalMax);

        sql.append(" ORDER BY s.NUM_SOLICITACAO DESC, s.COD_CANAL ASC");
        sql.append(" FETCH FIRST ? ROWS ONLY");
        params.add(limit);

        return jdbc.query(sql.toString(), this::mapRow, params.toArray());
    }

    // ── Atualizações ─────────────────────────────────────────────────

    @Override
    public void update(Long numSolicitacao, String codCanal, CreditRequest cr) {
        String sql = """
                UPDATE SPTRANSDBA.SOL_DISTRIBUICOES
                SET COD_SITUACAO = ?,
                    COD_FORMA_PAGTO = ?,
                    DT_ACEITE = ?,
                    DT_CONFIRMA_PAGTO = ?,
                    DT_PAGTO_ECONOMICA = ?,
                    DT_LIBERACAO_EFETIVA = ?,
                    VL_PAGO = ?,
                    FLG_CANC = ?,
                    FLG_BLOQ = ?,
                    NUM_LOTE = ?,
                    DT_MANUTENCAO = ?
                WHERE NUM_SOLICITACAO = ? AND COD_CANAL = ?
                """;
        jdbc.update(sql,
                cr.getCodSituacao(),
                cr.getCodFormaPagto(),
                cr.getDtAceite(),
                cr.getDtConfirmaPagto(),
                cr.getDtPagtoEconomica(),
                cr.getDtLiberacaoEfetiva(),
                cr.getVlPago(),
                cr.getFlgCanc(),
                cr.getFlgBloq(),
                cr.getNumLote(),
                LocalDateTime.now(),
                numSolicitacao,
                codCanal);
    }

    @Override
    public Optional<CreditRequest> findByCodTipoDocumentoAndIdUsuarioCadastro(
            String codTipoDocumento, Long idUsuarioCadastro) {
        String sql = """
                SELECT * FROM SPTRANSDBA.SOL_DISTRIBUICOES s
                WHERE s.COD_TIPO_DOCUMENTO = ? AND s.ID_USUARIO_CADASTRO = ?
                ORDER BY s.DT_SOLICITACAO DESC
                FETCH FIRST 1 ROWS ONLY
                """;
        List<CreditRequest> result = jdbc.query(sql, this::mapRow, codTipoDocumento, idUsuarioCadastro);
        return result.stream().findFirst();
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private void appendIfNotNull(StringBuilder sql, List<Object> params, String clause, Object value) {
        if (value != null) {
            sql.append(" ").append(clause);
            params.add(value);
        }
    }

    private CreditRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
        var cr = new CreditRequest();
        cr.setNumSolicitacao(getLong(rs, "NUM_SOLICITACAO"));
        cr.setCodCanal(rs.getString("COD_CANAL"));
        cr.setIdUsuarioCadastro(getLong(rs, "ID_USUARIO_CADASTRO"));
        cr.setCodTipoDocumento(rs.getString("COD_TIPO_DOCUMENTO"));
        cr.setCodSituacao(rs.getString("COD_SITUACAO"));
        cr.setCodFormaPagto(rs.getString("COD_FORMA_PAGTO"));
        cr.setDtSolicitacao(toLocalDateTime(rs.getTimestamp("DT_SOLICITACAO")));
        cr.setDtPrevLiberacao(toLocalDateTime(rs.getTimestamp("DT_PREV_LIBERACAO")));
        cr.setDtAceite(toLocalDateTime(rs.getTimestamp("DT_ACEITE")));
        cr.setDtConfirmaPagto(toLocalDateTime(rs.getTimestamp("DT_CONFIRMA_PAGTO")));
        cr.setDtPagtoEconomica(toLocalDateTime(rs.getTimestamp("DT_PAGTO_ECONOMICA")));
        cr.setCodUsuarioPortador(rs.getString("COD_USUARIO_PORTADOR"));
        cr.setDtLiberacaoEfetiva(toLocalDateTime(rs.getTimestamp("DT_LIBERACAO_EFETIVA")));
        cr.setCodEnderecoEntrega(rs.getString("COD_ENDERECO_ENTREGA"));
        cr.setNumLote(rs.getString("NUM_LOTE"));
        cr.setDtFinanceira(toLocalDateTime(rs.getTimestamp("DT_FINANCEIRA")));
        cr.setVlTotal(rs.getBigDecimal("VL_TOTAL"));
        cr.setDtCadastro(toLocalDateTime(rs.getTimestamp("DT_CADASTRO")));
        cr.setFlgCanc(rs.getString("FLG_CANC"));
        cr.setDtManutencao(toLocalDateTime(rs.getTimestamp("DT_MANUTENCAO")));
        cr.setDtEnvioHm(toLocalDateTime(rs.getTimestamp("DT_ENVIO_HM")));
        cr.setIdUsuarioManutencao(getLong(rs, "ID_USUARIO_MANUTENCAO"));
        cr.setFlgBloq(rs.getString("FLG_BLOQ"));
        cr.setVlPago(rs.getBigDecimal("VL_PAGO"));
        return cr;
    }

    private LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }

    private Long getLong(ResultSet rs, String column) throws SQLException {
        long val = rs.getLong(column);
        return rs.wasNull() ? null : val;
    }
}
