package br.sptrans.scd.auth.adapter.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;

import br.sptrans.scd.auth.adapter.in.rest.dto.UserFilterRequestDTO;
import br.sptrans.scd.auth.adapter.out.persistence.entity.UserEntityJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.UserProfileJpa;
import br.sptrans.scd.auth.domain.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;

public class UserSpecification {

    public static Specification<UserEntityJpa> filterUsers(UserFilterRequestDTO filtro) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filtro == null) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            if (filtro.nomUsuario() != null && !filtro.nomUsuario().isBlank()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nomUsuario")), "%" + filtro.nomUsuario().toLowerCase() + "%"));
            }

            if (filtro.nomEmail() != null && !filtro.nomEmail().isBlank()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nomEmail")), "%" + filtro.nomEmail().toLowerCase() + "%"));
            }

            if (filtro.codStatus() != null && !filtro.codStatus().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("codStatus"), filtro.codStatus()));
            }

            if (filtro.codPerfil() != null && !filtro.codPerfil().isBlank()) {
                Subquery<Long> sub = query.subquery(Long.class);
                var upRoot = sub.from(UserProfileJpa.class);
                sub.select(upRoot.get("id").get("idUsuario"))
                   .where(
                       criteriaBuilder.equal(upRoot.get("id").get("idUsuario"), root.get("idUsuario")),
                       criteriaBuilder.equal(upRoot.get("id").get("codPerfil"), filtro.codPerfil()),
                       criteriaBuilder.equal(upRoot.get("codStatus"), "A")
                   );
                predicates.add(criteriaBuilder.exists(sub));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<User> filterUsers(Map<String, String> filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters == null || filters.isEmpty()) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            filters.forEach((key, value) -> {
                if (value == null || value.isBlank()) {
                    return;
                }
                switch (key) {
                    case "codLogin":
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("codLogin")), "%" + value.toLowerCase() + "%"));
                        break;
                    case "nomUsuario":
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nomUsuario")), "%" + value.toLowerCase() + "%"));
                        break;
                    case "nomEmail":
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nomEmail")), "%" + value.toLowerCase() + "%"));
                        break;
                    case "codStatus":
                        predicates.add(criteriaBuilder.equal(root.get("codStatus"), value));
                        break;
                    default:
                        break;
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
