package br.sptrans.scd.product.adapter.in.rest.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductVersionRequest(
        @NotBlank(message = "Código do produto é obrigatório") @Size(min = 1, max = 20, message = "Código tipo produto deve ter até 20 caracteres") String codProduto,
        @NotBlank(message = "Código do produto é obrigatório") @Size(min = 1, max = 20, message = "Código tipo produto deve ter até 20 caracteres") String codVersao,
        @JsonAlias({
                "desProdutoVersoes",
                "desProdutosVersoes" }) @NotNull @Size(min = 1, max = 60, message = "Código tipo produto deve ter até 20 caracteres") String desProdutoVersoes,
        LocalDateTime dtValidade,
        @Pattern(regexp = "[SN]", message = "Flag deve ser S ou N") String flgBloqAquisicao,
        @Pattern(regexp = "[SN]", message = "Flag deve ser S ou N") String flgBloqDevolucao,
        @Pattern(regexp = "[SN]", message = "Flag deve ser S ou N") String flgBloqDistribuicao,
        @Pattern(regexp = "[SN]", message = "Flag deve ser S ou N") String flgBloqFabricacao,
        @Pattern(regexp = "[SN]", message = "Flag deve ser S ou N") String flgBloqPedido,
        @Pattern(regexp = "[SN]", message = "Flag deve ser S ou N") String flgBloqTroca,
        @Pattern(regexp = "[SN]", message = "Flag deve ser S ou N") String flgBloqVenda,
        @NotNull Long idUsuarioCadastro,
        @NotNull Long idUsuarioManutencao,
        @NotNull String stProdutosVersoes) {

}
