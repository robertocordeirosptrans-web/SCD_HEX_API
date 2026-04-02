package br.sptrans.scd.auth.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.auth.domain.User;

/**
 * Porto de saída — leitura de usuários.
 * <p>Segregado conforme ISP: clientes que só consultam não precisam conhecer
 * operações de escrita ou autenticação.</p>
 */
public interface UserReader {

    Optional<User> findById(Long id);

    Optional<User> findByCodLogin(String codLogin);

    Optional<User> findByNomEmail(String nomEmail);

    boolean existsByLogin(String codLogin);

    List<User> findAllPaginated(String status, String nome, String email, String perfil,
                                int offset, int limit, String sortBy, String sortDir);

    long countAll(String status, String nome, String email, String perfil);
}
