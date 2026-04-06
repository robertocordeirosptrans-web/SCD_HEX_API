package br.sptrans.scd.channel.adapter.port.in.rest.dto;

    public record CreateProductChannelRequest(
            String codCanal,
            String codProduto,
            Integer qtdLimiteComercializacao,
            Integer qtdMinimaEstoque,
            Integer qtdMaximaEstoque,
            Integer qtdMinimaRessuprimento,
            Integer qtdMaximaRessuprimento,
            Integer codOrgaoEmissor,
            Integer vlFace,
            String codStatus,
            Integer codConvenio,
            Integer codTipoOperHM,
            String flgCarac) {

    }