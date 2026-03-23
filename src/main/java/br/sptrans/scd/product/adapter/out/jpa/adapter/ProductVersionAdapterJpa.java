package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.product.application.port.out.ProductVersionRepository;
import br.sptrans.scd.product.domain.ProductVersion;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductVersionAdapterJpa implements ProductVersionRepository {

    private final JdbcTemplate jdbc;

    private static final String SQL_COLS = """
        COD_VERSAO, COD_PRODUTO, DES_PROD_VERSAO, ST_PRODUTOS_VERSOES,
        DT_VALIDADE, DT_VIDA_INICIO, DT_VIDA_FIM, DT_LIBERACAO, DT_LANCAMENTO,
        DT_VENDA_INICIO, DT_VENDA_FIM, DT_USO_INICIO, DT_USO_FIM,
        DT_TROCA_INICIO, DT_TROCA_FIM,
        FLG_BLOQ_FABRICACAO, FLG_BLOQ_VENDA, FLG_BLOQ_DISTRIBUICAO,
        FLG_BLOQ_TROCA, FLG_BLOQ_AQUISICAO, FLG_BLOQ_PEDIDO, FLG_BLOQ_DEVOLUCAO,
        DT_CADASTRO, DT_MANUTENCAO,
        ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO
        """;

    private static final String SQL_SELECT_BASE
            = "SELECT " + SQL_COLS + " FROM SPTRANSDBA.PRODUTOS_VERSOES ";

    private static final String SQL_UPDATE_STATUS = """
        UPDATE SPTRANSDBA.PRODUTOS_VERSOES SET
            ST_PRODUTOS_VERSOES   = ?,
            DT_MANUTENCAO         = SYSDATE,
            ID_USUARIO_MANUTENCAO = ?
        WHERE COD_PRODUTO = ?
        """;

    private static final String SQL_INSERT = """
        INSERT INTO SPTRANSDBA.PRODUTOS_VERSOES (
            COD_VERSAO, COD_PRODUTO, DES_PROD_VERSAO, ST_PRODUTOS_VERSOES,
            DT_VALIDADE, DT_VIDA_INICIO, DT_VIDA_FIM, DT_LIBERACAO, DT_LANCAMENTO,
            DT_VENDA_INICIO, DT_VENDA_FIM, DT_USO_INICIO, DT_USO_FIM,
            DT_TROCA_INICIO, DT_TROCA_FIM,
            FLG_BLOQ_FABRICACAO, FLG_BLOQ_VENDA, FLG_BLOQ_DISTRIBUICAO,
            FLG_BLOQ_TROCA, FLG_BLOQ_AQUISICAO, FLG_BLOQ_PEDIDO, FLG_BLOQ_DEVOLUCAO,
            DT_CADASTRO, DT_MANUTENCAO,
            ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO
        ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE,SYSDATE,?,?)
        """;

    public Optional<ProductVersion> findById(String codVersao) {
        List<ProductVersion> r = jdbc.query(
                SQL_SELECT_BASE + "WHERE COD_VERSAO = ?", rowMapper(), codVersao);
        return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
    }

    public Optional<ProductVersion> findLastVersion(String codProduto) {
        // Última versão = maior número de versão (COD_VERSAO DESC)
        List<ProductVersion> r = jdbc.query(
                SQL_SELECT_BASE + """
            WHERE COD_PRODUTO = ?
            ORDER BY COD_VERSAO DESC
            FETCH FIRST 1 ROWS ONLY
            """, rowMapper(), codProduto);
        return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
    }

    public boolean existsByProduct(String codProduto) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(1) FROM SPTRANSDBA.PRODUTOS_VERSOES WHERE COD_PRODUTO = ?",
                Integer.class, codProduto);
        return count != null && count > 0;
    }

    public ProductVersion save(ProductVersion v) {
        boolean existe = findById(v.getCodVersao()).isPresent();
        if (existe) {
            jdbc.update("""
                UPDATE SPTRANSDBA.PRODUTOS_VERSOES SET
                    DES_PROD_VERSAO          = ?,
                    ST_PRODUTOS_VERSOES      = ?,
                    DT_VALIDADE              = ?,
                    DT_VIDA_INICIO           = ?,
                    DT_VIDA_FIM              = ?,
                    DT_LIBERACAO             = ?,
                    DT_LANCAMENTO            = ?,
                    DT_VENDA_INICIO          = ?,
                    DT_VENDA_FIM             = ?,
                    DT_USO_INICIO            = ?,
                    DT_USO_FIM               = ?,
                    DT_TROCA_INICIO          = ?,
                    DT_TROCA_FIM             = ?,
                 DT_MANUTENCAO            = SYSDATE,
                    ID_USUARIO_MANUTENCAO    = ?
                WHERE COD_VERSAO = ?
                """,
                    v.getDesProdutoVersoes(), v.getStProdutosVersoes(),
                    ts(v.getDtValidade()), ts(v.getDtVidaInicio()), ts(v.getDtVidaFim()),
                    ts(v.getDtLiberacao()), ts(v.getDtLancamento()),
                    ts(v.getDtVendaInicio()), ts(v.getDtVendaFim()),
                    ts(v.getDtUsoIni()), ts(v.getDtUsoFim()),
                    ts(v.getDtTrocaIni()), ts(v.getDtTrocaFim()),
                    v.getIdUsuarioManutencao(),
                    v.getCodVersao()
            );
        } else {
            jdbc.update(SQL_INSERT,
                    v.getCodVersao(), v.getCodProduto(), v.getDesProdutoVersoes(),
                    v.getStProdutosVersoes(),
                    ts(v.getDtValidade()), ts(v.getDtVidaInicio()), ts(v.getDtVidaFim()),
                    ts(v.getDtLiberacao()), ts(v.getDtLancamento()),
                    ts(v.getDtVendaInicio()), ts(v.getDtVendaFim()),
                    ts(v.getDtUsoIni()), ts(v.getDtUsoFim()),
                    ts(v.getDtTrocaIni()), ts(v.getDtTrocaFim()),
                    v.getFlgBloqFabricacao(), v.getFlgBloqVenda(), v.getFlgBloqDistribuicao(),
                    v.getFlgBloqTroca(), v.getFlgBloqAquisicao(),
                    v.getFlgBloqPedido(), v.getFlgBloqDevolucao(),
                    v.getIdUsuarioCadastro(), v.getIdUsuarioManutencao()
            );
        }
        return findById(v.getCodVersao()).orElseThrow();
    }

    public List<ProductVersion> findByProduct(String codProduto) {
        return jdbc.query(
                SQL_SELECT_BASE + "WHERE COD_PRODUTO = ? ORDER BY COD_VERSAO ASC",
                rowMapper(), codProduto);
    }

    public void updateStatus(String codProduto, String codStatus, Long idUsuario) {
        jdbc.update(SQL_UPDATE_STATUS, codStatus, idUsuario, codProduto);
    }


    // -------------------------------------------------------------------------
    // RowMapper
    // -------------------------------------------------------------------------
    private RowMapper<ProductVersion> rowMapper() {
        return (rs, rowNum) -> {
            ProductVersion v = new ProductVersion();
            v.setCodVersao(rs.getString("COD_VERSAO"));
            // COD_PRODUTO via reflection (campo sem setter público para manter imutabilidade)
            try {
                var f = ProductVersion.class.getDeclaredField("codProduto");
                f.setAccessible(true);
                f.set(v, rs.getString("COD_PRODUTO"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            v.setDesProdutoVersoes(rs.getString("DES_PROD_VERSAO"));
            v.setStProdutosVersoes(rs.getString("ST_PRODUTOS_VERSOES"));
            v.setDtValidade(fromTs(rs.getTimestamp("DT_VALIDADE")));
            v.setDtVidaInicio(fromTs(rs.getTimestamp("DT_VIDA_INICIO")));
            v.setDtVidaFim(fromTs(rs.getTimestamp("DT_VIDA_FIM")));
            v.setDtLiberacao(fromTs(rs.getTimestamp("DT_LIBERACAO")));
            v.setDtLancamento(fromTs(rs.getTimestamp("DT_LANCAMENTO")));
            v.setDtVendaInicio(fromTs(rs.getTimestamp("DT_VENDA_INICIO")));
            v.setDtVendaFim(fromTs(rs.getTimestamp("DT_VENDA_FIM")));
            v.setDtUsoIni(fromTs(rs.getTimestamp("DT_USO_INICIO")));
            v.setDtUsoFim(fromTs(rs.getTimestamp("DT_USO_FIM")));
            v.setDtTrocaIni(fromTs(rs.getTimestamp("DT_TROCA_INICIO")));
            v.setDtTrocaFim(fromTs(rs.getTimestamp("DT_TROCA_FIM")));
            v.setFlgBloqFabricacao(rs.getString("FLG_BLOQ_FABRICACAO"));
            v.setFlgBloqVenda(rs.getString("FLG_BLOQ_VENDA"));
            v.setFlgBloqDistribuicao(rs.getString("FLG_BLOQ_DISTRIBUICAO"));
            v.setFlgBloqTroca(rs.getString("FLG_BLOQ_TROCA"));
            v.setFlgBloqAquisicao(rs.getString("FLG_BLOQ_AQUISICAO"));
            v.setFlgBloqPedido(rs.getString("FLG_BLOQ_PEDIDO"));
            v.setFlgBloqDevolucao(rs.getString("FLG_BLOQ_DEVOLUCAO"));
            v.setDtCadastro(fromTs(rs.getTimestamp("DT_CADASTRO")));
            v.setDtManutencao(fromTs(rs.getTimestamp("DT_MANUTENCAO")));
            return v;
        };
    }

    private static Timestamp ts(LocalDateTime ldt) {
        return ldt != null ? Timestamp.valueOf(ldt) : null;
    }

    private static LocalDateTime fromTs(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }

}
