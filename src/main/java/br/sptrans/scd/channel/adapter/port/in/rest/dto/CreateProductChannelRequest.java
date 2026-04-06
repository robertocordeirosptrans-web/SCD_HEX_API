package br.sptrans.scd.channel.adapter.port.in.rest.dto;

import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record CreateProductChannelRequest(
                String codCanal,
                String codProduto,
                Integer qtdLimiteComercializacao,
                @Min(0) Integer qtdMinimaEstoque,
                Integer qtdMaximaEstoque,
                @Min(0) Integer qtdMinimaRessuprimento,
                Integer qtdMaximaRessuprimento,
                @Min(1) @Max(999) Integer codOrgaoEmissor,
                @Min(1) @Max(99) Integer vlFace,
                ChannelDomainStatus codStatus,
                @Min(1) @Max(999) Integer codConvenio,
                @Min(1) @Max(999) Integer codTipoOperHM,
                @Size(min = 1, max = 1, message = "A caracteristica é até 1 caractere") String flgCarac) {

}