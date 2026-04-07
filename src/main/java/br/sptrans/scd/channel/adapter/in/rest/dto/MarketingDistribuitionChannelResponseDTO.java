package br.sptrans.scd.channel.adapter.in.rest.dto;

import java.time.LocalDateTime;

public record MarketingDistribuitionChannelResponseDTO(
        String codCanalComercializacao,
        String codCanalDistribuicao,
        String codStatus,
        LocalDateTime dtCadastro,
        LocalDateTime dtManutencao,
        // Usuários
        String usuarioCadastro,
        String usuarioManutencao,
        // Usuários detalhados
        UserSimpleDTO usuarioCadastroInfo,
        UserSimpleDTO usuarioManutencaoInfo
) {
}
