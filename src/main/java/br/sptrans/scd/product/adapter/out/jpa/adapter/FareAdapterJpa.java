package br.sptrans.scd.product.adapter.out.jpa.adapter;

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
import br.sptrans.scd.product.application.port.out.FareRepository;
import br.sptrans.scd.product.domain.Fare;
import br.sptrans.scd.product.domain.Product;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FareAdapterJpa implements FareRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<Fare> rowMapper = (rs, rowNum) -> mapRow(rs);

    // -------------------------------------------------------------------------
    // SQL
    // -------------------------------------------------------------------------
    private static final String SQL_SELECT_BASE = """
        SELECT COD_TARIFA, COD_VERSAO, DT_VIGENCIA_INI, DT_VIGENCIA_FIM,
               DT_CADASTRO, DT_MANUTENCAO, DES_TARIFA, ST_TARIFAS, VL_TARIFA,
               ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO, COD_PRODUTO
        FROM SPTRANSDBA.TARIFAS
        """;

    private static final String SQL_INSERT = """
        INSERT INTO SPTRANSDBA.TARIFAS (
            COD_TARIFA, COD_VERSAO, DT_VIGENCIA_INI, DT_VIGENCIA_FIM,
            DT_CADASTRO, DT_MANUTENCAO, DES_TARIFA, ST_TARIFAS, VL_TARIFA,
            ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO, COD_PRODUTO
        ) VALUES (?,?,?,?,SYSDATE,SYSDATE,?,?,?,?,?,?)
        """;

    private static final String SQL_UPDATE = """
        UPDATE SPTRANSDBA.TARIFAS SET
            COD_VERSAO            = ?,
            DT_VIGENCIA_INI       = ?,
            DT_VIGENCIA_FIM       = ?,
            DES_TARIFA            = ?,
            ST_TARIFAS            = ?,
            VL_TARIFA             = ?,
            DT_MANUTENCAO         = SYSDATE,
            ID_USUARIO_MANUTENCAO = ?,
            COD_PRODUTO           = ?
        WHERE COD_TARIFA = ?
        """;

    // -------------------------------------------------------------------------
    // Interface
    // -------------------------------------------------------------------------
    @Override
    public Optional<Fare> findById(String codTarifa) {
        List<Fare> result = jdbc.query(SQL_SELECT_BASE + "WHERE COD_TARIFA = ?",
                rowMapper, codTarifa);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public Fare save(Fare tarifa) {
        if (findById(tarifa.getCodTarifa()).isPresent()) {
            jdbc.update(SQL_UPDATE,
                    tarifa.getCodVersao(),
                    ts(tarifa.getDtVigenciaIni()),
                    ts(tarifa.getDtVigenciaFim()),
                    tarifa.getDesTarifa(),
                    tarifa.getStTarifas(),
                    tarifa.getVlTarifa(),
                    tarifa.getIdUsuarioManutencao() != null
                            ? tarifa.getIdUsuarioManutencao().getIdUsuario() : null,
                    tarifa.getCodProduto() != null
                            ? tarifa.getCodProduto().getCodProduto() : null,
                    tarifa.getCodTarifa());
        } else {
            jdbc.update(SQL_INSERT,
                    tarifa.getCodTarifa(),
                    tarifa.getCodVersao(),
                    ts(tarifa.getDtVigenciaIni()),
                    ts(tarifa.getDtVigenciaFim()),
                    tarifa.getDesTarifa(),
                    tarifa.getStTarifas(),
                    tarifa.getVlTarifa(),
                    tarifa.getIdUsuarioCadastro() != null
                            ? tarifa.getIdUsuarioCadastro().getIdUsuario() : null,
                    tarifa.getIdUsuarioManutencao() != null
                            ? tarifa.getIdUsuarioManutencao().getIdUsuario() : null,
                    tarifa.getCodProduto() != null
                            ? tarifa.getCodProduto().getCodProduto() : null);
        }
        return findById(tarifa.getCodTarifa()).orElseThrow();
    }

    @Override
    public void extendsValidity(String codTarifa, LocalDateTime dtFinal, Long idUsuario) {
        jdbc.update("""
            UPDATE SPTRANSDBA.TARIFAS SET
                DT_VIGENCIA_FIM       = ?,
                DT_MANUTENCAO         = SYSDATE,
                ID_USUARIO_MANUTENCAO = ?
            WHERE COD_TARIFA = ?
            """, ts(dtFinal), idUsuario, codTarifa);
    }

    @Override
    public List<Fare> listByProductChannel(String codProduto, String codCanal) {
        if (codCanal != null && !codCanal.isBlank()) {
            return jdbc.query(
                    SQL_SELECT_BASE + "WHERE COD_PRODUTO = ? AND COD_CANAL = ? ORDER BY DT_VIGENCIA_INI",
                    rowMapper, codProduto, codCanal);
        }
        return jdbc.query(
                SQL_SELECT_BASE + "WHERE COD_PRODUTO = ? ORDER BY DT_VIGENCIA_INI",
                rowMapper, codProduto);
    }

    @Override
    public boolean isConflictValidity(String codProduto, String codCanal,
            LocalDateTime dtInicial, LocalDateTime dtFinal, Long excluirIdTaxa) {
        String sql = """
            SELECT COUNT(1) FROM SPTRANSDBA.TARIFAS
            WHERE COD_PRODUTO = ?
              AND COD_CANAL   = ?
              AND COD_TARIFA  != ?
              AND DT_VIGENCIA_INI < ?
              AND (DT_VIGENCIA_FIM IS NULL OR DT_VIGENCIA_FIM > ?)
            """;
        Integer count = jdbc.queryForObject(sql, Integer.class,
                codProduto, codCanal, excluirIdTaxa, ts(dtFinal), ts(dtInicial));
        return count != null && count > 0;
    }

    @Override
    public Optional<Fare> searchCurrent(String codProduto, String codCanal,
            LocalDateTime dataOperacao) {
        String sql = SQL_SELECT_BASE + """
            WHERE COD_PRODUTO = ?
              AND COD_CANAL   = ?
              AND DT_VIGENCIA_INI <= ?
              AND (DT_VIGENCIA_FIM IS NULL OR DT_VIGENCIA_FIM >= ?)
            ORDER BY DT_VIGENCIA_INI DESC
            FETCH FIRST 1 ROWS ONLY
            """;
        List<Fare> result = jdbc.query(sql, rowMapper,
                codProduto, codCanal, ts(dataOperacao), ts(dataOperacao));
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    // -------------------------------------------------------------------------
    // RowMapper
    // -------------------------------------------------------------------------
    private Fare mapRow(ResultSet rs) throws SQLException {
        try {
            Fare f = new Fare();
            setField(f, "codTarifa",    rs.getString("COD_TARIFA"));
            setField(f, "codVersao",    rs.getString("COD_VERSAO"));
            setField(f, "dtVigenciaIni", fromTs(rs.getTimestamp("DT_VIGENCIA_INI")));
            setField(f, "dtVigenciaFim", fromTs(rs.getTimestamp("DT_VIGENCIA_FIM")));
            setField(f, "dtCadastro",   fromTs(rs.getTimestamp("DT_CADASTRO")));
            setField(f, "dtManutencao", fromTs(rs.getTimestamp("DT_MANUTENCAO")));
            setField(f, "desTarifa",    rs.getString("DES_TARIFA"));
            setField(f, "stTarifas",    rs.getString("ST_TARIFAS"));
            setField(f, "vlTarifa",     rs.getObject("VL_TARIFA", Integer.class));

            User usuCad = new User();
            usuCad.setIdUsuario(rs.getLong("ID_USUARIO_CADASTRO"));
            setField(f, "idUsuarioCadastro", usuCad);

            User usuMan = new User();
            usuMan.setIdUsuario(rs.getLong("ID_USUARIO_MANUTENCAO"));
            setField(f, "idUsuarioManutencao", usuMan);

            Product produto = new Product();
            var pf = Product.class.getDeclaredField("codProduto");
            pf.setAccessible(true);
            pf.set(produto, rs.getString("COD_PRODUTO"));
            setField(f, "codProduto", produto);

            return f;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setField(Fare f, String fieldName, Object value) throws Exception {
        var field = Fare.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(f, value);
    }

    private static Timestamp ts(LocalDateTime ldt) {
        return ldt != null ? Timestamp.valueOf(ldt) : null;
    }

    private static LocalDateTime fromTs(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }
}
