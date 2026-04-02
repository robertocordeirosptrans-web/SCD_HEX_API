package br.sptrans.scd.auth.application.port.out;

import br.sptrans.scd.auth.domain.User;

/**
 * Porto de saída — escrita de usuários.
 * <p>Segregado conforme ISP: clientes que só gravam não precisam conhecer
 * operações de leitura ou status.</p>
 */
public interface UserWriter {

    User save(User user);

    void update(User usuario);
}
