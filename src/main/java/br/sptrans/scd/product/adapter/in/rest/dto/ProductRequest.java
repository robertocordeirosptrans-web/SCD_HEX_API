package br.sptrans.scd.product.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductRequest(

        @NotBlank(message = "Código do produto é obrigatório") @Size(min = 1, max = 20, message = "Código tipo produto deve ter até 20 caracteres") String codProduto,
        // Relacionamentos com ID String
        @NotBlank(message = "Código tipo produto é obrigatório") @Size(min = 1, max = 20, message = "Código tipo produto deve ter até 20 caracteres") String codTipoProduto,
        @NotBlank(message = "Código tecnologia é obrigatório") @Size(min = 1, max = 20, message = "Código tecnologia deve ter até 20 caracteres") String codTecnologia,
        @NotBlank(message = "Código modalidade é obrigatório") @Size(min = 1, max = 20, message = "Código modalidade deve ter até 20 caracteres") String codModalidade,
        @NotBlank(message = "Código família é obrigatório") @Size(min = 1, max = 20, message = "Código família deve ter até 20 caracteres") String codFamilia,
        @NotBlank(message = "Código espécie é obrigatório") @Size(min = 1, max = 20, message = "Código espécie deve ter até 20 caracteres") String codEspecie,


        // Campos diretos
        @NotBlank(message = "Descrição do produto é obrigatória") @Size(min = 3, max = 100, message = "Descrição deve ter entre 3 e 100 caracteres") String desProduto,
        @NotBlank(message = "Descrição emissor responsável é obrigatória") @Size(max = 100, message = "Descrição emissor não pode exceder 100 caracteres") String desEmissorResponsavel,
        @NotBlank(message = "Código status é obrigatório") @Pattern(regexp = "[A-Z]", message = "Código status deve ser uma letra maiúscula") String codStatus,
        @Size(max = 500, message = "Descrição utilização não pode exceder 500 caracteres") String desUtilizacao,
        // Flags com regex para valores fixos
        @Pattern(regexp = "[SN]", message = "Flag deve ser S ou N") String flgBloqFabricacao,
        @Pattern(regexp = "[SN]", message = "Flag deve ser S ou N") String flgBloqVenda,
        @Pattern(regexp = "[SN]", message = "Flag deve ser S ou N") String flgBloqDistribuicao,
        @Pattern(regexp = "[SN]", message = "Flag deve ser S ou N") String flgBloqTroca,
        @Pattern(regexp = "[SN]", message = "Flag deve ser S ou N") String flgBloqAquisicao,
        @Pattern(regexp = "[SN]", message = "Flag deve ser S ou N") String flgBloqPedido,
        @Pattern(regexp = "[SN]", message = "Flag deve ser S ou N") String flgBloqDevolucao,
        @Pattern(regexp = "[SN]", message = "Flag deve ser S ou N") String flgInicializado,
        @Pattern(regexp = "[SN]", message = "Flag deve ser S ou N") String flgComercializado,
        @NotBlank(message = "Código entidade é obrigatório") @Size(min = 1, max = 20, message = "Código entidade deve ter até 20 caracteres") String codEntidade,
        @NotBlank(message = "Código da classificacao é obrigatório") @Size(min = 1, max = 20, message = "Código classificacao deve ter até 20 caracteres") String codClassificacaoPessoa,
        @Pattern(regexp = "[SN]", message = "Flag deve ser S ou N") String flgRestManual,
        String codTipoCartao) {

    public ProductRequest {
        // Valores padrão para campos opcionais
        if (codStatus == null) {
            codStatus = "A";
        }
        if (flgInicializado == null) {
            flgInicializado = "N";
        }
        if (flgComercializado == null) {
            flgComercializado = "N";
        }
        // Valores padrão para outras flags
        if (flgBloqFabricacao == null) {
            flgBloqFabricacao = "N";
        }
        if (flgBloqVenda == null) {
            flgBloqVenda = "N";
        }
        if (flgBloqDistribuicao == null) {
            flgBloqDistribuicao = "N";
        }
        if (flgBloqTroca == null) {
            flgBloqTroca = "N";
        }
        if (flgBloqAquisicao == null) {
            flgBloqAquisicao = "N";
        }
        if (flgBloqPedido == null) {
            flgBloqPedido = "N";
        }
        if (flgBloqDevolucao == null) {
            flgBloqDevolucao = "N";
        }
        if (flgRestManual == null) {
            flgRestManual = "N";
        }
    }
}
