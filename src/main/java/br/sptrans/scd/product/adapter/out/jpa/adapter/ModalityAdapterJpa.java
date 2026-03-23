package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.product.application.port.out.ModalityRepository;
import br.sptrans.scd.product.domain.Modality;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ModalityAdapterJpa implements ModalityRepository {

    private final JdbcTemplate jdbc;

    private static final String SQL_SELECT_BASE = """
        SELECT COD_MODALIDADE, DES_MODALIDADE, ST_MODALIDADES,
               DT_CADASTRO, DT_MANUTENCAO,
               ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO
        FROM SPTRANSDBA.MODALIDADES
        """;

    private static final String SQL_INSERT = """
        INSERT INTO SPTRANSDBA.MODALIDADES (
            COD_MODALIDADE, DES_MODALIDADE, ST_MODALIDADES,
            DT_CADASTRO, DT_MANUTENCAO,
            ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO
        ) VALUES (?,?,?,SYSDATE,SYSDATE,?,?)
        """;

    private static final String SQL_UPDATE = """
        UPDATE SPTRANSDBA.MODALIDADES SET
            DES_MODALIDADE        = ?,
            DT_MANUTENCAO         = SYSDATE,
            ID_USUARIO_MANUTENCAO = ?
        WHERE COD_MODALIDADE = ?
        """;

    private static final String SQL_UPDATE_STATUS = """
        UPDATE SPTRANSDBA.MODALIDADES SET
            ST_MODALIDADES        = ?,
            DT_MANUTENCAO         = SYSDATE,
            ID_USUARIO_MANUTENCAO = ?
        WHERE COD_MODALIDADE = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM SPTRANSDBA.MODALIDADES WHERE COD_MODALIDADE = ?";

    @Override
    public Optional<Modality> findById(String codModalidade) {
        List<Modality> result = jdbc.query(SQL_SELECT_BASE + "WHERE COD_MODALIDADE = ?", rowMapper(), codModalidade);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public boolean existsById(String codModalidade) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(1) FROM SPTRANSDBA.MODALIDADES WHERE COD_MODALIDADE = ?",
                Integer.class, codModalidade);
        return count != null && count > 0;
    }

    @Override
    public List<Modality> findAll(String codStatus) {
        if (codStatus != null && !codStatus.isBlank()) {
            return jdbc.query(SQL_SELECT_BASE + "WHERE ST_MODALIDADES = ? ORDER BY COD_MODALIDADE",
                    rowMapper(), codStatus);
        }
        return jdbc.query(SQL_SELECT_BASE + "ORDER BY COD_MODALIDADE", rowMapper());
    }

    @Override
    public Modality save(Modality modality) {
        if (existsById(modality.getCodModalidade())) {
            jdbc.update(SQL_UPDATE,
                    modality.getDesModalidade(),
                    modality.getIdUsuarioManutencao() != null ? modality.getIdUsuarioManutencao().getIdUsuario() : null,
                    modality.getCodModalidade()
            );
        } else {
            jdbc.update(SQL_INSERT,
                    modality.getCodModalidade(),
                    modality.getDesModalidade(),
                    modality.getCodStatus(),
                    modality.getIdUsuarioCadastro() != null ? modality.getIdUsuarioCadastro().getIdUsuario() : null,
                    modality.getIdUsuarioManutencao() != null ? modality.getIdUsuarioManutencao().getIdUsuario() : null
            );
        }
        return findById(modality.getCodModalidade()).orElseThrow();
    }

    @Override
    public void updateStatus(String codModalidade, String codStatus, Long idUsuario) {
        jdbc.update(SQL_UPDATE_STATUS, codStatus, idUsuario, codModalidade);
    }

    @Override
    public void deleteById(String codModalidade) {
        jdbc.update(SQL_DELETE, codModalidade);
    }

    private RowMapper<Modality> rowMapper() {
        return (rs, rowNum) -> new Modality(
                rs.getString("COD_MODALIDADE"),
                rs.getString("DES_MODALIDADE"),
                rs.getString("ST_MODALIDADES"),
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
