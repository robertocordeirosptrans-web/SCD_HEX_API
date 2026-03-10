package br.sptrans.scd.channel.adapter.port.out.jpa;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.channel.application.port.out.ContactChannelRepository;
import br.sptrans.scd.channel.domain.ContactChannel;
import br.sptrans.scd.channel.domain.SalesChannel;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ContactChannelAdapterJpa implements ContactChannelRepository {

    private final JdbcTemplate jdbc;

    private static final String SQL_SELECT_BASE = """
        SELECT COD_CONTATO, COD_FORNECEDOR, COD_EMPREGADOR,
               DES_CONTATO, DES_EMAIL_CONTATO,
               NUM_DDD, NUM_FONE, NUM_FONE_RAMAL, NUM_FAX, NUM_FAX_RAMAL,
               ST_ENTIDADE_CONTATO, DES_COMENTARIOS,
               COD_TIPO_DOCUMENTO, COD_DOCUMENTO,
               DT_CADASTRO, DT_MANUTENCAO,
               ID_USUARIO_MANUTENCAO, ID_USUARIO_CADASTRO,
               COD_CANAL
        FROM SPTRANSDBA.ENTIDADE_CONTATO
        """;

    private static final String SQL_INSERT = """
        INSERT INTO SPTRANSDBA.ENTIDADE_CONTATO (
            COD_CONTATO, COD_FORNECEDOR, COD_EMPREGADOR,
            DES_CONTATO, DES_EMAIL_CONTATO,
            NUM_DDD, NUM_FONE, NUM_FONE_RAMAL, NUM_FAX, NUM_FAX_RAMAL,
            ST_ENTIDADE_CONTATO, DES_COMENTARIOS,
            COD_TIPO_DOCUMENTO, COD_DOCUMENTO,
            DT_CADASTRO, DT_MANUTENCAO,
            ID_USUARIO_MANUTENCAO, ID_USUARIO_CADASTRO,
            COD_CANAL
        ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE,SYSDATE,?,?,?)
        """;

    private static final String SQL_UPDATE = """
        UPDATE SPTRANSDBA.ENTIDADE_CONTATO SET
            COD_FORNECEDOR        = ?,
            COD_EMPREGADOR        = ?,
            DES_CONTATO           = ?,
            DES_EMAIL_CONTATO     = ?,
            NUM_DDD               = ?,
            NUM_FONE              = ?,
            NUM_FONE_RAMAL        = ?,
            NUM_FAX               = ?,
            NUM_FAX_RAMAL         = ?,
            ST_ENTIDADE_CONTATO   = ?,
            DES_COMENTARIOS       = ?,
            COD_TIPO_DOCUMENTO    = ?,
            COD_DOCUMENTO         = ?,
            COD_CANAL             = ?,
            DT_MANUTENCAO         = SYSDATE,
            ID_USUARIO_MANUTENCAO = ?
        WHERE COD_CONTATO = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM SPTRANSDBA.ENTIDADE_CONTATO WHERE COD_CONTATO = ?";

    @Override
    public Optional<ContactChannel> findById(String codContato) {
        List<ContactChannel> result = jdbc.query(
                SQL_SELECT_BASE + "WHERE COD_CONTATO = ?", rowMapper(), codContato);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public boolean existsById(String codContato) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(1) FROM SPTRANSDBA.ENTIDADE_CONTATO WHERE COD_CONTATO = ?",
                Integer.class, codContato);
        return count != null && count > 0;
    }

    @Override
    public List<ContactChannel> findAllByCanal(String codCanal) {
        if (codCanal != null && !codCanal.isBlank()) {
            return jdbc.query(SQL_SELECT_BASE + "WHERE COD_CANAL = ? ORDER BY COD_CONTATO",
                    rowMapper(), codCanal);
        }
        return jdbc.query(SQL_SELECT_BASE + "ORDER BY COD_CONTATO", rowMapper());
    }

    @Override
    public ContactChannel save(ContactChannel cc) {
        if (existsById(cc.getCodContato())) {
            jdbc.update(SQL_UPDATE,
                    cc.getCodFornecedor(),
                    cc.getCodEmpregador(),
                    cc.getDesContato(),
                    cc.getDesEmailContato(),
                    cc.getNumDDD(),
                    cc.getNumFone(),
                    cc.getNumFoneRamal(),
                    cc.getNumFax(),
                    cc.getNumFaxRamal(),
                    cc.getStEntidadeContato(),
                    cc.getDesComentarios(),
                    cc.getCodTipoDocumento(),
                    cc.getCodDocumento(),
                    cc.getCodCanal() != null ? cc.getCodCanal().getCodCanal() : null,
                    cc.getIdUsuarioManutencao() != null ? cc.getIdUsuarioManutencao().getIdUsuario() : null,
                    cc.getCodContato()
            );
        } else {
            jdbc.update(SQL_INSERT,
                    cc.getCodContato(),
                    cc.getCodFornecedor(),
                    cc.getCodEmpregador(),
                    cc.getDesContato(),
                    cc.getDesEmailContato(),
                    cc.getNumDDD(),
                    cc.getNumFone(),
                    cc.getNumFoneRamal(),
                    cc.getNumFax(),
                    cc.getNumFaxRamal(),
                    cc.getStEntidadeContato(),
                    cc.getDesComentarios(),
                    cc.getCodTipoDocumento(),
                    cc.getCodDocumento(),
                    cc.getIdUsuarioManutencao() != null ? cc.getIdUsuarioManutencao().getIdUsuario() : null,
                    cc.getIdUsuarioCadastro() != null ? cc.getIdUsuarioCadastro().getIdUsuario() : null,
                    cc.getCodCanal() != null ? cc.getCodCanal().getCodCanal() : null
            );
        }
        return findById(cc.getCodContato()).orElseThrow();
    }

    @Override
    public void deleteById(String codContato) {
        jdbc.update(SQL_DELETE, codContato);
    }

    private RowMapper<ContactChannel> rowMapper() {
        return (rs, rowNum) -> new ContactChannel(
                rs.getString("COD_CONTATO"),
                rs.getString("COD_FORNECEDOR"),
                rs.getString("COD_EMPREGADOR"),
                rs.getString("DES_CONTATO"),
                rs.getString("DES_EMAIL_CONTATO"),
                rs.getObject("NUM_DDD", Integer.class),
                rs.getObject("NUM_FONE", Integer.class),
                rs.getObject("NUM_FONE_RAMAL", Integer.class),
                rs.getObject("NUM_FAX", Integer.class),
                rs.getObject("NUM_FAX_RAMAL", Integer.class),
                rs.getString("ST_ENTIDADE_CONTATO"),
                rs.getString("DES_COMENTARIOS"),
                rs.getString("COD_TIPO_DOCUMENTO"),
                rs.getString("COD_DOCUMENTO"),
                toLocalDateTime(rs.getTimestamp("DT_CADASTRO")),
                toLocalDateTime(rs.getTimestamp("DT_MANUTENCAO")),
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
