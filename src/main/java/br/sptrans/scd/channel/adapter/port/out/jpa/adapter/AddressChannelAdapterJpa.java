package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.channel.application.port.out.AddressChannelRepository;
import br.sptrans.scd.channel.domain.AddressChannel;
import br.sptrans.scd.channel.domain.SalesChannel;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AddressChannelAdapterJpa implements AddressChannelRepository {

    private final JdbcTemplate jdbc;

    private static final String SQL_SELECT_BASE = """
        SELECT COD_ENDERECO, COD_EMPREGADOR, DES_LOGRADOURO, COD_FORNECEDOR,
               COD_TIPO_ENDERECO, COD_CEP, DES_BAIRRO, DES_CIDADE, DES_UF,
               NUM_DDD, NUM_FONE, NUM_FAX, DES_OBS,
               DT_CADASTRO, DT_MANUTENCAO, ST_ENDERECOS,
               DT_VALIDADE, COD_SEQ, DES_NUMERO,
               ID_USUARIO_MANUTENCAO, ID_USUARIO_CADASTRO,
               COD_CANAL
        FROM SPTRANSDBA.ENTIDADE_ENDERECO
        """;

    private static final String SQL_INSERT = """
        INSERT INTO SPTRANSDBA.ENTIDADE_ENDERECO (
            COD_ENDERECO, COD_EMPREGADOR, DES_LOGRADOURO, COD_FORNECEDOR,
            COD_TIPO_ENDERECO, COD_CEP, DES_BAIRRO, DES_CIDADE, DES_UF,
            NUM_DDD, NUM_FONE, NUM_FAX, DES_OBS,
            DT_CADASTRO, DT_MANUTENCAO, ST_ENDERECOS,
            DES_NUMERO, ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO, COD_CANAL
        ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE,SYSDATE,?,?,?,?,?)
        """;

    private static final String SQL_UPDATE = """
        UPDATE SPTRANSDBA.ENTIDADE_ENDERECO SET
            COD_EMPREGADOR        = ?,
            DES_LOGRADOURO        = ?,
            COD_FORNECEDOR        = ?,
            COD_TIPO_ENDERECO     = ?,
            COD_CEP               = ?,
            DES_BAIRRO            = ?,
            DES_CIDADE            = ?,
            DES_UF                = ?,
            NUM_DDD               = ?,
            NUM_FONE              = ?,
            NUM_FAX               = ?,
            DES_OBS               = ?,
            ST_ENDERECOS          = ?,
            DES_NUMERO            = ?,
            COD_CANAL             = ?,
            DT_MANUTENCAO         = SYSDATE,
            ID_USUARIO_MANUTENCAO = ?
        WHERE COD_ENDERECO = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM SPTRANSDBA.ENTIDADE_ENDERECO WHERE COD_ENDERECO = ?";

    @Override
    public Optional<AddressChannel> findById(String codEndereco) {
        List<AddressChannel> result = jdbc.query(
                SQL_SELECT_BASE + "WHERE COD_ENDERECO = ?", rowMapper(), codEndereco);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public boolean existsById(String codEndereco) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(1) FROM SPTRANSDBA.ENTIDADE_ENDERECO WHERE COD_ENDERECO = ?",
                Integer.class, codEndereco);
        return count != null && count > 0;
    }

    @Override
    public List<AddressChannel> findAllByCanal(String codCanal) {
        if (codCanal != null && !codCanal.isBlank()) {
            return jdbc.query(SQL_SELECT_BASE + "WHERE COD_CANAL = ? ORDER BY COD_ENDERECO",
                    rowMapper(), codCanal);
        }
        return jdbc.query(SQL_SELECT_BASE + "ORDER BY COD_ENDERECO", rowMapper());
    }

    @Override
    public AddressChannel save(AddressChannel ac) {
        if (existsById(ac.getCodEndereco())) {
            jdbc.update(SQL_UPDATE,
                    ac.getCodEmpregador(),
                    ac.getDesLogradouro(),
                    ac.getCodFornecedor(),
                    ac.getCodTipoEndereco(),
                    ac.getCodCEP(),
                    ac.getDesBairro(),
                    ac.getDesCidade(),
                    ac.getDesUF(),
                    ac.getNumDDD(),
                    ac.getNumFone(),
                    ac.getNumFax(),
                    ac.getDesObs(),
                    ac.getStEnderecos(),
                    ac.getDesNumero(),
                    ac.getCodCanal() != null ? ac.getCodCanal().getCodCanal() : null,
                    ac.getIdUsuarioManutencao() != null ? ac.getIdUsuarioManutencao().getIdUsuario() : null,
                    ac.getCodEndereco()
            );
        } else {
            jdbc.update(SQL_INSERT,
                    ac.getCodEndereco(),
                    ac.getCodEmpregador(),
                    ac.getDesLogradouro(),
                    ac.getCodFornecedor(),
                    ac.getCodTipoEndereco(),
                    ac.getCodCEP(),
                    ac.getDesBairro(),
                    ac.getDesCidade(),
                    ac.getDesUF(),
                    ac.getNumDDD(),
                    ac.getNumFone(),
                    ac.getNumFax(),
                    ac.getDesObs(),
                    ac.getStEnderecos(),
                    ac.getDesNumero(),
                    ac.getIdUsuarioCadastro() != null ? ac.getIdUsuarioCadastro().getIdUsuario() : null,
                    ac.getIdUsuarioManutencao() != null ? ac.getIdUsuarioManutencao().getIdUsuario() : null,
                    ac.getCodCanal() != null ? ac.getCodCanal().getCodCanal() : null
            );
        }
        return findById(ac.getCodEndereco()).orElseThrow();
    }

    @Override
    public void deleteById(String codEndereco) {
        jdbc.update(SQL_DELETE, codEndereco);
    }

    private RowMapper<AddressChannel> rowMapper() {
        return (rs, rowNum) -> new AddressChannel(
                rs.getString("COD_ENDERECO"),
                rs.getString("COD_EMPREGADOR"),
                rs.getString("DES_LOGRADOURO"),
                rs.getString("COD_FORNECEDOR"),
                rs.getString("COD_TIPO_ENDERECO"),
                rs.getString("COD_CEP"),
                rs.getString("DES_BAIRRO"),
                rs.getString("DES_CIDADE"),
                rs.getString("DES_UF"),
                rs.getObject("NUM_DDD", Integer.class),
                rs.getObject("NUM_FONE", Integer.class),
                rs.getObject("NUM_FAX", Integer.class),
                rs.getString("DES_OBS"),
                toLocalDateTime(rs.getTimestamp("DT_CADASTRO")),
                toLocalDateTime(rs.getTimestamp("DT_MANUTENCAO")),
                rs.getString("ST_ENDERECOS"),
                toLocalDateTime(rs.getTimestamp("DT_VALIDADE")),
                rs.getObject("COD_SEQ", Integer.class),
                rs.getString("DES_NUMERO"),
                null,
                null,
                rs.getString("COD_CANAL") != null
                        ? new SalesChannel(rs.getString("COD_CANAL"), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
                        : null
        );
    }

    private LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }
}
