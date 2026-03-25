package br.sptrans.scd.channel.adapter.port.in.rest.dto;

public record ProductChDTO(
        Integer qtdLimiteComercializacao,
        Integer qtdMinimaEstoque,
        Integer qtdMaximaEstoque,
        Integer qtdMinimaRessuprimento,
        Integer qtdMaximaRessuprimento,
        Integer codOrgaoEmissor,
        Integer vlFace,
        String codStatus,
        String dtCadastro,
        String dtManutencao,
        Integer codConvenio,
        Integer tipoOperHM,
        String flgCarac,
        String codProduto,
        String codCanal,
        // Usuários detalhados (apenas id, login e nome)
        UserSimpleDTO usuarioCadastroInfo,
        UserSimpleDTO usuarioManutencaoInfo
        ) {

}
