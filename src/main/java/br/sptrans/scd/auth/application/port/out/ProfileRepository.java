package br.sptrans.scd.auth.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.FunctionalityKey;
import br.sptrans.scd.auth.domain.Profile;
import br.sptrans.scd.auth.domain.ProfileFunctionality;
import br.sptrans.scd.auth.domain.UserProfile;


/**
 * Porta de Saída — repositório de Perfis e Funcionalidades.
 * Tabelas: SPTRANSDBA.PERFIS · PERFIL_FUNCIONALIDADES · FUNCIONALIDADES.
 */

public interface ProfileRepository {

    Optional<Profile> findById(String codPerfil);

    boolean existsByCode(String codPerfil);

    List<Profile> listProfile(String codStatus);

    void save(Profile perfil);

    void updateStatus(String codPerfil, String codStatus, Long idUsuarioManutencao);

    // ── Associações PERFIL_FUNCIONALIDADES ────────────────────────────────────
    /**
     * Insere linha em PERFIL_FUNCIONALIDADES com COD_STATUS = 'A'.
     */
    void associateFunctionalitiesToProfile(String codPerfil, FunctionalityKey chave, Long idUsuarioManutencao);

    /**
     * Inativa associação em PERFIL_FUNCIONALIDADES (COD_STATUS = 'I'). Não
     * deleta — mantém histórico de quando a funcionalidade foi removida.
     */
    void desassociateFunctionalitiesToProfile(String codPerfil, FunctionalityKey chave, Long idUsuarioManutencao);

    boolean isFunctionalityAssociate(String codPerfil, FunctionalityKey chave);

    // ── Funcionalidades disponíveis ───────────────────────────────────────────
    List<Functionality> listFunctionalityActive();

    boolean isFunctionality(FunctionalityKey chave);

    // ── Verificação de dependências ───────────────────────────────────────────
    /**
     * Conta usuários ativos vinculados diretamente ao perfil via
     * USUARIO_PERFIS.
     */
    long countUserActive(String codPerfil);

    /**
     * Lista todas as associações usuário-perfil.
     */
    List<UserProfile> listUserProfiles();

    /**
     * Lista todas as associações perfil-funcionalidade.
     */
    List<ProfileFunctionality> listProfileFunctionalities();

}
