package br.sptrans.scd.auth.adapter.in.rest.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de resposta para operações de mudança de status de usuário.
 * 
 * Retorna confirmação da operação com informações do usuário e seu novo status.
 */
public record StatusChangeResponseDTO(
    @JsonProperty("id_usuario")
    @Schema(description = "ID do usuário", example = "123")
    Long idUsuario,

    @JsonProperty("cod_login")
    @Schema(description = "Login do usuário", example = "jdoe")
    String codLogin,

    @JsonProperty("nom_usuario")
    @Schema(description = "Nome completo do usuário", example = "John Doe")
    String nomUsuario,

    @JsonProperty("cod_status")
    @Schema(description = "Novo status do usuário (ATIVO, BLOQUEADO, INATIVO)", example = "ATIVO")
    String codStatus,

    @JsonProperty("cod_status_descricao")
    @Schema(description = "Descrição do status", example = "Ativo")
    String codStatusDescricao,

    @JsonProperty("dt_manutencao")
    @Schema(description = "Data/hora da alteração", example = "2026-04-24T10:30:00")
    LocalDateTime dtManutencao,

    @JsonProperty("mensagem")
    @Schema(description = "Mensagem descritiva da operação", example = "Usuário desbloqueado com sucesso")
    String mensagem
) {
}
