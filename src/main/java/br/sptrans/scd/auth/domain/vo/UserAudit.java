package br.sptrans.scd.auth.domain.vo;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.enums.UserStatus;
import lombok.Value;

@Value
public class UserAudit {

    private final UserStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final LocalDateTime lastAccess;
    private final LocalDateTime passwordExpiresAt;
    private final Long maintenanceUserId;
}
