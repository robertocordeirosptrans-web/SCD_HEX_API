package br.sptrans.scd.channel.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ProductChannel {

    private final ProductChannelKey id;

    @Setter private Integer qtdLimiteComercializacao;

    @Setter private Integer qtdMinimaEstoque;

    @Setter private Integer qtdMaximaEstoque;

    @Setter private Integer qtdMinimaRessuprimento;

    @Setter private Integer qtdMaximaRessuprimento;

    @Setter private Integer codOrgaoEmissor;

    @Setter private Integer vlFace;

    @Setter private ChannelDomainStatus codStatus;

    private final LocalDateTime dtCadastro;

    @Setter private LocalDateTime dtManutencao;

    @Setter private Integer codConvenio;

    @Setter private Integer codTipoOperHM;

    @Setter private String flgCarac;

    @Setter private  User idUsuarioCadastro;

    @Setter private User idUsuarioManutencao;

    // --- Métodos de Fábrica e Atualização (DDD) ---

    /**
     * Cria uma nova instância de ProductChannel, encapsulando validações obrigatórias.
     */
    public static ProductChannel criar(
            ProductChannelKey id,
            Integer qtdLimiteComercializacao,
            Integer qtdMinimaEstoque,
            Integer qtdMaximaEstoque,
            Integer qtdMinimaRessuprimento,
            Integer qtdMaximaRessuprimento,
            Integer codOrgaoEmissor,
            Integer vlFace,
            ChannelDomainStatus codStatus,
            LocalDateTime dtCadastro,
            LocalDateTime dtManutencao,
            Integer codConvenio,
            Integer codTipoOperHM,
            String flgCarac,
            User idUsuarioCadastro,
            User idUsuarioManutencao
    ) {
        if (id == null) {
            throw new IllegalArgumentException("Chave do ProductChannel é obrigatória");
        }
        if (codStatus == null) {
            throw new IllegalArgumentException("Status do ProductChannel é obrigatório");
        }
        // Outras validações podem ser adicionadas aqui
        return new ProductChannel(id, qtdLimiteComercializacao, qtdMinimaEstoque, qtdMaximaEstoque, qtdMinimaRessuprimento,
                qtdMaximaRessuprimento, codOrgaoEmissor, vlFace, codStatus, dtCadastro, dtManutencao, codConvenio,
                codTipoOperHM, flgCarac, idUsuarioCadastro, idUsuarioManutencao);
    }

    /**
     * Atualiza os dados do ProductChannel, encapsulando regras de negócio.
     */
    public void atualizar(
            Integer qtdLimiteComercializacao,
            Integer qtdMinimaEstoque,
            Integer qtdMaximaEstoque,
            Integer qtdMinimaRessuprimento,
            Integer qtdMaximaRessuprimento,
            Integer codOrgaoEmissor,
            Integer vlFace,
            ChannelDomainStatus codStatus,
            LocalDateTime dtManutencao,
            Integer codConvenio,
            Integer codTipoOperHM,
            String flgCarac,
            User idUsuarioManutencao
    ) {
        if (codStatus == null) {
            throw new IllegalArgumentException("Status do ProductChannel é obrigatório");
        }
        this.qtdLimiteComercializacao = qtdLimiteComercializacao;
        this.qtdMinimaEstoque = qtdMinimaEstoque;
        this.qtdMaximaEstoque = qtdMaximaEstoque;
        this.qtdMinimaRessuprimento = qtdMinimaRessuprimento;
        this.qtdMaximaRessuprimento = qtdMaximaRessuprimento;
        this.codOrgaoEmissor = codOrgaoEmissor;
        this.vlFace = vlFace;
        this.codStatus = codStatus;
        this.dtManutencao = dtManutencao;
        this.codConvenio = codConvenio;
        this.codTipoOperHM = codTipoOperHM;
        this.flgCarac = flgCarac;
        this.idUsuarioManutencao = idUsuarioManutencao;
    }

    // -------------------------------------------------------------------------
    // Consultas de status
    // -------------------------------------------------------------------------

    public boolean isAtivo() {
        return ChannelDomainStatus.ACTIVE.equals(codStatus);
    }

    public boolean isInativo() {
        return ChannelDomainStatus.INACTIVE.equals(codStatus);
    }

    // -------------------------------------------------------------------------
    // Transições de status
    // -------------------------------------------------------------------------

    /**
     * Ativa o produto no canal.
     *
     * @param operador usuário responsável pela operação
     */
    public void activate(User operador) {
        this.codStatus = ChannelDomainStatus.ACTIVE;
        this.idUsuarioManutencao = operador;
        this.dtManutencao = LocalDateTime.now();
    }

    /**
     * Inativa o produto no canal.
     *
     * @param operador usuário responsável pela operação
     */
    public void inactivate(User operador) {
        this.codStatus = ChannelDomainStatus.INACTIVE;
        this.idUsuarioManutencao = operador;
        this.dtManutencao = LocalDateTime.now();
    }
}
