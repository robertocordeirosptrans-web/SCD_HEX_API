package br.sptrans.scd.product.adapter.in.rest.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import br.sptrans.scd.product.domain.ProductType;

/**
 * Deserializador customizado para ProductType.
 * 
 * Aceita tanto:
 * - Uma string contendo apenas o código: "TIPO001"
 * - Um objeto JSON completo: {"codTipoProduto": "TIPO001", "desTipoProduto": "...", ...}
 */
public class ProductTypeDeserializer extends StdDeserializer<ProductType> {

    private static final long serialVersionUID = 1L;

    public ProductTypeDeserializer() {
        super(ProductType.class);
    }

    @Override
    public ProductType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // Se for uma string simples, cria ProductType apenas com o código
        if (node.isTextual()) {
            ProductType productType = new ProductType();
            productType.setCodTipoProduto(node.asText());
            return productType;
        }

        // Se for um objeto, tenta desserializar normalmente
        if (node.isObject()) {
            ProductType productType = new ProductType();
            
            if (node.has("codTipoProduto")) {
                productType.setCodTipoProduto(node.get("codTipoProduto").asText());
            }
            if (node.has("desTipoProduto")) {
                productType.setDesTipoProduto(node.get("desTipoProduto").asText());
            }
            if (node.has("codStatus")) {
                productType.setCodStatus(node.get("codStatus").asText());
            }
            
            return productType;
        }

        throw new IllegalArgumentException(
            "ProductType deve ser uma string (código) ou um objeto JSON. Recebido: " + node.getNodeType()
        );
    }
}
