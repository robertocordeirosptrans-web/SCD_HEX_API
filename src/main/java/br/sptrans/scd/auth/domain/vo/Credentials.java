package br.sptrans.scd.auth.domain.vo;

import javax.security.auth.login.AccountLockedException;

import lombok.Value;

@Value
public class Credentials {

    private final String login;
    private final String passwordHash;
    private final String oldPasswordHash;
    private final Integer failedAttempts;

    public void recordFailedAttempt() {
        if (failedAttempts >= 3) {
            throw new AccountLockedException();
        }
    }

}
