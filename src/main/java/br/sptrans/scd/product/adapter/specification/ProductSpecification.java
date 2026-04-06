package br.sptrans.scd.product.adapter.specification;

import java.util.Map;

import org.springframework.data.jpa.domain.Specification;

import br.sptrans.scd.product.domain.Product;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {
    public static Specification<Product> filterChannels(Map<String, String> filters) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            for (Map.Entry<String, String> entry : filters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (value != null && !value.isEmpty()) {
                    switch (key) {
                        case "desProduto":
                            predicate = criteriaBuilder.and(predicate,
                                    criteriaBuilder.like(criteriaBuilder.lower(root.get("desProduto")),
                                            "%" + value.toLowerCase() + "%"));
                            break;
                        case "codStatus":
                            predicate = criteriaBuilder.and(predicate,
                                    criteriaBuilder.equal(root.get("codStatus"), value));
                            break;
                        case "codTipoProduto":
                            // Filtro por relacionamento
                            Join<Object, Object> tipoProdutoJoin = root.join("codTipoProduto", JoinType.INNER);
                            predicate = criteriaBuilder.and(predicate,
                                    criteriaBuilder.equal(tipoProdutoJoin.get("codTipoProduto"), value));
                            break;
                        case "idUsuarioCadastro":
                            // Filtro por relacionamento com User
                            Join<Object, Object> usuarioCadastroJoin = root.join("idUsuarioCadastro", JoinType.INNER);
                            predicate = criteriaBuilder.and(predicate,
                                    criteriaBuilder.equal(usuarioCadastroJoin.get("idUsuario"), Long.valueOf(value)));
                            break;
                        // Adicione outros casos conforme necessário
                    }
                }
            }

            return predicate;
        };
    }
}
