

package br.sptrans.scd.auth.application.port.in;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.auth.adapter.in.rest.dto.ProfileFunctionalityProjectionDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.UserProfileProjectionDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.UserProfileResponseDTO;
import br.sptrans.scd.auth.adapter.out.jpa.repository.GroupCustomProjection;
import br.sptrans.scd.auth.adapter.out.jpa.repository.GroupUserCustomProjection;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.FunctionalityKey;
import br.sptrans.scd.auth.domain.Group;
import br.sptrans.scd.auth.domain.GroupUser;
import br.sptrans.scd.auth.domain.Profile;
import br.sptrans.scd.auth.domain.ProfileFunctionality;
import br.sptrans.scd.auth.domain.UserProfile;

/**
 * Porta de Entrada — Casos de uso de gestão de Grupos e Perfis.
 *
 * Regras de negócio cobertas: ─ Grupos possuem perfis; perfis possuem
 * funcionalidades. ─ Inativar um grupo NÃO inativa seus perfis (perfis são
 * compartilhados). ─ Inativar um perfil NÃO inativa suas funcionalidades. ─ Não
 * é possível inativar um grupo/perfil que ainda possui usuários ativos
 * vinculados. ─ COD_GRUPO e COD_PERFIL são únicos e imutáveis após criação. ─
 * Toda alteração registra ID_USUARIO_MANUTENCAO + DT_MANUTENCAO.
 */
public interface GroupProfileManagementUseCase {

        Page<GroupCustomProjection> listCustomGroupsByUser(Long idUsuario, Pageable pageable);

        // ══════════════════════════════════════════════════════════════════════════
        // GRUPOS
        // ══════════════════════════════════════════════════════════════════════════
        /**
         * Cria um novo grupo. COD_GRUPO deve ser único.
         */
        Group createGroup(CreateGroupCommand command);

        /**
         * Atualiza nome do grupo. COD_GRUPO é imutável.
         */
        Group updateGroup(UpdateGroupCommand command);

        /**
         * Inativa o grupo (COD_STATUS = 'I'). Lança exceção se ainda houver
         * usuários com status Ativo vinculados.
         */
        void deactivateGroup(DeactivateCommand command);

        /**
         * Reativa um grupo inativo.
         */
        void reactivateGroup(ReactivateCommand command);

        /**
         * Associa um ou mais perfis ao grupo (insere em GRUPO_PERFIS).
         */
        void associateProfilesToGroup(AssociateProfilesToGroupCommand command);

        /**
         * Remove a associação de um perfil do grupo (COD_STATUS = 'I' em
         * GRUPO_PERFIS).
         */
        void disassociateProfileFromGroup(DisassociateProfileFromGroupCommand command);

        /**
         * Lista todos os grupos, opcionalmente filtrado por status.
         */
        // List<Group> listGroups(String nomGrupo, String codStatus, Pageable pageable);

        Page<Group> listGroups(String nomGrupo, String codStatus, Pageable pageable);

        Page<GroupUserCustomProjection> listCustomUsersByGroup(String codGrupo, Pageable pageable);

        Optional<Group> getGroupByCode(String codGrupo);

        Optional<Group> findById(String codGrupo);

        // ══════════════════════════════════════════════════════════════════════════
        // PERFIS
        // ══════════════════════════════════════════════════════════════════════════
        /**
         * Cria um novo perfil. COD_PERFIL deve ser único.
         */
        Profile createProfile(CreateProfileCommand command);

        /**
         * Atualiza nome do perfil. COD_PERFIL é imutável.
         */
        Profile updateProfile(UpdateProfileCommand command);

        /**
         * Inativa o perfil (COD_STATUS = 'I'). Lança exceção se ainda houver
         * usuários com status Ativo vinculados diretamente.
         */
        void deactivateProfile(DeactivateCommand command);

        /**
         * Reativa um perfil inativo.
         */
        void reactivateProfile(ReactivateCommand command);

        /**
         * Associa funcionalidades ao perfil (insere em PERFIL_FUNCIONALIDADES).
         */
        void associateFunctionalitiesToProfile(AssociateFunctionalitiesToProfileCommand command);

        /**
         * Remove a associação de uma funcionalidade do perfil.
         */
        void disassociateFunctionalityFromProfile(DisassociateFunctionalityFromProfileCommand command);

        /**
         * Lista todos os perfis, opcionalmente filtrado por status.
         */
        List<Profile> listProfiles(String statusCode);

        Page<Profile> listProfiles(String nomPerfil, String statusCode, Pageable pageable);

        /**
         * Lista funcionalidades disponíveis para vinculação (todas ativas).
         */
        List<Functionality> listFunctionalities();

        /**
         * Lista funcionalidades disponíveis para vinculação (todas ativas).
         */
        // Page<ProfileFunctionality> listFunctionalitiesByProfile(String codPerfil,
        // Pageable pageable);

        Page<ProfileFunctionalityProjectionDTO> listFunctionalitiesProjectionByProfile(
                        String codPerfil, Pageable pageable);

        // ══════════════════════════════════════════════════════════════════════════
        // ASSOCIAÇÕES
        // ══════════════════════════════════════════════════════════════════════════
        /**
         * Lista todas as associações grupo-usuário.
         */
        List<GroupUser> listGroupUsers();

        Page<GroupUser> listGroupUsers(Pageable pageable);

        /**
         * Lista todas as associações grupo-usuário de um grupo específico (apenas
         * ativos).
         */
        List<GroupUser> listGroupUsersByCodGrupo(String codGrupo);

        /**
         * Lista todas as associações usuário-perfil.
         */
        List<UserProfile> listUserProfiles();

        Page<UserProfile> listUserProfiles(Pageable pageable);

        Page<UserProfileProjectionDTO> listUserProfilesByPerfil(String codPerfil, Pageable pageable);

        Page<UserProfileResponseDTO> listProfilesByUsuario(Long idUsuario, Pageable pageable);

                /**
         * Cria uma nova associação usuário-perfil, se não existir ativa.
         */
        void createUserProfileAssociation(Long idUsuario, String codPerfil, Long idUsuarioManutencao);

        /**
         * Atualiza a validade (dtFimValidade) da associação usuário-perfil.
         * 
         * @param idUsuario           ID do usuário
         * @param codPerfil           Código do perfil
         * @param idUsuarioManutencao Usuário que está realizando a manutenção
         * @param ativar              true para ativar (1 ano à frente), false para
         *                            inativar (data atual)
         */
        void updateUserProfileValidity(Long idUsuario, String codPerfil, Long idUsuarioManutencao, boolean ativar);

        /**
         * Lista todas as associações perfil-funcionalidade.
         */
        List<ProfileFunctionality> listProfileFunctionalities();

        Page<ProfileFunctionality> listProfileFunctionalities(Pageable pageable);

        // ══════════════════════════════════════════════════════════════════════════
        // Commands
        // ══════════════════════════════════════════════════════════════════════════
        record CreateGroupCommand(
                        String codGrupo,
                        String nomGrupo,
                        Long idUsuarioLogado) {

        }

        record UpdateGroupCommand(
                        String codGrupo,
                        String nomGrupo,
                        Long idUsuarioLogado) {

        }

        record CreateProfileCommand(
                        String codPerfil,
                        String nomPerfil,
                        Long idUsuarioLogado) {

        }

        record UpdateProfileCommand(
                        String codPerfil,
                        String nomPerfil,
                        Long idUsuarioLogado) {

        }

        record DeactivateCommand(
                        String code, // codGrupo or codPerfil
                        Long idUsuarioLogado) {

        }

        record ReactivateCommand(
                        String code,
                        Long idUsuarioLogado) {

        }

        record AssociateProfilesToGroupCommand(
                        String groupCode,
                        Set<String> profileCodes,
                        Long idUsuarioLogado) {

        }

        record DisassociateProfileFromGroupCommand(
                        String groupCode,
                        String profileCode,
                        Long idUsuarioLogado) {

        }

        record AssociateFunctionalitiesToProfileCommand(
                        String codPerfil,
                        Set<Functionality> functionalities,
                        Long idUsuarioLogado) {

        }

        record DisassociateFunctionalityFromProfileCommand(
                        String codPerfil,
                        FunctionalityKey functionality,
                        Long idUsuarioLogado) {

        }

        // record FunctionalityKey(
        // String codSistema,
        // String codModulo,
        // String codRotina,
        // String codFuncionalidade) {

        // }

        // ══════════════════════════════════════════════════════════════════════════
        // Domain Exceptions
        // ══════════════════════════════════════════════════════════════════════════
        class GroupProfileManagementException extends RuntimeException {

                private final ErrorType type;

                public GroupProfileManagementException(ErrorType type, String message) {
                        super(message);
                        this.type = type;
                }

                public ErrorType getType() {
                        return type;
                }
        }

        enum ErrorType {
                CODE_ALREADY_EXISTS,
                NOT_FOUND,
                ALREADY_INACTIVE,
                ALREADY_ACTIVE,
                HAS_ACTIVE_USERS,
                PROFILE_ALREADY_ASSOCIATED,
                PROFILE_NOT_ASSOCIATED,
                FUNCTIONALITY_ALREADY_ASSOCIATED,
                FUNCTIONALITY_NOT_FOUND
        }
}
