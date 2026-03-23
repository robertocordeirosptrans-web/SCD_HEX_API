package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.product.application.port.out.TechnologyRepository;
import br.sptrans.scd.product.domain.Technology;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TechnologyAdapterJpa implements TechnologyRepository {

    private final JdbcTemplate jdbc;

    private static final String SQL_SELECT_BASE = """
        SELECT COD_TECNOLOGIA, DES_TECNOLOGIA, ST_TECNOLOGIAS,
               DT_CADASTRO, DT_MANUTENCAO,
               ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO
        FROM SPTRANSDBA.TECNOLOGIAS
        """;

    private static final String SQL_INSERT = """
        INSERT INTO SPTRANSDBA.TECNOLOGIAS (
            COD_TECNOLOGIA, DES_TECNOLOGIA, ST_TECNOLOGIAS,
            DT_CADASTRO, DT_MANUTENCAO,
            ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO
        ) VALUES (?,?,?,SYSDATE,SYSDATE,?,?)
        """;

    private static final String SQL_UPDATE = """
        UPDATE SPTRANSDBA.TECNOLOGIAS SET
            DES_TECNOLOGIA        = ?,
            DT_MANUTENCAO         = SYSDATE,
            ID_USUARIO_MANUTENCAO = ?
        WHERE COD_TECNOLOGIA = ?
        """;

    private static final String SQL_UPDATE_STATUS = """
        UPDATE SPTRANSDBA.TECNOLOGIAS SET
            ST_TECNOLOGIAS        = ?,
            DT_MANUTENCAO         = SYSDATE,
            ID_USUARIO_MANUTENCAO = ?
        WHERE COD_TECNOLOGIA = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM SPTRANSDBA.TECNOLOGIAS WHERE COD_TECNOLOGIA = ?";

    @Override
    public Optional<Technology> findById(String codTecnologia) {
        List<Technology> result = jdbc.query(SQL_SELECT_BASE + "WHERE COD_TECNOLOGIA = ?", rowMapper(), codTecnologia);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public boolean existsById(String codTecnologia) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(1) FROM SPTRANSDBA.TECNOLOGIAS WHERE COD_TECNOLOGIA = ?",
                Integer.class, codTecnologia);
        return count != null && count > 0;
    }

    @Override
    public List<Technology> findAll(String codStatus) {
        if (codStatus != null && !codStatus.isBlank()) {
            return jdbc.query(SQL_SELECT_BASE + "WHERE ST_TECNOLOGIAS = ? ORDER BY COD_TECNOLOGIA",
                    rowMapper(), codStatus);
        }
        return jdbc.query(SQL_SELECT_BASE + "ORDER BY COD_TECNOLOGIA", rowMapper());
    }

    @Override
    public Technology save(Technology technology) {
        if (existsById(technology.getCodTecnologia())) {
            jdbc.update(SQL_UPDATE,
                    technology.getDesTecnologia(),
                    technology.getIdUsuarioManutencao() != null ? technology.getIdUsuarioManutencao().getIdUsuario() : null,
                    technology.getCodTecnologia()
            );
        } else {
            jdbc.update(SQL_INSERT,
                    technology.getCodTecnologia(),
                    technology.getDesTecnologia(),
                    technology.getCodStatus(),
                    technology.getIdUsuarioCadastro() != null ? technology.getIdUsuarioCadastro().getIdUsuario() : null,
                    technology.getIdUsuarioManutencao() != null ? technology.getIdUsuarioManutencao().getIdUsuario() : null
            );
        }
        return findById(technology.getCodTecnologia()).orElseThrow();
    }

    @Override
    public void updateStatus(String codTecnologia, String codStatus, Long idUsuario) {
        jdbc.update(SQL_UPDATE_STATUS, codStatus, idUsuario, codTecnologia);
    }

    @Override
    public void deleteById(String codTecnologia) {
        jdbc.update(SQL_DELETE, codTecnologia);
    }

    private RowMapper<Technology> rowMapper() {
        return (rs, rowNum) -> new Technology(
                rs.getString("COD_TECNOLOGIA"),
                rs.getString("DES_TECNOLOGIA"),
                rs.getString("ST_TECNOLOGIAS"),
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
