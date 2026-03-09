package br.sptrans.scd.product.adapter.out.jpa;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.product.application.port.out.FamilyRepository;
import br.sptrans.scd.product.domain.Family;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FamilyAdapterJpa implements FamilyRepository {

    private final JdbcTemplate jdbc;

    private static final String SQL_SELECT_BASE = """
        SELECT COD_FAMILIA, DES_FAMILIA, ST_FAMILIAS,
               DT_CADASTRO, DT_MANUTENCAO,
               ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO
        FROM SPTRANSDBA.FAMILIAS
        """;

    private static final String SQL_INSERT = """
        INSERT INTO SPTRANSDBA.FAMILIAS (
            COD_FAMILIA, DES_FAMILIA, ST_FAMILIAS,
            DT_CADASTRO, DT_MANUTENCAO,
            ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO
        ) VALUES (?,?,?,SYSDATE,SYSDATE,?,?)
        """;

    private static final String SQL_UPDATE = """
        UPDATE SPTRANSDBA.FAMILIAS SET
            DES_FAMILIA           = ?,
            DT_MANUTENCAO         = SYSDATE,
            ID_USUARIO_MANUTENCAO = ?
        WHERE COD_FAMILIA = ?
        """;

    private static final String SQL_UPDATE_STATUS = """
        UPDATE SPTRANSDBA.FAMILIAS SET
            ST_FAMILIAS           = ?,
            DT_MANUTENCAO         = SYSDATE,
            ID_USUARIO_MANUTENCAO = ?
        WHERE COD_FAMILIA = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM SPTRANSDBA.FAMILIAS WHERE COD_FAMILIA = ?";

    @Override
    public Optional<Family> findById(String codFamilia) {
        List<Family> result = jdbc.query(SQL_SELECT_BASE + "WHERE COD_FAMILIA = ?", rowMapper(), codFamilia);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public boolean existsById(String codFamilia) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(1) FROM SPTRANSDBA.FAMILIAS WHERE COD_FAMILIA = ?",
                Integer.class, codFamilia);
        return count != null && count > 0;
    }

    @Override
    public List<Family> findAll(String codStatus) {
        if (codStatus != null && !codStatus.isBlank()) {
            return jdbc.query(SQL_SELECT_BASE + "WHERE ST_FAMILIAS = ? ORDER BY COD_FAMILIA",
                    rowMapper(), codStatus);
        }
        return jdbc.query(SQL_SELECT_BASE + "ORDER BY COD_FAMILIA", rowMapper());
    }

    @Override
    public Family save(Family family) {
        if (existsById(family.getCodFamilia())) {
            jdbc.update(SQL_UPDATE,
                    family.getDesFamilia(),
                    family.getIdUsuarioManutencao() != null ? family.getIdUsuarioManutencao().getIdUsuario() : null,
                    family.getCodFamilia()
            );
        } else {
            jdbc.update(SQL_INSERT,
                    family.getCodFamilia(),
                    family.getDesFamilia(),
                    family.getCodStatus(),
                    family.getIdUsuarioCadastro() != null ? family.getIdUsuarioCadastro().getIdUsuario() : null,
                    family.getIdUsuarioManutencao() != null ? family.getIdUsuarioManutencao().getIdUsuario() : null
            );
        }
        return findById(family.getCodFamilia()).orElseThrow();
    }

    @Override
    public void updateStatus(String codFamilia, String codStatus, Long idUsuario) {
        jdbc.update(SQL_UPDATE_STATUS, codStatus, idUsuario, codFamilia);
    }

    @Override
    public void deleteById(String codFamilia) {
        jdbc.update(SQL_DELETE, codFamilia);
    }

    private RowMapper<Family> rowMapper() {
        return (rs, rowNum) -> new Family(
                rs.getString("COD_FAMILIA"),
                rs.getString("DES_FAMILIA"),
                rs.getString("ST_FAMILIAS"),
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
