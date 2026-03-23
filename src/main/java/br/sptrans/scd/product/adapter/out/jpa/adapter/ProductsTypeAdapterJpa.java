package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.product.application.port.out.ProductsTypeRepository;
import br.sptrans.scd.product.domain.ProductType;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductsTypeAdapterJpa implements ProductsTypeRepository {

    private final JdbcTemplate jdbc;

    private static final String SQL_SELECT_BASE = """
        SELECT COD_TIPO_PRODUTO, DES_TIPO_PRODUTO, ST_TIPOS_PRODUTOS,
               DT_CADASTRO, DT_MANUTENCAO,
               ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO
        FROM SPTRANSDBA.TIPOS_PRODUTOS
        """;

    private static final String SQL_INSERT = """
        INSERT INTO SPTRANSDBA.TIPOS_PRODUTOS (
            COD_TIPO_PRODUTO, DES_TIPO_PRODUTO, ST_TIPOS_PRODUTOS,
            DT_CADASTRO, DT_MANUTENCAO,
            ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO
        ) VALUES (?,?,?,SYSDATE,SYSDATE,?,?)
        """;

    private static final String SQL_UPDATE = """
        UPDATE SPTRANSDBA.TIPOS_PRODUTOS SET
            DES_TIPO_PRODUTO      = ?,
            DT_MANUTENCAO         = SYSDATE,
            ID_USUARIO_MANUTENCAO = ?
        WHERE COD_TIPO_PRODUTO = ?
        """;

    private static final String SQL_UPDATE_STATUS = """
        UPDATE SPTRANSDBA.TIPOS_PRODUTOS SET
            ST_TIPOS_PRODUTOS     = ?,
            DT_MANUTENCAO         = SYSDATE,
            ID_USUARIO_MANUTENCAO = ?
        WHERE COD_TIPO_PRODUTO = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM SPTRANSDBA.TIPOS_PRODUTOS WHERE COD_TIPO_PRODUTO = ?";

    @Override
    public Optional<ProductType> findById(String codTipoProduto) {
        List<ProductType> result = jdbc.query(SQL_SELECT_BASE + "WHERE COD_TIPO_PRODUTO = ?", rowMapper(), codTipoProduto);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public boolean existsById(String codTipoProduto) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(1) FROM SPTRANSDBA.TIPOS_PRODUTOS WHERE COD_TIPO_PRODUTO = ?",
                Integer.class, codTipoProduto);
        return count != null && count > 0;
    }

    @Override
    public List<ProductType> findAll(String codStatus) {
        if (codStatus != null && !codStatus.isBlank()) {
            return jdbc.query(SQL_SELECT_BASE + "WHERE ST_TIPOS_PRODUTOS = ? ORDER BY COD_TIPO_PRODUTO",
                    rowMapper(), codStatus);
        }
        return jdbc.query(SQL_SELECT_BASE + "ORDER BY COD_TIPO_PRODUTO", rowMapper());
    }

    @Override
    public ProductType save(ProductType productType) {
        if (existsById(productType.getCodTipoProduto())) {
            jdbc.update(SQL_UPDATE,
                    productType.getDesTipoProduto(),
                    productType.getIdUsuarioManutencao() != null ? productType.getIdUsuarioManutencao().getIdUsuario() : null,
                    productType.getCodTipoProduto()
            );
        } else {
            jdbc.update(SQL_INSERT,
                    productType.getCodTipoProduto(),
                    productType.getDesTipoProduto(),
                    productType.getCodStatus(),
                    productType.getIdUsuarioCadastro() != null ? productType.getIdUsuarioCadastro().getIdUsuario() : null,
                    productType.getIdUsuarioManutencao() != null ? productType.getIdUsuarioManutencao().getIdUsuario() : null
            );
        }
        return findById(productType.getCodTipoProduto()).orElseThrow();
    }

    @Override
    public void updateStatus(String codTipoProduto, String codStatus, Long idUsuario) {
        jdbc.update(SQL_UPDATE_STATUS, codStatus, idUsuario, codTipoProduto);
    }

    @Override
    public void deleteById(String codTipoProduto) {
        jdbc.update(SQL_DELETE, codTipoProduto);
    }

    private RowMapper<ProductType> rowMapper() {
        return (rs, rowNum) -> new ProductType(
                rs.getString("COD_TIPO_PRODUTO"),
                rs.getString("DES_TIPO_PRODUTO"),
                rs.getString("ST_TIPOS_PRODUTOS"),
                toLocalDateTime(rs.getTimestamp("DT_CADASTRO")),
                toLocalDateTime(rs.getTimestamp("DT_MANUTENCAO")),
                null,
                null
        );
    }

    private LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }
}
