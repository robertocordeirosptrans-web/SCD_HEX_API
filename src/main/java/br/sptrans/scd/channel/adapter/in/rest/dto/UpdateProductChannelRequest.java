package br.sptrans.scd.channel.adapter.in.rest.dto;

import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;

public record UpdateProductChannelRequest(
        Integer qtdLimiteComercializacao,
        Integer qtdMinimaEstoque,
        Integer qtdMaximaEstoque,
        Integer qtdMinimaRessuprimento,
        Integer qtdMaximaRessuprimento,
        Integer codOrgaoEmissor,
        Integer vlFace,
        ChannelDomainStatus codStatus,
        Integer codConvenio,
        Integer codTipoOperHM,
        String flgCarac) {

}