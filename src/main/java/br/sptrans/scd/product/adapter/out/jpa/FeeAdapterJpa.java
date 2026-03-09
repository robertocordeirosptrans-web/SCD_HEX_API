package br.sptrans.scd.product.adapter.out.jpa;

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
import br.sptrans.scd.product.application.port.out.AdministrativeFeeRepository;
import br.sptrans.scd.product.application.port.out.ChannelFeeRepository;
import br.sptrans.scd.product.application.port.out.DestinyFeeRepository;
import br.sptrans.scd.product.application.port.out.FeeRepository;
import br.sptrans.scd.product.application.port.out.ServiceFeeRepository;
import br.sptrans.scd.product.domain.AdministrativeFee;
import br.sptrans.scd.product.domain.ChannelFee;
import br.sptrans.scd.product.domain.ChannelFeeKey;
import br.sptrans.scd.product.domain.DestinyFee;
import br.sptrans.scd.product.domain.Fee;
import br.sptrans.scd.product.domain.ServiceFee;
import lombok.RequiredArgsConstructor;

/**
 * Adaptador JPA (SQL nativo Oracle) para as tabelas de Tarifa e Taxas.
//  * <p>
 * Tabelas envolvidas: - SPTRANSDBA.TAXAS → cabeçalho da tarifa (PK: ID_TAXA via
 * SEQ_TAXA_ID) - SPTRANSDBA.TAXAS_ADMINISTRATIVA → faixas de taxa
 * administrativa (FK: ID_TAXA) - SPTRANSDBA.TAXAS_SERVICO → faixas de taxa de
 * serviço (FK: ID_TAXA) - SPTRANSDBA.TAXA_SCANAL → canais de destino da taxa
 * (FK: ID_TAXA)
 * <p>
 * Regras Oracle: - ID_TAXA gerado pela sequence SEQ_TAXA_ID (NEXTVAL) -
 * Sobreposição de vigência verificada via SQL de range overlap
 */
@Repository
@RequiredArgsConstructor
public class FeeAdapterJpa implements AdministrativeFeeRepository, FeeRepository, ChannelFeeRepository, DestinyFeeRepository, ServiceFeeRepository {

    private final JdbcTemplate jdbc;

    // -------------------------------------------------------------------------
    // RowMappers
    // -------------------------------------------------------------------------
    private final RowMapper<Fee>             feeRowMapper     = (rs, rn) -> mapFee(rs);
    private final RowMapper<AdministrativeFee> admFeeRowMapper = (rs, rn) -> mapAdministrativeFee(rs);
    private final RowMapper<ServiceFee>      srvFeeRowMapper  = (rs, rn) -> mapServiceFee(rs);
    private final RowMapper<ChannelFee>      chFeeRowMapper   = (rs, rn) -> mapChannelFee(rs);
    private final RowMapper<DestinyFee>      desFeeRowMapper  = (rs, rn) -> mapDestinyFee(rs);

    // -------------------------------------------------------------------------
    // SQL – TAXAS (Fee)
    // -------------------------------------------------------------------------
    private static final String SQL_FEE_SELECT = """
            SELECT ID_TAXA, DT_INICIAL, DSC_TAXA, DT_FINAL, COD_CANAL, COD_PRODUTO
            FROM SPTRANSDBA.TAXAS
            """;
    private static final String SQL_FEE_NEXTVAL = "SELECT SPTRANSDBA.SEQ_TAXA_ID.NEXTVAL FROM DUAL";
    private static final String SQL_FEE_INSERT  = """
            INSERT INTO SPTRANSDBA.TAXAS (ID_TAXA, DT_INICIAL, DSC_TAXA, DT_FINAL, COD_CANAL, COD_PRODUTO)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
    private static final String SQL_FEE_UPDATE  = """
            UPDATE SPTRANSDBA.TAXAS SET
                DT_INICIAL  = ?,
                DSC_TAXA    = ?,
                DT_FINAL    = ?,
                COD_CANAL   = ?,
                COD_PRODUTO = ?
            WHERE ID_TAXA = ?
            """;

    // -------------------------------------------------------------------------
    // SQL – TAXAS_ADMINISTRATIVA (AdministrativeFee)
    // -------------------------------------------------------------------------
    private static final String SQL_ADM_SELECT  = """
            SELECT COD_TAXA_ADM, REC_INICIAL, REC_FINAL, VAL_FIXO, VAL_PERCENTUAL, ID_TAXA
            FROM SPTRANSDBA.TAXAS_ADMINISTRATIVA
            """;
    private static final String SQL_ADM_NEXTVAL = "SELECT SPTRANSDBA.SEQ_TAXA_ADM_ID.NEXTVAL FROM DUAL";
    private static final String SQL_ADM_INSERT  = """
            INSERT INTO SPTRANSDBA.TAXAS_ADMINISTRATIVA
                (COD_TAXA_ADM, REC_INICIAL, REC_FINAL, VAL_FIXO, VAL_PERCENTUAL, ID_TAXA)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
    private static final String SQL_ADM_UPDATE  = """
            UPDATE SPTRANSDBA.TAXAS_ADMINISTRATIVA SET
                REC_INICIAL    = ?,
                REC_FINAL      = ?,
                VAL_FIXO       = ?,
                VAL_PERCENTUAL = ?
            WHERE COD_TAXA_ADM = ?
            """;

    // -------------------------------------------------------------------------
    // SQL – TAXAS_SERVICO (ServiceFee)
    // -------------------------------------------------------------------------
    private static final String SQL_SRV_SELECT  = """
            SELECT COD_TAXA_SRV, REC_INICIAL, REC_FINAL, VAL_FIXO, VAL_PERCENTUAL, VAL_MINIMO, ID_TAXA
            FROM SPTRANSDBA.TAXAS_SERVICO
            """;
    private static final String SQL_SRV_NEXTVAL = "SELECT SPTRANSDBA.SEQ_TAXA_SRV_ID.NEXTVAL FROM DUAL";
    private static final String SQL_SRV_INSERT  = """
            INSERT INTO SPTRANSDBA.TAXAS_SERVICO
                (COD_TAXA_SRV, REC_INICIAL, REC_FINAL, VAL_FIXO, VAL_PERCENTUAL, VAL_MINIMO, ID_TAXA)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
    private static final String SQL_SRV_UPDATE  = """
            UPDATE SPTRANSDBA.TAXAS_SERVICO SET
                REC_INICIAL    = ?,
                REC_FINAL      = ?,
                VAL_FIXO       = ?,
                VAL_PERCENTUAL = ?,
                VAL_MINIMO     = ?
            WHERE COD_TAXA_SRV = ?
            """;

    // -------------------------------------------------------------------------
    // SQL – TAXA_SCANAL (ChannelFee)
    // -------------------------------------------------------------------------
    private static final String SQL_CH_SELECT = """
            SELECT COD_CANAL, COD_PRODUTO, VLT_INICIO, VLT_FINAL, VL_PERCENTUAL,
                   DT_INICIO, DT_FINAL, DT_MANUTENCAO, ID_USUARIO_MANUTENCAO
            FROM SPTRANSDBA.TAXA_SCANAL
            """;
    private static final String SQL_CH_INSERT = """
            INSERT INTO SPTRANSDBA.TAXA_SCANAL
                (COD_CANAL, COD_PRODUTO, VLT_INICIO, VLT_FINAL, VL_PERCENTUAL,
                 DT_INICIO, DT_FINAL, DT_MANUTENCAO, ID_USUARIO_MANUTENCAO)
            VALUES (?, ?, ?, ?, ?, ?, ?, SYSDATE, ?)
            """;
    private static final String SQL_CH_UPDATE = """
            UPDATE SPTRANSDBA.TAXA_SCANAL SET
                VLT_INICIO            = ?,
                VLT_FINAL             = ?,
                VL_PERCENTUAL         = ?,
                DT_INICIO             = ?,
                DT_FINAL              = ?,
                DT_MANUTENCAO         = SYSDATE,
                ID_USUARIO_MANUTENCAO = ?
            WHERE COD_CANAL = ? AND COD_PRODUTO = ?
            """;

    // -------------------------------------------------------------------------
    // SQL – TAXAS_DESTINO (DestinyFee)
    // -------------------------------------------------------------------------
    private static final String SQL_DES_SELECT  = """
            SELECT COD_TAXA_DES, COD_CANAL_DESTINO
            FROM SPTRANSDBA.TAXAS_DESTINO
            """;
    private static final String SQL_DES_NEXTVAL = "SELECT SPTRANSDBA.SEQ_TAXA_DES_ID.NEXTVAL FROM DUAL";
    private static final String SQL_DES_INSERT  = """
            INSERT INTO SPTRANSDBA.TAXAS_DESTINO (COD_TAXA_DES, COD_CANAL_DESTINO)
            VALUES (?, ?)
            """;
    private static final String SQL_DES_UPDATE  = """
            UPDATE SPTRANSDBA.TAXAS_DESTINO SET COD_CANAL_DESTINO = ?
            WHERE COD_TAXA_DES = ?
            """;

    // =========================================================================
    // FeeRepository
    // =========================================================================

    @Override
    public Fee save(Fee taxa) {
        if (taxa.getCodTaxa() != null && existsByFeeId(taxa.getCodTaxa())) {
            jdbc.update(SQL_FEE_UPDATE,
                    ts(taxa.getDtInicial()),
                    taxa.getDscTaxa(),
                    ts(taxa.getDtFinal()),
                    taxa.getCodCanal(),
                    taxa.getCodProduto(),
                    taxa.getCodTaxa());
        } else {
            Long newId = jdbc.queryForObject(SQL_FEE_NEXTVAL, Long.class);
            taxa.setCodTaxa(newId);
            jdbc.update(SQL_FEE_INSERT,
                    newId,
                    ts(taxa.getDtInicial()),
                    taxa.getDscTaxa(),
                    ts(taxa.getDtFinal()),
                    taxa.getCodCanal(),
                    taxa.getCodProduto());
        }
        return findByIdFee(taxa.getCodTaxa()).orElseThrow();
    }

    @Override
    public Optional<Fee> findByIdFee(Long codTaxa) {
        List<Fee> result = jdbc.query(SQL_FEE_SELECT + "WHERE ID_TAXA = ?", feeRowMapper, codTaxa);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Fee> findByCanal(String codCanal) {
        return jdbc.query(SQL_FEE_SELECT + "WHERE COD_CANAL = ?", feeRowMapper, codCanal);
    }

    @Override
    public List<Fee> findByProduto(String codProduto) {
        return jdbc.query(SQL_FEE_SELECT + "WHERE COD_PRODUTO = ?", feeRowMapper, codProduto);
    }

    @Override
    public List<Fee> findByCanalProduto(String codCanal, String codProduto) {
        return jdbc.query(SQL_FEE_SELECT + "WHERE COD_CANAL = ? AND COD_PRODUTO = ?",
                feeRowMapper, codCanal, codProduto);
    }

    @Override
    public Optional<Fee> findAtivaByCanalProduto(String codCanal, String codProduto) {
        String sql = SQL_FEE_SELECT + """
                WHERE COD_CANAL = ? AND COD_PRODUTO = ?
                  AND DT_INICIAL <= SYSDATE
                  AND (DT_FINAL IS NULL OR DT_FINAL >= SYSDATE)
                ORDER BY DT_INICIAL DESC
                FETCH FIRST 1 ROWS ONLY
                """;
        List<Fee> result = jdbc.query(sql, feeRowMapper, codCanal, codProduto);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    /**
     * Satisfaz FeeRepository, AdministrativeFeeRepository e ServiceFeeRepository.
     * Verifica existência na tabela TAXAS (Fee).
     * Para verificação nas demais tabelas, use os métodos privados dedicados.
     */
    @Override
    public boolean existsById(Long codTaxa) {
        return existsByFeeId(codTaxa);
    }

    // =========================================================================
    // AdministrativeFeeRepository
    // =========================================================================

    @Override
    public Optional<AdministrativeFee> findAdmById(Long codTaxaAdm) {
        List<AdministrativeFee> result = jdbc.query(
                SQL_ADM_SELECT + "WHERE COD_TAXA_ADM = ?", admFeeRowMapper, codTaxaAdm);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public AdministrativeFee save(AdministrativeFee taxasAdm) {
        if (taxasAdm.getCodTaxaAdm() != null && existsByAdmId(taxasAdm.getCodTaxaAdm())) {
            jdbc.update(SQL_ADM_UPDATE,
                    taxasAdm.getRecInicial(),
                    taxasAdm.getRecFinal(),
                    taxasAdm.getValFixo(),
                    taxasAdm.getValPercentual(),
                    taxasAdm.getCodTaxaAdm());
            return findAdmById(taxasAdm.getCodTaxaAdm()).orElseThrow();
        } else {
            Long newId = jdbc.queryForObject(SQL_ADM_NEXTVAL, Long.class);
            jdbc.update(SQL_ADM_INSERT,
                    newId,
                    taxasAdm.getRecInicial(),
                    taxasAdm.getRecFinal(),
                    taxasAdm.getValFixo(),
                    taxasAdm.getValPercentual(),
                    taxasAdm.getTaxa() != null ? taxasAdm.getTaxa().getCodTaxa() : null);
            return findAdmById(newId).orElseThrow();
        }
    }

    // =========================================================================
    // ServiceFeeRepository
    // =========================================================================

    @Override
    public Optional<ServiceFee> findSrvById(Long codTaxaSrv) {
        List<ServiceFee> result = jdbc.query(
                SQL_SRV_SELECT + "WHERE COD_TAXA_SRV = ?", srvFeeRowMapper, codTaxaSrv);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public ServiceFee save(ServiceFee taxasServico) {
        if (taxasServico.getCodTaxaSrv() != null && existsSrvById(taxasServico.getCodTaxaSrv())) {
            jdbc.update(SQL_SRV_UPDATE,
                    taxasServico.getRecInicial(),
                    taxasServico.getRecFinal(),
                    taxasServico.getValFixo(),
                    taxasServico.getValPercentual(),
                    taxasServico.getValMinimo(),
                    taxasServico.getCodTaxaSrv());
            return findSrvById(taxasServico.getCodTaxaSrv()).orElseThrow();
        } else {
            Long newId = jdbc.queryForObject(SQL_SRV_NEXTVAL, Long.class);
            jdbc.update(SQL_SRV_INSERT,
                    newId,
                    taxasServico.getRecInicial(),
                    taxasServico.getRecFinal(),
                    taxasServico.getValFixo(),
                    taxasServico.getValPercentual(),
                    taxasServico.getValMinimo(),
                    taxasServico.getTaxa() != null ? taxasServico.getTaxa().getCodTaxa() : null);
            return findSrvById(newId).orElseThrow();
        }
    }

    // =========================================================================
    // ChannelFeeRepository
    // =========================================================================

    @Override
    public Optional<ChannelFee> findById(ChannelFeeKey id) {
        List<ChannelFee> result = jdbc.query(
                SQL_CH_SELECT + "WHERE COD_CANAL = ? AND COD_PRODUTO = ?",
                chFeeRowMapper, id.getCodCanal(), id.getCodProduto());
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public ChannelFee save(ChannelFee taxasScanal) {
        if (existsByKey(taxasScanal.getId())) {
            jdbc.update(SQL_CH_UPDATE,
                    taxasScanal.getVltInicio(),
                    taxasScanal.getVltFinal(),
                    taxasScanal.getVlPercentual(),
                    ts(taxasScanal.getDtInicio()),
                    ts(taxasScanal.getDtFinal()),
                    taxasScanal.getIdUsuarioManutencao() != null
                            ? taxasScanal.getIdUsuarioManutencao().getIdUsuario() : null,
                    taxasScanal.getId().getCodCanal(),
                    taxasScanal.getId().getCodProduto());
        } else {
            jdbc.update(SQL_CH_INSERT,
                    taxasScanal.getId().getCodCanal(),
                    taxasScanal.getId().getCodProduto(),
                    taxasScanal.getVltInicio(),
                    taxasScanal.getVltFinal(),
                    taxasScanal.getVlPercentual(),
                    ts(taxasScanal.getDtInicio()),
                    ts(taxasScanal.getDtFinal()),
                    taxasScanal.getIdUsuarioManutencao() != null
                            ? taxasScanal.getIdUsuarioManutencao().getIdUsuario() : null);
        }
        return findById(taxasScanal.getId()).orElseThrow();
    }

    @Override
    public boolean existsByKey(ChannelFeeKey id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(1) FROM SPTRANSDBA.TAXA_SCANAL WHERE COD_CANAL = ? AND COD_PRODUTO = ?",
                Integer.class, id.getCodCanal(), id.getCodProduto());
        return count != null && count > 0;
    }

    // =========================================================================
    // DestinyFeeRepository
    // =========================================================================

    @Override
    public Optional<DestinyFee> findDesById(Long id) {
        List<DestinyFee> result = jdbc.query(
                SQL_DES_SELECT + "WHERE COD_TAXA_DES = ?", desFeeRowMapper, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public DestinyFee save(DestinyFee taxasDes) {
        if (taxasDes.getCodTaxaDes() != null && existsByDesId(taxasDes)) {
            jdbc.update(SQL_DES_UPDATE, taxasDes.getCodCanalDestino(), taxasDes.getCodTaxaDes());
            return findDesById(taxasDes.getCodTaxaDes()).orElseThrow();
        } else {
            Long newId = jdbc.queryForObject(SQL_DES_NEXTVAL, Long.class);
            jdbc.update(SQL_DES_INSERT, newId, taxasDes.getCodCanalDestino());
            return findDesById(newId).orElseThrow();
        }
    }

    @Override
    public boolean existsByDesId(DestinyFee id) {
        if (id.getCodTaxaDes() == null) return false;
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(1) FROM SPTRANSDBA.TAXAS_DESTINO WHERE COD_TAXA_DES = ?",
                Integer.class, id.getCodTaxaDes());
        return count != null && count > 0;
    }

    // =========================================================================
    // RowMappers
    // =========================================================================

    private Fee mapFee(ResultSet rs) throws SQLException {
        Fee f = new Fee();
        f.setCodTaxa(rs.getLong("ID_TAXA"));
        f.setDtInicial(fromTs(rs.getTimestamp("DT_INICIAL")));
        f.setDscTaxa(rs.getString("DSC_TAXA"));
        f.setDtFinal(fromTs(rs.getTimestamp("DT_FINAL")));
        f.setCodCanal(rs.getString("COD_CANAL"));
        f.setCodProduto(rs.getString("COD_PRODUTO"));
        return f;
    }

    private AdministrativeFee mapAdministrativeFee(ResultSet rs) throws SQLException {
        try {
            AdministrativeFee adm = new AdministrativeFee();
            setField(adm, AdministrativeFee.class, "codTaxaAdm",    rs.getLong("COD_TAXA_ADM"));
            setField(adm, AdministrativeFee.class, "recInicial",     rs.getByte("REC_INICIAL"));
            setField(adm, AdministrativeFee.class, "recFinal",       rs.getByte("REC_FINAL"));
            setField(adm, AdministrativeFee.class, "valFixo",        rs.getBigDecimal("VAL_FIXO"));
            setField(adm, AdministrativeFee.class, "valPercentual",  rs.getBigDecimal("VAL_PERCENTUAL"));
            long idTaxa = rs.getLong("ID_TAXA");
            if (!rs.wasNull()) {
                Fee taxa = new Fee();
                taxa.setCodTaxa(idTaxa);
                setField(adm, AdministrativeFee.class, "taxa", taxa);
            }
            return adm;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private ServiceFee mapServiceFee(ResultSet rs) throws SQLException {
        try {
            ServiceFee srv = new ServiceFee();
            setField(srv, ServiceFee.class, "codTaxaSrv",    rs.getLong("COD_TAXA_SRV"));
            setField(srv, ServiceFee.class, "recInicial",    rs.getByte("REC_INICIAL"));
            setField(srv, ServiceFee.class, "recFinal",      rs.getByte("REC_FINAL"));
            setField(srv, ServiceFee.class, "valFixo",       rs.getBigDecimal("VAL_FIXO"));
            setField(srv, ServiceFee.class, "valPercentual", rs.getBigDecimal("VAL_PERCENTUAL"));
            setField(srv, ServiceFee.class, "valMinimo",     rs.getBigDecimal("VAL_MINIMO"));
            long idTaxa = rs.getLong("ID_TAXA");
            if (!rs.wasNull()) {
                Fee taxa = new Fee();
                taxa.setCodTaxa(idTaxa);
                setField(srv, ServiceFee.class, "taxa", taxa);
            }
            return srv;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private ChannelFee mapChannelFee(ResultSet rs) throws SQLException {
        ChannelFee ch = new ChannelFee();
        ch.setId(new ChannelFeeKey(rs.getString("COD_CANAL"), rs.getString("COD_PRODUTO")));
        ch.setVltInicio(rs.getBigDecimal("VLT_INICIO"));
        ch.setVltFinal(rs.getBigDecimal("VLT_FINAL"));
        ch.setVlPercentual(rs.getBigDecimal("VL_PERCENTUAL"));
        ch.setDtInicio(fromTs(rs.getTimestamp("DT_INICIO")));
        ch.setDtFinal(fromTs(rs.getTimestamp("DT_FINAL")));
        ch.setDtManutencao(fromTs(rs.getTimestamp("DT_MANUTENCAO")));
        long idUsuario = rs.getLong("ID_USUARIO_MANUTENCAO");
        if (!rs.wasNull()) {
            User user = new User();
            user.setIdUsuario(idUsuario);
            ch.setIdUsuarioManutencao(user);
        }
        return ch;
    }

    private DestinyFee mapDestinyFee(ResultSet rs) throws SQLException {
        try {
            DestinyFee des = new DestinyFee();
            setField(des, DestinyFee.class, "codTaxaDes",      rs.getLong("COD_TAXA_DES"));
            setField(des, DestinyFee.class, "codCanalDestino", rs.getString("COD_CANAL_DESTINO"));
            return des;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    private boolean existsByFeeId(Long id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(1) FROM SPTRANSDBA.TAXAS WHERE ID_TAXA = ?", Integer.class, id);
        return count != null && count > 0;
    }

    private boolean existsByAdmId(Long id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(1) FROM SPTRANSDBA.TAXAS_ADMINISTRATIVA WHERE COD_TAXA_ADM = ?",
                Integer.class, id);
        return count != null && count > 0;
    }

    public boolean existsSrvById(Long id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(1) FROM SPTRANSDBA.TAXAS_SERVICO WHERE COD_TAXA_SRV = ?",
                Integer.class, id);
        return count != null && count > 0;
    }

    private static void setField(Object target, Class<?> clazz, String fieldName, Object value)
            throws ReflectiveOperationException {
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
