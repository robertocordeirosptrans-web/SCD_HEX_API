
package br.sptrans.scd.product.adapter.specification;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import br.sptrans.scd.channel.adapter.out.persistence.entity.AgreementValidityEntityJpa;
import br.sptrans.scd.channel.adapter.out.persistence.entity.ProductChannelEntityJpa;
import br.sptrans.scd.product.adapter.in.rest.dto.ProductFilterRequest;
import br.sptrans.scd.product.adapter.out.persistence.entity.ProductEntityJpa;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class ProductSpecification {

    private static final String WILDCARD = "%";

    public static Specification<ProductEntityJpa> filterProducts(ProductFilterRequest filters) {
        if (filters == null) {
            return (root, query, cb) -> cb.conjunction();
        }

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            addStringPredicate(predicates, root, cb, filters.desProduto(), "desProduto", true);
            addEqualPredicate(predicates, root, cb, filters.codStatus(), "codStatus");
            addEqualPredicate(predicates, root, cb, filters.codTipoProduto(), "codTipoProduto");
            
            // Adicione outros campos conforme necessário

            return predicates.isEmpty()
                    ? cb.conjunction()
                    : cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    // Métodos auxiliares reutilizáveis
    private static void addStringPredicate(List<Predicate> predicates,
            Root<ProductEntityJpa> root,
            CriteriaBuilder cb,
            String value, String fieldName, boolean caseInsensitive) {
        if (!StringUtils.hasText(value)) {
            return;
        }
        if (caseInsensitive) {
            predicates.add(cb.like(cb.lower(root.get(fieldName)), WILDCARD + value.toLowerCase() + WILDCARD));
        } else {
            predicates.add(cb.like(root.get(fieldName), WILDCARD + value + WILDCARD));
        }
    }





    private static void addEqualPredicate(List<Predicate> predicates,
            Root<ProductEntityJpa> root,
            CriteriaBuilder cb,
            String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            return;
        }
        predicates.add(cb.equal(root.get(fieldName), value));
    }

    // Versão sobrecarregada para Map
    public static Specification<ProductEntityJpa> filterProducts(Map<String, String> filtersMap) {
        if (CollectionUtils.isEmpty(filtersMap)) {
            return filterProducts((ProductFilterRequest) null);
        }

        ProductFilterRequest request = new ProductFilterRequest(
                filtersMap.get("desProduto"),
                filtersMap.get("codStatus"),
                filtersMap.get("codTipoProduto"),
                filtersMap.get("codCanal"),
                filtersMap.get("isExpired") != null ? Boolean.parseBoolean(filtersMap.get("isExpired")) : null);
        return filterProducts(request);
    }

    // Método para combinar múltiplas specifications
    @SafeVarargs
    public static Specification<ProductEntityJpa> combine(Specification<ProductEntityJpa>... specifications) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (Specification<ProductEntityJpa> spec : specifications) {
                if (spec != null) {
                    Predicate predicate = spec.toPredicate(root, query, cb);
                    if (predicate != null) {
                        predicates.add(predicate);
                    }
                }
            }
            return predicates.isEmpty()
                    ? cb.conjunction()
                    : cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    public static Specification<ProductEntityJpa> combine(List<Specification<ProductEntityJpa>> specifications) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!CollectionUtils.isEmpty(specifications)) {
                for (Specification<ProductEntityJpa> spec : specifications) {
                    if (spec != null) {
                        Predicate predicate = spec.toPredicate(root, query, cb);
                        if (predicate != null) {
                            predicates.add(predicate);
                        }
                    }
                }
            }
            return predicates.isEmpty()
                    ? cb.conjunction()
                    : cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    /**
     * Filtra produtos cuja validade termina em até 3 meses a partir de agora (join
     * com AgreementValidityEntityJpa).
     */
    public static Specification<ProductEntityJpa> withValidityExpiringInNext3Months() {
        return (root, query, cb) -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime threeMonthsLater = now.plusMonths(3);
            Join<Object, AgreementValidityEntityJpa> agreementJoin = root.join("agreementValidityEntities");
            return cb.between(agreementJoin.get("dtFimValidade"), now, threeMonthsLater);
        };
    }

    /**
     * Filtra produtos ativos (codStatus = "A") para um determinado canal (join com
     * ProductChannelEntityJpa).
     */
    public static Specification<ProductEntityJpa> withActiveStatusForChannel(String codCanal) {
        return (root, query, cb) -> {
            Join<Object, ProductChannelEntityJpa> channelJoin = root.join("productChannelEntities");
            Predicate statusPredicate = cb.equal(channelJoin.get("codStatus"), "A");
            Predicate canalPredicate = cb.equal(channelJoin.get("id").get("codCanal"), codCanal);
            return cb.and(statusPredicate, canalPredicate);
        };
    }
}
