package br.sptrans.scd.shared.helper;

import br.sptrans.scd.auth.domain.User;

public interface UserResolverHelper {

    User resolve(Long userId);

    String getCurrentLogin();

    User getCurrentUser();

    Long getCurrentUserId();
}
