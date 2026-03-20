package br.sptrans.scd.auth.adapter.specification;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.adapter.port.in.rest.dto.UserFilterRequestDTO;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserSpecification {

    public static Specification<User> filterUsers(UserFilterRequestDTO filtro) {
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
                Join<Object, Object> usuarioPerfilJoin = root.join("perfisUsuario", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(usuarioPerfilJoin.get("id").get("codPerfil"), filtro.codPerfil()));
                predicates.add(criteriaBuilder.equal(usuarioPerfilJoin.get("codStatus"), "A"));
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
                    case "codPerfil":
                        Join<Object, Object> usuarioPerfilJoin = root.join("perfisUsuario", JoinType.LEFT);
                        predicates.add(criteriaBuilder.equal(usuarioPerfilJoin.get("id").get("codPerfil"), value));
                        predicates.add(criteriaBuilder.equal(usuarioPerfilJoin.get("codStatus"), "A"));
                        break;
                    default:
                        break;
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
