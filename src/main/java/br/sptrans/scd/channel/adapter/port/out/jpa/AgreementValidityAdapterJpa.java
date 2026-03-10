package br.sptrans.scd.channel.adapter.port.out.jpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.channel.application.port.out.AgreementValidityRepository;
import br.sptrans.scd.channel.domain.AgreementValidity;
import br.sptrans.scd.channel.domain.AgreementValidityKey;
import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.channel.domain.ProductChannelKey;
import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.product.domain.Product;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AgreementValidityAdapterJpa implements AgreementValidityRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<AgreementValidity> rowMapper = (rs, rowNum) -> mapRow(rs);

    // -------------------------------------------------------------------------
    // SQL
    // -------------------------------------------------------------------------
    private static final String SQL_SELECT_BASE = """
            SELECT COD_CANAL, COD_PRODUTO, DT_FIM_VALIDADE, DT_INICIO_VALIDADE,
                   COD_STATUS, DT_MANUTENCAO, ID_USUARIO
            FROM SPTRANSDBA.CONVENIOS_VIGENCIAS
            """;

    private static final String SQL_INSERT = """
            INSERT INTO SPTRANSDBA.CONVENIOS_VIGENCIAS (
                COD_CANAL, COD_PRODUTO, DT_FIM_VALIDADE, DT_INICIO_VALIDADE,
                COD_STATUS, DT_MANUTENCAO, ID_USUARIO
            ) VALUES (?,?,?,?,?,SYSDATE,?)
            """;

    private static final String SQL_UPDATE = """
            UPDATE SPTRANSDBA.CONVENIOS_VIGENCIAS SET
                DT_FIM_VALIDADE    = ?,
                DT_INICIO_VALIDADE = ?,
                COD_STATUS         = ?,
                DT_MANUTENCAO      = SYSDATE,
                ID_USUARIO         = ?
            WHERE COD_CANAL = ? AND COD_PRODUTO = ?
            """;

    private static final String SQL_DELETE = """
            DELETE FROM SPTRANSDBA.CONVENIOS_VIGENCIAS
            WHERE COD_CANAL = ? AND COD_PRODUTO = ?
            """;

    private static final String SQL_EXISTS = """
            SELECT COUNT(1) FROM SPTRANSDBA.CONVENIOS_VIGENCIAS
            WHERE COD_CANAL = ? AND COD_PRODUTO = ?
            """;

    // -------------------------------------------------------------------------
    // Interface
    // -------------------------------------------------------------------------

    @Override
    public Optional<AgreementValidity> findById(AgreementValidityKey id) {
        List<AgreementValidity> result = jdbc.query(
                SQL_SELECT_BASE + "WHERE COD_CANAL = ? AND COD_PRODUTO = ?",
                rowMapper, id.getCodCanal(), id.getCodProduto());
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<AgreementValidity> findAll() {
        return jdbc.query(SQL_SELECT_BASE, rowMapper);
    }

    @Override
    public List<AgreementValidity> findByCodCanal(String codCanal) {
        return jdbc.query(SQL_SELECT_BASE + "WHERE COD_CANAL = ?", rowMapper, codCanal);
    }

    @Override
    public List<AgreementValidity> findByCodProduto(String codProduto) {
        return jdbc.query(SQL_SELECT_BASE + "WHERE COD_PRODUTO = ?", rowMapper, codProduto);
    }

    @Override
    public AgreementValidity save(AgreementValidity entity) {
        AgreementValidityKey key = entity.getId();
        if (existsById(key)) {
            jdbc.update(SQL_UPDATE,
                    ts(entity.getDataFimValidade()),
                    ts(entity.getDataInicioValidade()),
                    entity.getStatus(),
                    entity.getIdUsuario(),
                    key.getCodCanal(),
                    key.getCodProduto());
        } else {
            jdbc.update(SQL_INSERT,
                    key.getCodCanal(),
                    key.getCodProduto(),
                    ts(entity.getDataFimValidade()),
                    ts(entity.getDataInicioValidade()),
                    entity.getStatus(),
                    entity.getIdUsuario());
        }
        return findById(key).orElseThrow();
    }

    @Override
    public void deleteById(AgreementValidityKey id) {
        jdbc.update(SQL_DELETE, id.getCodCanal(), id.getCodProduto());
    }

    @Override
    public boolean existsById(AgreementValidityKey id) {
        Integer count = jdbc.queryForObject(SQL_EXISTS, Integer.class,
                id.getCodCanal(), id.getCodProduto());
        return count != null && count > 0;
    }

    // -------------------------------------------------------------------------
    // RowMapper
    // -------------------------------------------------------------------------

    private AgreementValidity mapRow(ResultSet rs) throws SQLException {
        try {
            String codCanal   = rs.getString("COD_CANAL");
            String codProduto = rs.getString("COD_PRODUTO");

            SalesChannel canal = new SalesChannel();
            setRef(canal, SalesChannel.class, "codCanal", codCanal);

            Product produto = new Product();
            setRef(produto, Product.class, "codProduto", codProduto);

            ProductChannel canalProduto = new ProductChannel();
            canalProduto.setId(new ProductChannelKey(codCanal, codProduto));

            AgreementValidity av = new AgreementValidity();
            av.setId(new AgreementValidityKey(codCanal, codProduto));
            av.setCanal(canal);
            av.setProduto(produto);
            av.setCanalProduto(canalProduto);
            av.setDataFimValidade(fromTs(rs.getTimestamp("DT_FIM_VALIDADE")));
            av.setDataInicioValidade(fromTs(rs.getTimestamp("DT_INICIO_VALIDADE")));
            av.setStatus(rs.getString("COD_STATUS"));
            av.setDataManutencao(fromTs(rs.getTimestamp("DT_MANUTENCAO")));
            av.setIdUsuario(rs.getObject("ID_USUARIO", Long.class));
            return av;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static void setRef(Object target, Class<?> clazz, String fieldName, Object value)
            throws Exception {
        var field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static Timestamp ts(LocalDateTime ldt) {
        return ldt != null ? Timestamp.valueOf(ldt) : null;
    }

    private static LocalDateTime fromTs(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }
}
