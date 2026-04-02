package br.sptrans.scd.auth.application.port.out;

/**
 * Porto de saída agregador — mantido para compatibilidade retroativa.
 * <p>Estende as cinco interfaces segregadas por ISP, permitindo que adaptadores
 * existentes implementem apenas esta interface e que novos serviços injetem
 * somente a fatia de que precisam ({@link UserReader}, {@link UserWriter},
 * {@link AuthenticationRepository}, {@link UserStatusRepository} ou
 * {@link AuthorizationRepository}).</p>
 */
public interface UserRepository extends
        UserReader,
        UserWriter,
        AuthenticationRepository,
        UserStatusRepository,
        AuthorizationRepository {
    // Nenhum método adicional — todas as operações estão nas interfaces segregadas.
}
