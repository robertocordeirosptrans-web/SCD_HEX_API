package br.sptrans.scd.product.adapter.in.rest.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import br.sptrans.scd.product.domain.Species;

/**
 * Deserializador customizado para Species.
 * 
 * Aceita tanto:
 * - Uma string contendo apenas o código: "ESP001"
 * - Um objeto JSON completo: {"codEspecie": "ESP001", "desEspecie": "...", ...}
 */
public class SpeciesDeserializer extends StdDeserializer<Species> {

    private static final long serialVersionUID = 1L;

    public SpeciesDeserializer() {
        super(Species.class);
    }

    @Override
    public Species deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // Se for uma string simples, cria Species apenas com o código
        if (node.isTextual()) {
            Species species = new Species();
            species.setCodEspecie(node.asText());
            return species;
        }

        // Se for um objeto, tenta desserializar normalmente
        if (node.isObject()) {
            Species species = new Species();
            
            if (node.has("codEspecie")) {
                species.setCodEspecie(node.get("codEspecie").asText());
            }
            if (node.has("desEspecie")) {
                species.setDesEspecie(node.get("desEspecie").asText());
            }
            if (node.has("codStatus")) {
                species.setCodStatus(node.get("codStatus").asText());
            }
            
            return species;
        }

        throw new IllegalArgumentException(
            "Species deve ser uma string (código) ou um objeto JSON. Recebido: " + node.getNodeType()
        );
    }
}
