package br.sptrans.scd.auth.adapter.in.rest.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import br.sptrans.scd.auth.domain.ClassificationPerson;

/**
 * Deserializador customizado para ClassificationPerson.
 * 
 * Aceita tanto:
 * - Uma string contendo apenas o código: "300"
 * - Um objeto JSON completo: {"codClassificacaoPessoa": "300", ...}
 */
public class ClassificationPersonDeserializer extends StdDeserializer<ClassificationPerson> {

    private static final long serialVersionUID = 1L;

    public ClassificationPersonDeserializer() {
        super(ClassificationPerson.class);
    }

    @Override
    public ClassificationPerson deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // Se for uma string simples, cria ClassificationPerson apenas com o código
        if (node.isTextual()) {
            ClassificationPerson cp = new ClassificationPerson();
            cp.setCodClassificacaoPessoa(node.asText());
            return cp;
        }

        // Se for um objeto, tenta desserializar normalmente
        if (node.isObject()) {
            ClassificationPerson cp = new ClassificationPerson();
            
            if (node.has("codClassificacaoPessoa")) {
                cp.setCodClassificacaoPessoa(node.get("codClassificacaoPessoa").asText());
            }
            if (node.has("desClassificacaoPessoa")) {
                cp.setDesClassificacaoPessoa(node.get("desClassificacaoPessoa").asText());
            }
            if (node.has("flgVenda")) {
                cp.setFlgVenda(node.get("flgVenda").asText());
            }
            if (node.has("stClassificacoesPessoa")) {
                cp.setStClassificacoesPessoa(node.get("stClassificacoesPessoa").asText());
            }
            
            return cp;
        }

        throw new IllegalArgumentException(
            "ClassificationPerson deve ser uma string (código) ou um objeto JSON. Recebido: " + node.getNodeType()
        );
    }
}
