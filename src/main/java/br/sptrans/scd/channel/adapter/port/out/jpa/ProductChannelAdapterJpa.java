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

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.application.port.out.ProductChannelRepository;
import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.channel.domain.ProductChannelKey;
import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.product.domain.Product;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductChannelAdapterJpa implements ProductChannelRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<ProductChannel> rowMapper = (rs, rowNum) -> mapRow(rs);

    // -------------------------------------------------------------------------
    // SQL
    // -------------------------------------------------------------------------
    private static final String SQL_SELECT_BASE = """
            SELECT COD_CANAL, COD_PRODUTO, QTD_LIMITE_COMERCIALIZACAO, QTD_MINIMA_ESTOQUE,
                   QTD_MAXIMA_ESTOQUE, QTD_MINIMA_RESSUPRIMENTO, QTD_MAXIMA_RESSUPRIMENTO,
                   COD_ORGAO_EMISSOR, VL_FACE, ST_CANAIS_PRODUTOS, DT_CADASTRO, DT_MANUTENCAO,
                   COD_CONVENIO, TIPO_OPER_HM, FLG_CARAC, ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO
            FROM SPTRANSDBA.CANAIS_PRODUTOS
            """;

    private static final String SQL_INSERT = """
            INSERT INTO SPTRANSDBA.CANAIS_PRODUTOS (
                COD_CANAL, COD_PRODUTO, QTD_LIMITE_COMERCIALIZACAO, QTD_MINIMA_ESTOQUE,
                QTD_MAXIMA_ESTOQUE, QTD_MINIMA_RESSUPRIMENTO, QTD_MAXIMA_RESSUPRIMENTO,
                COD_ORGAO_EMISSOR, VL_FACE, ST_CANAIS_PRODUTOS, DT_CADASTRO, DT_MANUTENCAO,
                COD_CONVENIO, TIPO_OPER_HM, FLG_CARAC, ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO
            ) VALUES (?,?,?,?,?,?,?,?,?,?,SYSDATE,SYSDATE,?,?,?,?,?)
            """;

    private static final String SQL_UPDATE = """
            UPDATE SPTRANSDBA.CANAIS_PRODUTOS SET
                QTD_LIMITE_COMERCIALIZACAO = ?,
                QTD_MINIMA_ESTOQUE         = ?,
                QTD_MAXIMA_ESTOQUE         = ?,
                QTD_MINIMA_RESSUPRIMENTO   = ?,
                QTD_MAXIMA_RESSUPRIMENTO   = ?,
                COD_ORGAO_EMISSOR          = ?,
                VL_FACE                    = ?,
                ST_CANAIS_PRODUTOS         = ?,
                DT_MANUTENCAO              = SYSDATE,
                COD_CONVENIO               = ?,
                TIPO_OPER_HM               = ?,
                FLG_CARAC                  = ?,
                ID_USUARIO_MANUTENCAO      = ?
            WHERE COD_CANAL = ? AND COD_PRODUTO = ?
            """;

    private static final String SQL_DELETE = """
            DELETE FROM SPTRANSDBA.CANAIS_PRODUTOS
            WHERE COD_CANAL = ? AND COD_PRODUTO = ?
            """;

    private static final String SQL_EXISTS = """
            SELECT COUNT(1) FROM SPTRANSDBA.CANAIS_PRODUTOS
            WHERE COD_CANAL = ? AND COD_PRODUTO = ?
            """;

    // -------------------------------------------------------------------------
    // Interface
    // -------------------------------------------------------------------------

    @Override
    public Optional<ProductChannel> findById(ProductChannelKey id) {
        List<ProductChannel> result = jdbc.query(
                SQL_SELECT_BASE + "WHERE COD_CANAL = ? AND COD_PRODUTO = ?",
                rowMapper, id.getCodCanal(), id.getCodProduto());
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<ProductChannel> findAll() {
        return jdbc.query(SQL_SELECT_BASE, rowMapper);
    }

    @Override
    public List<ProductChannel> findByCodCanal(String codCanal) {
        return jdbc.query(SQL_SELECT_BASE + "WHERE COD_CANAL = ?", rowMapper, codCanal);
    }

    @Override
    public List<ProductChannel> findByCodProduto(String codProduto) {
        return jdbc.query(SQL_SELECT_BASE + "WHERE COD_PRODUTO = ?", rowMapper, codProduto);
    }

    @Override
    public ProductChannel save(ProductChannel entity) {
        ProductChannelKey key = entity.getId();
        if (existsById(key)) {
            jdbc.update(SQL_UPDATE,
                    entity.getQtdLimiteComercializacao(),
                    entity.getQtdMinimaEstoque(),
                    entity.getQtdMaximaEstoque(),
                    entity.getQtdMinimaRessuprimento(),
                    entity.getQtdMaximaRessuprimento(),
                    entity.getCodOrgaoEmissor(),
                    entity.getVlFace(),
                    entity.getCodStatus(),
                    entity.getCodConvenio(),
                    entity.getTipoOperHM(),
                    entity.getFlgCarac(),
                    entity.getIdUsuarioManutencao() != null ? entity.getIdUsuarioManutencao().getIdUsuario() : null,
                    key.getCodCanal(),
                    key.getCodProduto());
        } else {
            jdbc.update(SQL_INSERT,
                    key.getCodCanal(),
                    key.getCodProduto(),
                    entity.getQtdLimiteComercializacao(),
                    entity.getQtdMinimaEstoque(),
                    entity.getQtdMaximaEstoque(),
                    entity.getQtdMinimaRessuprimento(),
                    entity.getQtdMaximaRessuprimento(),
                    entity.getCodOrgaoEmissor(),
                    entity.getVlFace(),
                    entity.getCodStatus(),
                    entity.getCodConvenio(),
                    entity.getTipoOperHM(),
                    entity.getFlgCarac(),
                    entity.getIdUsuarioCadastro() != null ? entity.getIdUsuarioCadastro().getIdUsuario() : null,
                    entity.getIdUsuarioManutencao() != null ? entity.getIdUsuarioManutencao().getIdUsuario() : null);
        }
        return findById(key).orElseThrow();
    }

    @Override
    public void deleteById(ProductChannelKey id) {
        jdbc.update(SQL_DELETE, id.getCodCanal(), id.getCodProduto());
    }

    @Override
    public boolean existsById(ProductChannelKey id) {
        Integer count = jdbc.queryForObject(SQL_EXISTS, Integer.class,
                id.getCodCanal(), id.getCodProduto());
        return count != null && count > 0;
    }

    // -------------------------------------------------------------------------
    // RowMapper
    // -------------------------------------------------------------------------

    private ProductChannel mapRow(ResultSet rs) throws SQLException {
        try {
            String codCanal   = rs.getString("COD_CANAL");
            String codProduto = rs.getString("COD_PRODUTO");

            SalesChannel canal = new SalesChannel();
            setRef(canal, SalesChannel.class, "codCanal", codCanal);

            Product produto = new Product();
            setRef(produto, Product.class, "codProduto", codProduto);

            User usuCad = new User();
            usuCad.setIdUsuario(rs.getLong("ID_USUARIO_CADASTRO"));

            User usuMan = new User();
            usuMan.setIdUsuario(rs.getLong("ID_USUARIO_MANUTENCAO"));

            ProductChannel pc = new ProductChannel();
            pc.setId(new ProductChannelKey(codCanal, codProduto));
            pc.setQtdLimiteComercializacao(rs.getObject("QTD_LIMITE_COMERCIALIZACAO", Integer.class));
            pc.setQtdMinimaEstoque(rs.getObject("QTD_MINIMA_ESTOQUE", Integer.class));
            pc.setQtdMaximaEstoque(rs.getObject("QTD_MAXIMA_ESTOQUE", Integer.class));
            pc.setQtdMinimaRessuprimento(rs.getObject("QTD_MINIMA_RESSUPRIMENTO", Integer.class));
            pc.setQtdMaximaRessuprimento(rs.getObject("QTD_MAXIMA_RESSUPRIMENTO", Integer.class));
            pc.setCodOrgaoEmissor(rs.getObject("COD_ORGAO_EMISSOR", Integer.class));
            pc.setVlFace(rs.getObject("VL_FACE", Integer.class));
            pc.setCodStatus(rs.getString("ST_CANAIS_PRODUTOS"));
            pc.setDtCadastro(fromTs(rs.getTimestamp("DT_CADASTRO")));
            pc.setDtManutencao(fromTs(rs.getTimestamp("DT_MANUTENCAO")));
            pc.setCodConvenio(rs.getObject("COD_CONVENIO", Integer.class));
            pc.setTipoOperHM(rs.getObject("TIPO_OPER_HM", Integer.class));
            pc.setFlgCarac(rs.getString("FLG_CARAC"));
            pc.setIdUsuarioCadastro(usuCad);
            pc.setIdUsuarioManutencao(usuMan);
            pc.setCanal(canal);
            pc.setProduto(produto);
            return pc;
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
