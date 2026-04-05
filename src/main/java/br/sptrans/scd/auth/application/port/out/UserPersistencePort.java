package br.sptrans.scd.auth.application.port.out;

/**
 * Porta de Saída — Agregador de persistência de User (Hexagonal Architecture).
 * <p>Combina todas as operações de User em um único contrato segregado por ISP:
 * <ul>
 *   <li>Leitura de dados (queries) - {@link UserQueryPort}</li>
 *   <li>Escrita de dados (commands) - {@link UserCommandPort}</li>
 *   <li>Autenticação (validação de credenciais) - {@link AuthenticationPort}</li>
 *   <li>Autorização (verificação de permissões) - {@link AuthorizationPort}</li>
 *   <li>Status (gerenciamento de estado) - {@link UserStatusPort}</li>
 * </ul>
 * </p>
 */
public interface UserPersistencePort extends
        UserQueryPort,
        UserCommandPort,
        AuthenticationPort,
        UserStatusPort,
        AuthorizationPort {
    
    // Nenhum método adicional
    // Todos os métodos estão nas interfaces segregadas acima
}