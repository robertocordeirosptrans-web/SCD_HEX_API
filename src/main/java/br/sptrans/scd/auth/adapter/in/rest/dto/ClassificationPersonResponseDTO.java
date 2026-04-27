package br.sptrans.scd.auth.adapter.in.rest.dto;

import java.time.LocalDateTime;

public record ClassificationPersonResponseDTO(
    String codClassificacaoPessoa,
    String desClassificacaoPessoa,
    String flgVenda,
    LocalDateTime dtCadastro,
    LocalDateTime dtManutencao,
    String stClassificacoesPessoa,
    Long idUsuarioCadastro,
    Long idUsuarioManutencao
) {}
