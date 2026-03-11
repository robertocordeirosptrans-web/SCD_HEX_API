package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.domain.ClassificationPerson;
import br.sptrans.scd.channel.application.port.out.SalesChannelRepository;
import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.channel.domain.TypesActivity;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SalesChannelAdapterJpa implements SalesChannelRepository {

    private final JdbcTemplate jdbc;

    private static final String SQL_SELECT_BASE = """
        SELECT COD_CANAL, COD_DOCUMENTO, COD_CANAL_SUPERIOR, DES_CANAL,
               COD_TIPO_DOCUMENTO, DT_MANUTENCAO, DES_RAZAO_SOCIAL, ST_CANAIS,
               DES_NOME_FANTASIA, DT_CADASTRO, VL_CAUCAO,
               DT_INICIO_CAUCAO, DT_FIM_CAUCAO, SEQ_NIVEL,
               FLG_CRITICA_NUMLOTE, FLG_LIMITE_DIAS,
               FLG_PROCESSAMENTO_AUTOMATICO, FLG_PROCESSAMENTO_PARCIAL,
               FLG_SALDO_DEVEDOR, NUM_MINUTO_INI_LIB_RECARGA,
               NUM_MINUTO_FIM_LIB_RECARGA, FLG_EMITE_RECIBO_PEDIDO,
               FLG_SUPERCANAL, FLG_PAGTOFUTURO,
               COD_CLASSIFICACAO_PESSOA, COD_ATIVIDADE,
               ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO
        FROM SPTRANSDBA.CANAIS
        """;

    private static final String SQL_INSERT = """
        INSERT INTO SPTRANSDBA.CANAIS (
            COD_CANAL, COD_DOCUMENTO, COD_CANAL_SUPERIOR, DES_CANAL,
            COD_TIPO_DOCUMENTO, DT_MANUTENCAO, DES_RAZAO_SOCIAL, ST_CANAIS,
            DES_NOME_FANTASIA, DT_CADASTRO, VL_CAUCAO,
            DT_INICIO_CAUCAO, DT_FIM_CAUCAO, SEQ_NIVEL,
            FLG_CRITICA_NUMLOTE, FLG_LIMITE_DIAS,
            FLG_PROCESSAMENTO_AUTOMATICO, FLG_PROCESSAMENTO_PARCIAL,
            FLG_SALDO_DEVEDOR, NUM_MINUTO_INI_LIB_RECARGA,
            NUM_MINUTO_FIM_LIB_RECARGA, FLG_EMITE_RECIBO_PEDIDO,
            FLG_SUPERCANAL, FLG_PAGTOFUTURO,
            COD_CLASSIFICACAO_PESSOA, COD_ATIVIDADE,
            ID_USUARIO_CADASTRO, ID_USUARIO_MANUTENCAO
        ) VALUES (?,?,?,?,?,SYSDATE,?,?,?,SYSDATE,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
        """;

    private static final String SQL_UPDATE = """
        UPDATE SPTRANSDBA.CANAIS SET
            COD_CANAL_SUPERIOR           = ?,
            DES_CANAL                    = ?,
            DES_RAZAO_SOCIAL             = ?,
            DES_NOME_FANTASIA            = ?,
            VL_CAUCAO                    = ?,
            DT_INICIO_CAUCAO             = ?,
            DT_FIM_CAUCAO                = ?,
            SEQ_NIVEL                    = ?,
            FLG_CRITICA_NUMLOTE          = ?,
            FLG_LIMITE_DIAS              = ?,
            FLG_PROCESSAMENTO_AUTOMATICO = ?,
            FLG_PROCESSAMENTO_PARCIAL    = ?,
            FLG_SALDO_DEVEDOR            = ?,
            NUM_MINUTO_INI_LIB_RECARGA   = ?,
            NUM_MINUTO_FIM_LIB_RECARGA   = ?,
            FLG_EMITE_RECIBO_PEDIDO      = ?,
            FLG_SUPERCANAL               = ?,
            FLG_PAGTOFUTURO              = ?,
            COD_CLASSIFICACAO_PESSOA     = ?,
            COD_ATIVIDADE                = ?,
            DT_MANUTENCAO                = SYSDATE,
            ID_USUARIO_MANUTENCAO        = ?
        WHERE COD_CANAL = ?
        """;

    private static final String SQL_UPDATE_STATUS = """
        UPDATE SPTRANSDBA.CANAIS SET
            ST_CANAIS             = ?,
            DT_MANUTENCAO         = SYSDATE,
            ID_USUARIO_MANUTENCAO = ?
        WHERE COD_CANAL = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM SPTRANSDBA.CANAIS WHERE COD_CANAL = ?";

    @Override
    public Optional<SalesChannel> findById(String codCanal) {
        List<SalesChannel> result = jdbc.query(
                SQL_SELECT_BASE + "WHERE COD_CANAL = ?", rowMapper(), codCanal);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public boolean existsById(String codCanal) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(1) FROM SPTRANSDBA.CANAIS WHERE COD_CANAL = ?",
                Integer.class, codCanal);
        return count != null && count > 0;
    }

    @Override
    public List<SalesChannel> findAll(String stCanais) {
        if (stCanais != null && !stCanais.isBlank()) {
            return jdbc.query(SQL_SELECT_BASE + "WHERE ST_CANAIS = ? ORDER BY COD_CANAL",
                    rowMapper(), stCanais);
        }
        return jdbc.query(SQL_SELECT_BASE + "ORDER BY COD_CANAL", rowMapper());
    }

    @Override
    public SalesChannel save(SalesChannel sc) {
        if (existsById(sc.getCodCanal())) {
            jdbc.update(SQL_UPDATE,
                    sc.getCodCanalSuperior(),
                    sc.getDesCanal(),
                    sc.getDesRazaoSocial(),
                    sc.getDesNomeFantasia(),
                    sc.getVlCaucao(),
                    toSqlDate(sc.getDtInicioCaucao()),
                    toSqlDate(sc.getDtFimCaucao()),
                    sc.getSeqNivel(),
                    sc.getFlgCriticaNumlote(),
                    sc.getFlgLimiteDias(),
                    sc.getFlgProcessamentoAutomatico(),
                    sc.getFlgProcessamentoParcial(),
                    sc.getFlgSaldoDevedor(),
                    sc.getNumMinutoIniLibRecarga(),
                    sc.getNumMinutoFimLibRecarga(),
                    sc.getFlgEmiteReciboPedido(),
                    sc.getFlgSupercanal(),
                    sc.getFlgPagtoFuturo(),
                    sc.getCodClassificacaoPessoa() != null ? sc.getCodClassificacaoPessoa().getCodClassificacaoPessoa() : null,
                    sc.getCodAtividade() != null ? sc.getCodAtividade().getCodAtividade() : null,
                    sc.getIdUsuarioManutencao() != null ? sc.getIdUsuarioManutencao().getIdUsuario() : null,
                    sc.getCodCanal()
            );
        } else {
            jdbc.update(SQL_INSERT,
                    sc.getCodCanal(),
                    sc.getCodDocumento(),
                    sc.getCodCanalSuperior(),
                    sc.getDesCanal(),
                    sc.getCodTipoDocumento(),
                    sc.getDesRazaoSocial(),
                    sc.getStCanais(),
                    sc.getDesNomeFantasia(),
                    sc.getVlCaucao(),
                    toSqlDate(sc.getDtInicioCaucao()),
                    toSqlDate(sc.getDtFimCaucao()),
                    sc.getSeqNivel(),
                    sc.getFlgCriticaNumlote(),
                    sc.getFlgLimiteDias(),
                    sc.getFlgProcessamentoAutomatico(),
                    sc.getFlgProcessamentoParcial(),
                    sc.getFlgSaldoDevedor(),
                    sc.getNumMinutoIniLibRecarga(),
                    sc.getNumMinutoFimLibRecarga(),
                    sc.getFlgEmiteReciboPedido(),
                    sc.getFlgSupercanal(),
                    sc.getFlgPagtoFuturo(),
                    sc.getCodClassificacaoPessoa() != null ? sc.getCodClassificacaoPessoa().getCodClassificacaoPessoa() : null,
                    sc.getCodAtividade() != null ? sc.getCodAtividade().getCodAtividade() : null,
                    sc.getIdUsuarioCadastro() != null ? sc.getIdUsuarioCadastro().getIdUsuario() : null,
                    sc.getIdUsuarioManutencao() != null ? sc.getIdUsuarioManutencao().getIdUsuario() : null
            );
        }
        return findById(sc.getCodCanal()).orElseThrow();
    }

    @Override
    public void updateStatus(String codCanal, String stCanais, Long idUsuario) {
        jdbc.update(SQL_UPDATE_STATUS, stCanais, idUsuario, codCanal);
    }

    @Override
    public void deleteById(String codCanal) {
        jdbc.update(SQL_DELETE, codCanal);
    }

    private RowMapper<SalesChannel> rowMapper() {
        return (rs, rowNum) -> new SalesChannel(
                rs.getString("COD_CANAL"),
                rs.getString("COD_DOCUMENTO"),
                rs.getString("COD_CANAL_SUPERIOR"),
                rs.getString("DES_CANAL"),
                rs.getString("COD_TIPO_DOCUMENTO"),
                toLocalDateTime(rs.getTimestamp("DT_MANUTENCAO")),
                rs.getString("DES_RAZAO_SOCIAL"),
                rs.getString("ST_CANAIS"),
                rs.getString("DES_NOME_FANTASIA"),
                toLocalDateTime(rs.getTimestamp("DT_CADASTRO")),
                rs.getBigDecimal("VL_CAUCAO"),
                toLocalDate(rs.getDate("DT_INICIO_CAUCAO")),
                toLocalDate(rs.getDate("DT_FIM_CAUCAO")),
                rs.getObject("SEQ_NIVEL", Integer.class),
                rs.getString("FLG_CRITICA_NUMLOTE"),
                rs.getObject("FLG_LIMITE_DIAS", Integer.class),
                rs.getString("FLG_PROCESSAMENTO_AUTOMATICO"),
                rs.getString("FLG_PROCESSAMENTO_PARCIAL"),
                rs.getString("FLG_SALDO_DEVEDOR"),
                rs.getObject("NUM_MINUTO_INI_LIB_RECARGA", Integer.class),
                rs.getObject("NUM_MINUTO_FIM_LIB_RECARGA", Integer.class),
                rs.getString("FLG_EMITE_RECIBO_PEDIDO"),
                rs.getString("FLG_SUPERCANAL"),
                rs.getString("FLG_PAGTOFUTURO"),
                rs.getString("COD_CLASSIFICACAO_PESSOA") != null
                        ? new ClassificationPerson(rs.getString("COD_CLASSIFICACAO_PESSOA"), null, null, null, null, null, null, null)
                        : null,
                rs.getString("COD_ATIVIDADE") != null
                        ? new TypesActivity(rs.getString("COD_ATIVIDADE"), null, null, null, null)
                        : null,
                null,
                null
        );
    }

    private LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }

    private LocalDate toLocalDate(Date d) {
        return d != null ? d.toLocalDate() : null;
    }

    private Date toSqlDate(LocalDate ld) {
        return ld != null ? Date.valueOf(ld) : null;
    }
}
