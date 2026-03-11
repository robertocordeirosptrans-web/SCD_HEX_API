package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.application.port.out.MarketingDistribuitionChannelRepository;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannel;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannelKey;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MarketingDistribuitionChannelAdapterJpa implements MarketingDistribuitionChannelRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<MarketingDistribuitionChannel> rowMapper = (rs, rowNum) -> mapRow(rs);

    // -------------------------------------------------------------------------
    // SQL
    // -------------------------------------------------------------------------
    private static final String SQL_SELECT_BASE = """
            SELECT COD_CANAL_COMERC, COD_CANAL_DISTRIB, ST_CANAL_COMERC_DISTRIB,
                   DT_CADASTRO, DT_MANUTENCAO, ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO
            FROM SPTRANSDBA.CANAIS_COMERC_DISTRIB
            """;

    private static final String SQL_INSERT = """
            INSERT INTO SPTRANSDBA.CANAIS_COMERC_DISTRIB (
                COD_CANAL_COMERC, COD_CANAL_DISTRIB, ST_CANAL_COMERC_DISTRIB,
                DT_CADASTRO, DT_MANUTENCAO, ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO
            ) VALUES (?,?,?,SYSDATE,SYSDATE,?,?)
            """;

    private static final String SQL_UPDATE = """
            UPDATE SPTRANSDBA.CANAIS_COMERC_DISTRIB SET
                ST_CANAL_COMERC_DISTRIB  = ?,
                DT_MANUTENCAO            = SYSDATE,
                ID_USUARIO_MANUTENCAO    = ?
            WHERE COD_CANAL_COMERC = ? AND COD_CANAL_DISTRIB = ?
            """;

    private static final String SQL_DELETE = """
            DELETE FROM SPTRANSDBA.CANAIS_COMERC_DISTRIB
            WHERE COD_CANAL_COMERC = ? AND COD_CANAL_DISTRIB = ?
            """;

    private static final String SQL_EXISTS = """
            SELECT COUNT(1) FROM SPTRANSDBA.CANAIS_COMERC_DISTRIB
            WHERE COD_CANAL_COMERC = ? AND COD_CANAL_DISTRIB = ?
            """;

    // -------------------------------------------------------------------------
    // Interface
    // -------------------------------------------------------------------------

    @Override
    public Optional<MarketingDistribuitionChannel> findById(MarketingDistribuitionChannelKey id) {
        List<MarketingDistribuitionChannel> result = jdbc.query(
                SQL_SELECT_BASE + "WHERE COD_CANAL_COMERC = ? AND COD_CANAL_DISTRIB = ?",
                rowMapper, id.getCodCanalComercializacao(), id.getCodCanalDistribuicao());
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<MarketingDistribuitionChannel> findAll() {
        return jdbc.query(SQL_SELECT_BASE, rowMapper);
    }

    @Override
    public List<MarketingDistribuitionChannel> findByCodCanalComercializacao(String codCanalComercializacao) {
        return jdbc.query(SQL_SELECT_BASE + "WHERE COD_CANAL_COMERC = ?", rowMapper, codCanalComercializacao);
    }

    @Override
    public List<MarketingDistribuitionChannel> findByCodCanalDistribuicao(String codCanalDistribuicao) {
        return jdbc.query(SQL_SELECT_BASE + "WHERE COD_CANAL_DISTRIB = ?", rowMapper, codCanalDistribuicao);
    }

    @Override
    public MarketingDistribuitionChannel save(MarketingDistribuitionChannel entity) {
        MarketingDistribuitionChannelKey key = entity.getId();
        if (existsById(key)) {
            jdbc.update(SQL_UPDATE,
                    entity.getCodStatus(),
                    entity.getIdUsuarioManutencao() != null ? entity.getIdUsuarioManutencao().getIdUsuario() : null,
                    key.getCodCanalComercializacao(),
                    key.getCodCanalDistribuicao());
        } else {
            jdbc.update(SQL_INSERT,
                    key.getCodCanalComercializacao(),
                    key.getCodCanalDistribuicao(),
                    entity.getCodStatus(),
                    entity.getIdUsuarioCadastro() != null ? entity.getIdUsuarioCadastro().getIdUsuario() : null,
                    entity.getIdUsuarioManutencao() != null ? entity.getIdUsuarioManutencao().getIdUsuario() : null);
        }
        return findById(key).orElseThrow();
    }

    @Override
    public void deleteById(MarketingDistribuitionChannelKey id) {
        jdbc.update(SQL_DELETE, id.getCodCanalComercializacao(), id.getCodCanalDistribuicao());
    }

    @Override
    public boolean existsById(MarketingDistribuitionChannelKey id) {
        Integer count = jdbc.queryForObject(SQL_EXISTS, Integer.class,
                id.getCodCanalComercializacao(), id.getCodCanalDistribuicao());
        return count != null && count > 0;
    }

    // -------------------------------------------------------------------------
    // RowMapper
    // -------------------------------------------------------------------------

    private MarketingDistribuitionChannel mapRow(ResultSet rs) throws SQLException {
        String codComercial = rs.getString("COD_CANAL_COMERC");
        String codDistrib   = rs.getString("COD_CANAL_DISTRIB");

        MarketingDistribuitionChannelKey key =
                new MarketingDistribuitionChannelKey(codComercial, codDistrib);

        User usuCad = new User();
        usuCad.setIdUsuario(rs.getLong("ID_USUARIO_CADASTRO"));

        User usuMan = new User();
        usuMan.setIdUsuario(rs.getLong("ID_USUARIO_MANUTENCAO"));

        return new MarketingDistribuitionChannel(
                key,
                rs.getString("ST_CANAL_COMERC_DISTRIB"),
                fromTs(rs.getTimestamp("DT_CADASTRO")),
                fromTs(rs.getTimestamp("DT_MANUTENCAO")),
                usuCad,
                usuMan,
                codComercial,
                codDistrib);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static Timestamp ts(LocalDateTime ldt) {
        return ldt != null ? Timestamp.valueOf(ldt) : null;
    }

    private static LocalDateTime fromTs(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }
}
