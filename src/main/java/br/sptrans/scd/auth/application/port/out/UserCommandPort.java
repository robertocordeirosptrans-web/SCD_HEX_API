package br.sptrans.scd.auth.application.port.out;

import br.sptrans.scd.auth.domain.User;

public interface UserCommandPort {

    User save(User user);

    void delete(Long userId);

    void update(User user);

    void updatePassword(Long userId, String hashedPassword);
}
