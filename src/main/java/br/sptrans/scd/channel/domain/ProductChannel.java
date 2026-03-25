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

    private Integer tipoOperHM;

    private String flgCarac;

    private User idUsuarioCadastro;

    private User idUsuarioManutencao;
}
