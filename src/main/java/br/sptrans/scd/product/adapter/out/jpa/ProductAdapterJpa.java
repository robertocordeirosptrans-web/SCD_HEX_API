package br.sptrans.scd.product.adapter.out.jpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.product.application.port.out.ProductRepository;
import br.sptrans.scd.product.domain.Product;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductAdapterJpa implements ProductRepository {

    private final JdbcTemplate jdbc;

    // -------------------------------------------------------------------------
    // SQL
    // -------------------------------------------------------------------------
    private static final String SQL_SELECT_BASE = """
        SELECT COD_PRODUTO, DES_PRODUTO, DES_EMISSOR_RESPONSAVEL, ST_PRODUTOS,
               DES_UTILIZACAO, COD_CLASSIFICACAO_PESSOA, COD_TIPO_PRODUTO,
               COD_TECNOLOGIA, COD_MODALIDADE, COD_FAMILIA, COD_ESPECIE,

               DT_CADASTRO, DT_MANUTENCAO,
               ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO
        FROM SPTRANSDBA.PRODUTOS
        """;

    private static final String SQL_INSERT = """
        INSERT INTO SPTRANSDBA.PRODUTOS (
            COD_PRODUTO, DES_PRODUTO, DES_EMISSOR_RESPONSAVEL, ST_PRODUTOS,
            DES_UTILIZACAO, COD_CLASSIFICACAO_PESSOA, COD_TIPO_PRODUTO,
            COD_TECNOLOGIA, COD_MODALIDADE, COD_FAMILIA, COD_ESPECIE,
            DT_CADASTRO, DT_MANUTENCAO,
            ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO
        ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
        """;

    private static final String SQL_UPDATE = """
        UPDATE SPTRANSDBA.PRODUTOS SET
            DES_PRODUTO              = ?,
            DES_EMISSOR_RESPONSAVEL  = ?,
            DES_UTILIZACAO           = ?,
            COD_CLASSIFICACAO_PESSOA = ?,
            COD_TIPO_PRODUTO         = ?,
            COD_TECNOLOGIA           = ?,
            COD_MODALIDADE           = ?,
            COD_FAMILIA              = ?,
            COD_ESPECIE              = ?,
            DT_VIGENCIA_INI          = ?,
            DT_VIGENCIA_FIM          = ?,
            DT_MANUTENCAO            = SYSDATE,
            ID_USUARIO_MANUTENCAO    = ?
        WHERE COD_PRODUTO = ?
        """;

    private static final String SQL_UPDATE_STATUS = """
        UPDATE SPTRANSDBA.PRODUTOS SET
            ST_PRODUTOS           = ?,
            DT_MANUTENCAO         = SYSDATE,
            ID_USUARIO_MANUTENCAO = ?
        WHERE COD_PRODUTO = ?
        """;

    // -------------------------------------------------------------------------
    // Implementação
    // -------------------------------------------------------------------------
    public Optional<Product> findById(String codProduto) {
        String sql = SQL_SELECT_BASE + " WHERE COD_PRODUTO = ?";
        List<Product> result = jdbc.query(sql, rowMapper(), codProduto);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public boolean existsByProduct(String codProduto) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(1) FROM SPTRANSDBA.PRODUTOS WHERE COD_PRODUTO = ?",
                Integer.class, codProduto);
        return count != null && count > 0;
    }

    public Product save(Product produto) {

        if (existsByProduct(produto.getCodProduto())) {
            // UPDATE
            jdbc.update(SQL_UPDATE,
                    produto.getDesProduto(),
                    produto.getDesEmissorResponsavel(),
                    produto.getDesUtilizacao(),
                    produto.getCodClassificacaoPessoa(),
                    produto.getCodTipoProduto(),
                    produto.getCodTecnologia(),
                    produto.getCodModalidade(),
                    produto.getCodFamilia(),
                    produto.getCodEspecie(),
                    produto.getIdUsuarioManutencao(),
                    produto.getCodProduto()
            );
        } else {
            // INSERT
            jdbc.update(SQL_INSERT,
                    produto.getCodProduto(),
                    produto.getDesProduto(),
                    produto.getDesEmissorResponsavel(),
                    produto.getCodStatus(),
                    produto.getDesUtilizacao(),
                    produto.getCodClassificacaoPessoa(),
                    produto.getCodTipoProduto(),
                    produto.getCodTecnologia(),
                    produto.getCodModalidade(),
                    produto.getCodFamilia(),
                    produto.getCodEspecie(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    Timestamp.valueOf(LocalDateTime.now()),
                    produto.getIdUsuarioCadastro(),
                    produto.getIdUsuarioManutencao()
            );
        }
        return findById(produto.getCodProduto()).orElseThrow();
    }

    public void updateStatus(String codProduto, String codStatus, Long idUsuario) {
        jdbc.update(SQL_UPDATE_STATUS, codStatus, idUsuario, codProduto);
    }

    public List<Product> findAll(String codStatus) {
        if (codStatus != null && !codStatus.isBlank()) {
            return jdbc.query(SQL_SELECT_BASE + " WHERE ST_PRODUTOS = ? ORDER BY COD_PRODUTO",
                    rowMapper(), codStatus);
        }
        return jdbc.query(SQL_SELECT_BASE + " ORDER BY COD_PRODUTO", rowMapper());
    }

    // -------------------------------------------------------------------------
    // RowMapper
    // -------------------------------------------------------------------------
    private RowMapper<Product> rowMapper() {
        return (rs, rowNum) -> mapRow(rs);
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        // Usar setters diretamente pois o construtor de factory é para criação nova
        // (aqui é reconstrução a partir do banco)
        setField(p, rs);
        return p;
    }

    private void setField(Product p, ResultSet rs) throws SQLException {
        // Reflection não usada — mapeamento explícito para clareza
        try {
            var f = Product.class.getDeclaredField("codProduto");
            f.setAccessible(true);
            f.set(p, rs.getString("COD_PRODUTO"));
            f = Product.class.getDeclaredField("desProduto");
            f.setAccessible(true);
            f.set(p, rs.getString("DES_PRODUTO"));
            f = Product.class.getDeclaredField("desEmissorResponsavel");
            f.setAccessible(true);
            f.set(p, rs.getString("DES_EMISSOR_RESPONSAVEL"));
            f = Product.class.getDeclaredField("codStatus");
            f.setAccessible(true);
            f.set(p, rs.getString("ST_PRODUTOS"));
            f = Product.class.getDeclaredField("desUtilizacao");
            f.setAccessible(true);
            f.set(p, rs.getString("DES_UTILIZACAO"));
            f = Product.class.getDeclaredField("codClassificacaoPessoa");
            f.setAccessible(true);
            f.set(p, rs.getString("COD_CLASSIFICACAO_PESSOA"));
            f = Product.class.getDeclaredField("codTipoProduto");
            f.setAccessible(true);
            f.set(p, rs.getString("COD_TIPO_PRODUTO"));
            f = Product.class.getDeclaredField("codTecnologia");
            f.setAccessible(true);
            f.set(p, rs.getString("COD_TECNOLOGIA"));
            f = Product.class.getDeclaredField("codModalidade");
            f.setAccessible(true);
            f.set(p, rs.getString("COD_MODALIDADE"));
            f = Product.class.getDeclaredField("codFamilia");
            f.setAccessible(true);
            f.set(p, rs.getString("COD_FAMILIA"));
            f = Product.class.getDeclaredField("codEspecie");
            f.setAccessible(true);
            f.set(p, rs.getString("COD_ESPECIE"));
            f = Product.class.getDeclaredField("dtCadastro");
            f.setAccessible(true);
            f.set(p, fromTs(rs.getTimestamp("DT_CADASTRO")));
            f = Product.class.getDeclaredField("dtManutencao");
            f.setAccessible(true);
            f.set(p, fromTs(rs.getTimestamp("DT_MANUTENCAO")));
            f = Product.class.getDeclaredField("idUsuarioCadastro");
            f.setAccessible(true);
            f.set(p, rs.getLong("ID_USUARIO_CADASTRO"));
            f = Product.class.getDeclaredField("idUsuarioManutencao");
            f.setAccessible(true);
            f.set(p, rs.getLong("ID_USUARIO_MANUTENCAO"));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Erro ao mapear Produto do ResultSet: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao mapear Produto do ResultSet: " + e.getMessage(), e);
        }
    }

    private static Timestamp ts(LocalDateTime ldt) {
        return ldt != null ? Timestamp.valueOf(ldt) : null;
    }

    private static LocalDateTime fromTs(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }

}
