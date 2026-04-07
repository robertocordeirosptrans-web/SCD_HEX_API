package br.sptrans.scd.product.adapter.in.rest.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
        LocalDateTime dtVidaInicio,
        LocalDateTime dtVidaFim,
        LocalDateTime dtLiberacao,
        LocalDateTime dtLancamento,
        LocalDateTime dtVendaInicio,
        LocalDateTime dtVendaFim,
        LocalDateTime dtUsoInicio,
        LocalDateTime dtUsoFim,
        LocalDateTime dtTrocaInicio,
        LocalDateTime dtTrocaFim,
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

    @JsonCreator
    public ProductVersionRequest(
            @JsonProperty("codProduto") String codProduto,
            @JsonProperty("codVersao") String codVersao,
            @JsonProperty("desProdutoVersoes") String desProdutoVersoes,
            @JsonProperty("dtValidade") LocalDateTime dtValidade,
            @JsonProperty("dtVidaInicio") LocalDateTime dtVidaInicio,
            @JsonProperty("dtVidaFim") LocalDateTime dtVidaFim,
            @JsonProperty("dtLiberacao") LocalDateTime dtLiberacao,
            @JsonProperty("dtLancamento") LocalDateTime dtLancamento,
            @JsonProperty("dtVendaInicio") LocalDateTime dtVendaInicio,
            @JsonProperty("dtVendaFim") LocalDateTime dtVendaFim,
            @JsonProperty("dtUsoInicio") LocalDateTime dtUsoInicio,
            @JsonProperty("dtUsoFim") LocalDateTime dtUsoFim,
            @JsonProperty("dtTrocaInicio") LocalDateTime dtTrocaInicio,
            @JsonProperty("dtTrocaFim") LocalDateTime dtTrocaFim,
            @JsonProperty("flgBloqAquisicao") String flgBloqAquisicao,
            @JsonProperty("flgBloqDevolucao") String flgBloqDevolucao,
            @JsonProperty("flgBloqDistribuicao") String flgBloqDistribuicao,
            @JsonProperty("flgBloqFabricacao") String flgBloqFabricacao,
            @JsonProperty("flgBloqPedido") String flgBloqPedido,
            @JsonProperty("flgBloqTroca") String flgBloqTroca,
            @JsonProperty("flgBloqVenda") String flgBloqVenda,
            @JsonProperty("idUsuarioCadastro") Long idUsuarioCadastro,
            @JsonProperty("idUsuarioManutencao") Long idUsuarioManutencao,
            @JsonProperty("stProdutosVersoes") String stProdutosVersoes
    ) {
        this.codProduto = codProduto;
        this.codVersao = codVersao;
        this.desProdutoVersoes = desProdutoVersoes;
        this.dtValidade = dtValidade;
        this.dtVidaInicio = dtVidaInicio;
        this.dtVidaFim = dtVidaFim;
        this.dtLiberacao = dtLiberacao;
        this.dtLancamento = dtLancamento;
        this.dtVendaInicio = dtVendaInicio;
        this.dtVendaFim = dtVendaFim;
        this.dtUsoInicio = dtUsoInicio;
        this.dtUsoFim = dtUsoFim;
        this.dtTrocaInicio = dtTrocaInicio;
        this.dtTrocaFim = dtTrocaFim;
        this.flgBloqAquisicao = flgBloqAquisicao;
        this.flgBloqDevolucao = flgBloqDevolucao;
        this.flgBloqDistribuicao = flgBloqDistribuicao;
        this.flgBloqFabricacao = flgBloqFabricacao;
        this.flgBloqPedido = flgBloqPedido;
        this.flgBloqTroca = flgBloqTroca;
        this.flgBloqVenda = flgBloqVenda;
        this.idUsuarioCadastro = idUsuarioCadastro;
        this.idUsuarioManutencao = idUsuarioManutencao;
        this.stProdutosVersoes = stProdutosVersoes;
    }
}
