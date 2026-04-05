package br.sptrans.scd.auth.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.auth.domain.User;

/**
 * Porta de Saída — Queries de User (responsabilidade: LEITURA). Contém todas as
 * operações de consulta sem efeitos colaterais.
 */
public interface UserQueryPort {

    Optional<User> findById(Long userId);

    Optional<User> findByCpf(String cpf);

    Object getUserProfile(Long userId);

    // Aliases com nomenclatura do domínio (COD_LOGIN, NOM_EMAIL)
    Optional<User> findByCodLogin(String codLogin);

    Optional<User> findByNomEmail(String nomEmail);


    boolean existsByLogin(String codLogin);

    List<User> findAllPaginated(String status, String nome, String email, String perfil,
            int offset, int limit, String sortBy, String sortDir);

    long countAll(String status, String nome, String email, String perfil);
}
