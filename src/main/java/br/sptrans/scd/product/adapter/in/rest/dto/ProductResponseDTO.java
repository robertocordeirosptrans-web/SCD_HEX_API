package br.sptrans.scd.product.adapter.in.rest.dto;

import java.time.LocalDateTime;

public record ProductResponseDTO(
    // ID do produto
    String codProduto,

    // TiposProdutos
    String codTipoProduto,
    String desTipoProduto,

    // Tecnologia
    String codTecnologia,
    String desTecnologia,

    // Modalidade
    String codModalidade,
    String desModalidade,

    // User Cadastro
    Long idUsuarioCadastro,
    String nomeUsuarioCadastro,

    // User Manutenção
    Long idUsuarioManutencao,
    String nomeUsuarioManutencao,

    // Family
    String codFamilia,
    String desFamilia,

    // Especies
    String codEspecie,
    String desEspecie,

    // Campos diretos
    String desProduto,
    String desEmissorResponsavel,
    String codStatus,
    String desUtilizacao,
    LocalDateTime dtCadastro,
    LocalDateTime dtManutencao,

    // Flags
    String flgBloqFabricacao,
    String flgBloqVenda,
    String flgBloqDistribuicao,
    String flgBloqTroca,
    String flgBloqAquisicao,
    String flgBloqPedido,
    String flgBloqDevolucao,
    String flgInicializado,
    String flgComercializado,

    // Entidade
    String codEntidade
) {}