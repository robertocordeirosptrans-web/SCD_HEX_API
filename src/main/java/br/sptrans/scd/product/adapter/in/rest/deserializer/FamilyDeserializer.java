package br.sptrans.scd.product.adapter.in.rest.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import br.sptrans.scd.product.domain.Family;

/**
 * Deserializador customizado para Family.
 * 
 * Aceita tanto:
 * - Uma string contendo apenas o código: "FAM001"
 * - Um objeto JSON completo: {"codFamilia": "FAM001", "desFamilia": "...", ...}
 */
public class FamilyDeserializer extends StdDeserializer<Family> {

    private static final long serialVersionUID = 1L;

    public FamilyDeserializer() {
        super(Family.class);
    }

    @Override
    public Family deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // Se for uma string simples, cria Family apenas com o código
        if (node.isTextual()) {
            Family family = new Family();
            family.setCodFamilia(node.asText());
            return family;
        }

        // Se for um objeto, tenta desserializar normalmente
        if (node.isObject()) {
            Family family = new Family();
            
            if (node.has("codFamilia")) {
                family.setCodFamilia(node.get("codFamilia").asText());
            }
            if (node.has("desFamilia")) {
                family.setDesFamilia(node.get("desFamilia").asText());
            }
            if (node.has("codStatus")) {
                family.setCodStatus(node.get("codStatus").asText());
            }
            
            return family;
        }

        throw new IllegalArgumentException(
            "Family deve ser uma string (código) ou um objeto JSON. Recebido: " + node.getNodeType()
        );
    }
}
