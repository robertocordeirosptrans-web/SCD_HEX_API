package br.sptrans.scd.channel.adapter.port.in.rest.dto;

import java.time.LocalDateTime;

public record ContactChannelResponseDTO(
        String codContato,
        String codFornecedor,
        String codEmpregador,
        String desContato,
        String desEmailContato,
        Integer numDDD,
        Integer numFone,
        Integer numFoneRamal,
        Integer numFax,
        Integer numFaxRamal,
        String stEntidadeContato,
        String desComentarios,
        String codTipoDocumento,
        String codDocumento,
        LocalDateTime dtCadastro,
        LocalDateTime dtManutencao,
        // Canal info (flattened)
        String codCanal,
        String desCanal,
        // Usuários
        String usuarioCadastro,
        String usuarioManutencao,
        // Usuários detalhados
        UserSimpleDTO usuarioCadastroInfo,
        UserSimpleDTO usuarioManutencaoInfo
) {
}
