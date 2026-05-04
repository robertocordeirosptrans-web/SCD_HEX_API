package br.sptrans.scd.product.adapter.in.rest.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import br.sptrans.scd.product.domain.Technology;

/**
 * Deserializador customizado para Technology.
 * 
 * Aceita tanto:
 * - Uma string contendo apenas o código: "TEC001"
 * - Um objeto JSON completo: {"codTecnologia": "TEC001", "desTecnologia": "...", ...}
 */
public class TechnologyDeserializer extends StdDeserializer<Technology> {

    private static final long serialVersionUID = 1L;

    public TechnologyDeserializer() {
        super(Technology.class);
    }

    @Override
    public Technology deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // Se for uma string simples, cria Technology apenas com o código
        if (node.isTextual()) {
            Technology technology = new Technology();
            technology.setCodTecnologia(node.asText());
            return technology;
        }

        // Se for um objeto, tenta desserializar normalmente
        if (node.isObject()) {
            Technology technology = new Technology();
            
            if (node.has("codTecnologia")) {
                technology.setCodTecnologia(node.get("codTecnologia").asText());
            }
            if (node.has("desTecnologia")) {
                technology.setDesTecnologia(node.get("desTecnologia").asText());
            }
            if (node.has("codStatus")) {
                technology.setCodStatus(node.get("codStatus").asText());
            }
            
            return technology;
        }

        throw new IllegalArgumentException(
            "Technology deve ser uma string (código) ou um objeto JSON. Recebido: " + node.getNodeType()
        );
    }
}
