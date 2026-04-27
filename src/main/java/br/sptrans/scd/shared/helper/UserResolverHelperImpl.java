package br.sptrans.scd.shared.helper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.application.port.out.UserPersistencePort;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.shared.exception.AuthenticationFailedException;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserResolverHelperImpl implements UserResolverHelper {

    private final UserPersistencePort userPort;

    @Override
    public User resolve(Long userId) {
        if (userId == null) return null;
        return userPort.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));
    }


    @Override
    public String getCurrentLogin() {
        if (isSchedulerContext()) {
            // Retorna login do usuário id 1 (ajuste conforme necessário)
            User user = userPort.findById(1L)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", 1L));
            return user.getCodLogin();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationFailedException("Usuário não autenticado");
        }
        return authentication.getName();
    }

    @Override
    public User getCurrentUser() {
        if (isSchedulerContext()) {
            return userPort.findById(1L)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", 1L));
        }
        String login = getCurrentLogin();
        return userPort.findByCodLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "login", login));
    }

    /**
     * Detecta se o contexto de chamada é um job agendado (sem SecurityContext).
     *
     * <p>Duas heurísticas em ordem de custo crescente:
     * <ol>
     *   <li>Nome da thread contém "scheduling" ou "scheduler" (padrão Spring).</li>
     *   <li>Stack trace contém alguma classe do pacote {@code .adapter.in.scheduler}
     *       (cobre todos os schedulers atuais e futuros sem necessidade de manutenção).</li>
     * </ol>
     */
    private boolean isSchedulerContext() {
        String threadName = Thread.currentThread().getName().toLowerCase();
        if (threadName.contains("scheduling") || threadName.contains("scheduler")) {
            return true;
        }
        for (StackTraceElement el : Thread.currentThread().getStackTrace()) {
            if (el.getClassName().contains(".adapter.in.scheduler.")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Long getCurrentUserId() {
        return getCurrentUser().getIdUsuario();
    }

    /**
     * Retorna o codEmpresa do usuário autenticado.
     */
    @Override
    public String getCurrentCodEmpresa() {
        return getCurrentUser().getCodEmpresa();
    }
}

