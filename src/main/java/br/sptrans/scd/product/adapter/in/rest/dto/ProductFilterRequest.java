package br.sptrans.scd.product.adapter.in.rest.dto;

public record ProductFilterRequest(
    String desProduto,
    String codStatus,
    String codTipoProduto,
    String codCanal ,
    Boolean isExpired
) {
    public ProductFilterRequest {
        // Valores padrão para nulos
        desProduto = desProduto != null ? desProduto : "";
        codStatus = codStatus != null ? codStatus : "";
        codTipoProduto = codTipoProduto != null ? codTipoProduto : "";
        codCanal = codCanal != null ? codCanal : "";
        isExpired = isExpired != null ? isExpired : false;
    }
}
