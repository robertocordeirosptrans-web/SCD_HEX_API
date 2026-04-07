package br.sptrans.scd.channel.adapter.in.rest.dto;

import java.time.LocalDateTime;

public record AddressChannelResponseDTO(
        String codEndereco,
        String codEmpregador,
        String desLogradouro,
        String codFornecedor,
        String codTipoEndereco,
        String codCEP,
        String desBairro,
        String desCidade,
        String desUF,
        Integer numDDD,
        Integer numFone,
        Integer numFax,
        String desObs,
        String stEnderecos,
        String desNumero,
        LocalDateTime dtCadastro,
        LocalDateTime dtManutencao,
        // Canal info (flattened)
        String codCanal,
        String desCanal,
        // Usuários (login)
        String usuarioCadastro,
        String usuarioManutencao,
        // Usuários detalhados
        UserSimpleDTO usuarioCadastroInfo,
        UserSimpleDTO usuarioManutencaoInfo
) {
}
