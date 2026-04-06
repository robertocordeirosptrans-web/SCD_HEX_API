package br.sptrans.scd.auth.adapter.port.out.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.application.port.out.AuditLogRepository;
import br.sptrans.scd.shared.helper.LogSanitizer;

@Component
public class AuditLogAdapter implements AuditLogRepository {

    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT");

    @Override
    public void logLoginSuccess(String username, String ipAddress) {
        auditLog.info("event=LOGIN_SUCCESS user={} ip={}", LogSanitizer.maskLogin(username), ipAddress);
    }

    @Override
    public void logLoginFailed(String username, String ipAddress, String reason) {
        auditLog.warn("event=LOGIN_FAILED user={} ip={} reason={}", LogSanitizer.maskLogin(username), ipAddress, reason);
    }

    @Override
    public void logAccountBlocked(String username, int attempts) {
        auditLog.error("event=ACCOUNT_BLOCKED user={} attempts={}", LogSanitizer.maskLogin(username), attempts);
    }
}