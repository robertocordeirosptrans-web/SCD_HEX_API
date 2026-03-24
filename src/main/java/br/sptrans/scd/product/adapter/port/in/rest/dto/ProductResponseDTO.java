package br.sptrans.scd.product.adapter.port.in.rest.dto;

import java.time.LocalDateTime;

import br.sptrans.scd.product.domain.Product;

public record ProductResponseDTO(
    // ID do produto
    String codProduto,

    // TiposProdutos
    String codTipoProduto,


    // Tecnologia
    String codTecnologia,


    // Modalidade
    String codModalidade,


    // User Cadastro
    Long idUsuarioCadastro,
 

    // User Manutenção
    Long idUsuarioManutencao,
 

    // Family
    String codFamilia,


    // Especies
    String codEspecie,
 

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
) {
    // Construtor que recebe a entidade Product
    public ProductResponseDTO(Product product) {
        this(
            // ID do produto
            product.getCodProduto(),

            // TiposProdutos
            product.getCodTipoProduto() != null ? product.getCodTipoProduto() : null,


            // Tecnologia
            product.getCodTecnologia() != null ? product.getCodTecnologia(): null,


            // Modalidade
            product.getCodModalidade() != null ? product.getCodModalidade() : null,


            // User Cadastro
            product.getIdUsuarioCadastro() != null ? product.getIdUsuarioCadastro() : null,
   

            // User Manutenção
            product.getIdUsuarioManutencao() != null ? product.getIdUsuarioManutencao() : null,


            // Family
            product.getCodFamilia() != null ? product.getCodFamilia(): null,


            // Especies
            product.getCodEspecie() != null ? product.getCodEspecie() : null,
    

            // Campos diretos
            product.getDesProduto(),
            product.getDesEmissorResponsavel(),
            product.getCodStatus(),
            product.getDesUtilizacao(),
            product.getDtCadastro(),
            product.getDtManutencao(),

            // Flags
            product.getFlgBloqFabricacao(),
            product.getFlgBloqVenda(),
            product.getFlgBloqDistribuicao(),
            product.getFlgBloqTroca(),
            product.getFlgBloqAquisicao(),
            product.getFlgBloqPedido(),
            product.getFlgBloqDevolucao(),
            product.getFlgInicializado(),
            product.getFlgComercializado(),

            // Entidade
            product.getCodEntidade()
        );
    }
}