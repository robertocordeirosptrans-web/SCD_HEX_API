package br.sptrans.scd.shared.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.cache.annotation.CacheEvict;

/**
 * Meta-annotation para garantir invalidação consistente de cache de usuários.
 * 
 * Invalida todos os caches relacionados a usuários quando um método de escrita
 * é executado, prevenindo dados desatualizados.
 * 
 * Uso:
 * <pre>
 * @InvalidateUserCache
 * public User createUser(CreateUserCommand cmd) { ... }
 * 
 * @InvalidateUserCache
 * public void updateAccessSchedule(UpdateScheduleCommand cmd) { ... }
 * </pre>
 * 
 * Caches invalidados:
 * - "usuarios": Cache de usuários (listagem, contagem)
 * - "permissoes": Cache de funcionalidades e perfis do usuário
 * - "user-contexts": Contexto de autenticação do usuário
 * - "user-permissions": Permissões efetivas do usuário
 * 
 * @author SCD Architecture Team
 * @since 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@CacheEvict(value = {"usuarios", "permissoes", "user-contexts", "user-permissions"}, allEntries = true)
public @interface InvalidateUserCache {
}
