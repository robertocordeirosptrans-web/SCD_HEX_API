package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.channel.application.port.out.TypesActivityRepository;
import br.sptrans.scd.channel.domain.TypesActivity;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TypesActivityAdapterJpa implements TypesActivityRepository {

    private final JdbcTemplate jdbc;

    private static final String SQL_SELECT_BASE = """
        SELECT COD_ATIVIDADE, DES_ATIVIDADE, ST_ATIVIDADE, DT_CADASTRO, DT_MANUTENCAO
        FROM TIPOS_ATIVIDADE
        """;

    private static final String SQL_INSERT = """
        INSERT INTO TIPOS_ATIVIDADE (COD_ATIVIDADE, DES_ATIVIDADE, ST_ATIVIDADE, DT_CADASTRO, DT_MANUTENCAO)
        VALUES (?, ?, 'I', SYSDATE, SYSDATE)
        """;

    private static final String SQL_UPDATE = """
        UPDATE TIPOS_ATIVIDADE SET
            DES_ATIVIDADE = ?,
            DT_MANUTENCAO = SYSDATE
        WHERE COD_ATIVIDADE = ?
        """;

    private static final String SQL_UPDATE_STATUS = """
        UPDATE TIPOS_ATIVIDADE SET
            ST_ATIVIDADE  = ?,
            DT_MANUTENCAO = SYSDATE
        WHERE COD_ATIVIDADE = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM TIPOS_ATIVIDADE WHERE COD_ATIVIDADE = ?";

    @Override
    public Optional<TypesActivity> findById(String codAtividade) {
        List<TypesActivity> result = jdbc.query(
                SQL_SELECT_BASE + "WHERE COD_ATIVIDADE = ?", rowMapper(), codAtividade);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public boolean existsById(String codAtividade) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(1) FROM TIPOS_ATIVIDADE WHERE COD_ATIVIDADE = ?",
                Integer.class, codAtividade);
        return count != null && count > 0;
    }

    @Override
    public List<TypesActivity> findAll(String codStatus) {
        if (codStatus != null && !codStatus.isBlank()) {
            return jdbc.query(SQL_SELECT_BASE + "WHERE ST_ATIVIDADE = ? ORDER BY COD_ATIVIDADE",
                    rowMapper(), codStatus);
        }
        return jdbc.query(SQL_SELECT_BASE + "ORDER BY COD_ATIVIDADE", rowMapper());
    }

    @Override
    public TypesActivity save(TypesActivity typesActivity) {
        if (existsById(typesActivity.getCodAtividade())) {
            jdbc.update(SQL_UPDATE,
                    typesActivity.getDesAtividade(),
                    typesActivity.getCodAtividade());
        } else {
            jdbc.update(SQL_INSERT,
                    typesActivity.getCodAtividade(),
                    typesActivity.getDesAtividade());
        }
        return findById(typesActivity.getCodAtividade()).orElseThrow();
    }

    @Override
    public void updateStatus(String codAtividade, String codStatus) {
        jdbc.update(SQL_UPDATE_STATUS, codStatus, codAtividade);
    }

    @Override
    public void deleteById(String codAtividade) {
        jdbc.update(SQL_DELETE, codAtividade);
    }

    private RowMapper<TypesActivity> rowMapper() {
        return (rs, rowNum) -> new TypesActivity(
                rs.getString("COD_ATIVIDADE"),
                rs.getString("DES_ATIVIDADE"),
                rs.getString("ST_ATIVIDADE"),
                rs.getString("DT_CADASTRO"),
                rs.getString("DT_MANUTENCAO")
        );
    }
}
