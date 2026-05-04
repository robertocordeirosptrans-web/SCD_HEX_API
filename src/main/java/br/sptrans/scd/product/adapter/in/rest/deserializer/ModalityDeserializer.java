package br.sptrans.scd.product.adapter.in.rest.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import br.sptrans.scd.product.domain.Modality;

/**
 * Deserializador customizado para Modality.
 * 
 * Aceita tanto:
 * - Uma string contendo apenas o código: "MOD001"
 * - Um objeto JSON completo: {"codModalidade": "MOD001", "desModalidade": "...", ...}
 */
public class ModalityDeserializer extends StdDeserializer<Modality> {

    private static final long serialVersionUID = 1L;

    public ModalityDeserializer() {
        super(Modality.class);
    }

    @Override
    public Modality deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // Se for uma string simples, cria Modality apenas com o código
        if (node.isTextual()) {
            Modality modality = new Modality();
            modality.setCodModalidade(node.asText());
            return modality;
        }

        // Se for um objeto, tenta desserializar normalmente
        if (node.isObject()) {
            Modality modality = new Modality();
            
            if (node.has("codModalidade")) {
                modality.setCodModalidade(node.get("codModalidade").asText());
            }
            if (node.has("desModalidade")) {
                modality.setDesModalidade(node.get("desModalidade").asText());
            }
            if (node.has("codStatus")) {
                modality.setCodStatus(node.get("codStatus").asText());
            }
            
            return modality;
        }

        throw new IllegalArgumentException(
            "Modality deve ser uma string (código) ou um objeto JSON. Recebido: " + node.getNodeType()
        );
    }
}
