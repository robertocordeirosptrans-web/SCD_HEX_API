package br.sptrans.scd.channel.adapter.specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import br.sptrans.scd.channel.adapter.port.in.rest.dto.SalesChannelFilterRequest;
import br.sptrans.scd.channel.domain.SalesChannel;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class SalesChannelSpecification {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String WILDCARD = "%";

    public static Specification<SalesChannel> filterChannels(SalesChannelFilterRequest filters) {
        return (root, query, criteriaBuilder) -> {
            if (filters == null) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            addStringPredicate(predicates, root, criteriaBuilder,
                    filters.codDocumento(), "codDocumento", true);

            addEqualPredicate(predicates, root, criteriaBuilder,
                    filters.stCanais(), "stCanais");

            addNumericPredicate(predicates, root, criteriaBuilder,
                    filters.vlCaucao(), "vlCaucao");

            addDatePredicate(predicates, root, criteriaBuilder,
                    filters.dtInicioCaucao(), "dtInicioCaucao");

            addDatePredicate(predicates, root, criteriaBuilder,
                    filters.dtFimCaucao(), "dtFimCaucao");

            addStringPredicate(predicates, root, criteriaBuilder,
                    filters.codCanalSuperior(), "codCanalSuperior", true);

            return predicates.isEmpty()
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // Métodos auxiliares reutilizáveis
    private static void addStringPredicate(List<Predicate> predicates,
            Root<SalesChannel> root,
            CriteriaBuilder criteriaBuilder,
            String value, String fieldName, boolean caseInsensitive) {
        if (!StringUtils.hasText(value)) {
            return;
        }

        if (caseInsensitive) {
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(fieldName)),
                    WILDCARD + value.toLowerCase() + WILDCARD));
        } else {
            predicates.add(criteriaBuilder.like(
                    root.get(fieldName),
                    WILDCARD + value + WILDCARD));
        }
    }

    private static void addEqualPredicate(List<Predicate> predicates,
            Root<SalesChannel> root,
            CriteriaBuilder criteriaBuilder,
            String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            return;
        }
        predicates.add(criteriaBuilder.equal(root.get(fieldName), value));
    }

    private static void addNumericPredicate(List<Predicate> predicates,
            Root<SalesChannel> root,
            CriteriaBuilder criteriaBuilder,
            String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            return;
        }

        try {
            // Tenta converter para BigDecimal para busca exata
            BigDecimal numericValue = new BigDecimal(value);
            predicates.add(criteriaBuilder.equal(root.get(fieldName), numericValue));
        } catch (NumberFormatException e) {
            // Fallback: busca como string se não for número válido
            predicates.add(criteriaBuilder.like(
                    root.get(fieldName).as(String.class),
                    WILDCARD + value + WILDCARD));
        }
    }

    private static void addDatePredicate(List<Predicate> predicates,
            Root<SalesChannel> root,
            CriteriaBuilder criteriaBuilder,
            String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            return;
        }

        try {
            // Tenta converter para data para comparação exata
            LocalDate dateValue = LocalDate.parse(value, DATE_FORMATTER);
            predicates.add(criteriaBuilder.equal(root.get(fieldName), dateValue));
        } catch (DateTimeParseException e) {
            // Fallback: busca como string se não for data válida
            predicates.add(criteriaBuilder.like(
                    root.get(fieldName).as(String.class),
                    WILDCARD + value + WILDCARD));
        }
    }

    // Métodos específicos mais genéricos
    public static Specification<SalesChannel> withCodCanalSuperior(String codCanalSuperior) {
        return StringUtils.hasText(codCanalSuperior)
                ? (root, query, cb) -> cb.equal(root.get("codCanalSuperior"), codCanalSuperior)
                : null;
    }

    public static Specification<SalesChannel> withCodCanalLike(String codCanal) {
        return StringUtils.hasText(codCanal)
                ? (root, query, cb) -> cb.like(root.get("codCanal"), WILDCARD + codCanal + WILDCARD)
                : null;
    }

    public static Specification<SalesChannel> withDescricaoLike(String descricao) {
        return StringUtils.hasText(descricao)
                ? (root, query, cb) -> cb.like(
                        cb.upper(root.get("descricao")),
                        WILDCARD + descricao.toUpperCase() + WILDCARD)
                : null;
    }

    public static Specification<SalesChannel> withStatus(String status) {
        return StringUtils.hasText(status)
                ? (root, query, cb) -> cb.equal(root.get("status"), status)
                : null;
    }

    // Versão sobrecarregada para Map - CORRIGIDA
    public static Specification<SalesChannel> filterChannels(Map<String, String> filtersMap) {
        if (CollectionUtils.isEmpty(filtersMap)) {
            return filterChannels((SalesChannelFilterRequest) null);
        }

        // Usando o construtor original do SalesChannelFilterRequest
        SalesChannelFilterRequest request = new SalesChannelFilterRequest(
                filtersMap.get("codDocumento"),
                filtersMap.get("stCanais"),
                filtersMap.get("vlCaucao"),
                filtersMap.get("dtInicioCaucao"),
                filtersMap.get("dtFimCaucao"),
                filtersMap.get("codCanalSuperior"));

        return filterChannels(request);
    }

    // Método para combinar múltiplas specifications
    public static Specification<SalesChannel> combine(Specification<SalesChannel>... specifications) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (Specification<SalesChannel> spec : specifications) {
                if (spec != null) {
                    Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);
                    if (predicate != null) {
                        predicates.add(predicate);
                    }
                }
            }

            return predicates.isEmpty()
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // Método adicional: combinar lista de specifications
    public static Specification<SalesChannel> combine(List<Specification<SalesChannel>> specifications) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!CollectionUtils.isEmpty(specifications)) {
                for (Specification<SalesChannel> spec : specifications) {
                    if (spec != null) {
                        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);
                        if (predicate != null) {
                            predicates.add(predicate);
                        }
                    }
                }
            }

            return predicates.isEmpty()
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
