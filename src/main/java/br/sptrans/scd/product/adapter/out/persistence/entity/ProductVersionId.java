package br.sptrans.scd.product.adapter.out.persistence.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Chave composta para ProductVersionEntityJpa.
 * Permite múltiplas versões com o mesmo código (ex: versão "1") para diferentes produtos.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProductVersionId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "COD_VERSAO", nullable = false, length = 20)
    private String codVersao;

    @Column(name = "COD_PRODUTO", nullable = false, length = 20)
    private String codProduto;
}
