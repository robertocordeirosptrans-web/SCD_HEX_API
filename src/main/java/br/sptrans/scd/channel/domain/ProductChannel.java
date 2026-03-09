package br.sptrans.scd.channel.domain;

import java.time.LocalDateTime;
import java.util.List;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.domain.AdministrativeFee;
import br.sptrans.scd.product.domain.ChannelFee;
import br.sptrans.scd.product.domain.Fee;
import br.sptrans.scd.product.domain.Product;
import br.sptrans.scd.product.domain.ServiceFee;
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

    // RELACIONAMENTOS MAPEADOS CORRETAMENTE COM @MapsId
    private SalesChannel canal;

    private Product produto;

    // Relacionamentos 1:N 
    private List<AgreementValidity> vigencias;

    private List<RechargeLimit> limitesRecarga;

    private List<Fee> taxas;

    private List<ChannelFee> taxasCanal;

    private List<AdministrativeFee> taxasAdms;

    private List<ServiceFee> taxasServ;
}
