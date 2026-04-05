package br.sptrans.scd.channel.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductChannel {

    private ProductChannelKey id;

    private Integer qtdLimiteComercializacao;

    private Integer qtdMinimaEstoque;

    private Integer qtdMaximaEstoque;

    private Integer qtdMinimaRessuprimento;

    private Integer qtdMaximaRessuprimento;

    private Integer codOrgaoEmissor;

    private Integer vlFace;

    private String codStatus;

    private LocalDateTime dtCadastro;

    private LocalDateTime dtManutencao;

    private Integer codConvenio;

    private Integer codTipoOperHM;

    private String flgCarac;

    private User idUsuarioCadastro;

    private User idUsuarioManutencao;

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
            String codStatus,
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
        if (codStatus == null || codStatus.isEmpty()) {
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
            String codStatus,
            LocalDateTime dtManutencao,
            Integer codConvenio,
            Integer codTipoOperHM,
            String flgCarac,
            User idUsuarioManutencao
    ) {
        if (codStatus == null || codStatus.isEmpty()) {
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
}
