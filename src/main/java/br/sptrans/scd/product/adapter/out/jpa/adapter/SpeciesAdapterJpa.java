package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.product.application.port.out.SpeciesRepository;
import br.sptrans.scd.product.domain.Species;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SpeciesAdapterJpa implements SpeciesRepository {

    private final JdbcTemplate jdbc;

    private static final String SQL_SELECT_BASE = """
        SELECT COD_ESPECIE, DES_ESPECIE, ST_ESPECIES,
               DT_CADASTRO, DT_MANUTENCAO,
               ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO
        FROM SPTRANSDBA.ESPECIES
        """;

    private static final String SQL_INSERT = """
        INSERT INTO SPTRANSDBA.ESPECIES (
            COD_ESPECIE, DES_ESPECIE, ST_ESPECIES,
            DT_CADASTRO, DT_MANUTENCAO,
            ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO
        ) VALUES (?,?,?,SYSDATE,SYSDATE,?,?)
        """;

    private static final String SQL_UPDATE = """
        UPDATE SPTRANSDBA.ESPECIES SET
            DES_ESPECIE           = ?,
            DT_MANUTENCAO         = SYSDATE,
            ID_USUARIO_MANUTENCAO = ?
        WHERE COD_ESPECIE = ?
        """;

    private static final String SQL_UPDATE_STATUS = """
        UPDATE SPTRANSDBA.ESPECIES SET
            ST_ESPECIES           = ?,
            DT_MANUTENCAO         = SYSDATE,
            ID_USUARIO_MANUTENCAO = ?
        WHERE COD_ESPECIE = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM SPTRANSDBA.ESPECIES WHERE COD_ESPECIE = ?";

    @Override
    public Optional<Species> findById(String codEspecie) {
        List<Species> result = jdbc.query(SQL_SELECT_BASE + "WHERE COD_ESPECIE = ?", rowMapper(), codEspecie);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public boolean existsById(String codEspecie) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(1) FROM SPTRANSDBA.ESPECIES WHERE COD_ESPECIE = ?",
                Integer.class, codEspecie);
        return count != null && count > 0;
    }

    @Override
    public List<Species> findAll(String codStatus) {
        if (codStatus != null && !codStatus.isBlank()) {
            return jdbc.query(SQL_SELECT_BASE + "WHERE ST_ESPECIES = ? ORDER BY COD_ESPECIE",
                    rowMapper(), codStatus);
        }
        return jdbc.query(SQL_SELECT_BASE + "ORDER BY COD_ESPECIE", rowMapper());
    }

    @Override
    public Species save(Species species) {
        if (existsById(species.getCodEspecie())) {
            jdbc.update(SQL_UPDATE,
                    species.getDesEspecie(),
                    species.getIdUsuarioManutencao() != null ? species.getIdUsuarioManutencao().getIdUsuario() : null,
                    species.getCodEspecie()
            );
        } else {
            jdbc.update(SQL_INSERT,
                    species.getCodEspecie(),
                    species.getDesEspecie(),
                    species.getCodStatus(),
                    species.getIdUsuarioCadastro() != null ? species.getIdUsuarioCadastro().getIdUsuario() : null,
                    species.getIdUsuarioManutencao() != null ? species.getIdUsuarioManutencao().getIdUsuario() : null
            );
        }
        return findById(species.getCodEspecie()).orElseThrow();
    }

    @Override
    public void updateStatus(String codEspecie, String codStatus, Long idUsuario) {
        jdbc.update(SQL_UPDATE_STATUS, codStatus, idUsuario, codEspecie);
    }

    @Override
    public void deleteById(String codEspecie) {
        jdbc.update(SQL_DELETE, codEspecie);
    }

    private RowMapper<Species> rowMapper() {
        return (rs, rowNum) -> new Species(
                rs.getString("COD_ESPECIE"),
                rs.getString("DES_ESPECIE"),
                rs.getString("ST_ESPECIES"),
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
