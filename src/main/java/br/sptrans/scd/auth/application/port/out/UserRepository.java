package br.sptrans.scd.auth.application.port.out;

import java.util.Optional;
import java.util.Set;

import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.User;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByCodLogin(String codLogin);

    Optional<User> findByNomEmail(String nomEmail);

    void atualizarTentativasEStatus(Long idUsuario, int numTentativas, String codStatus);

    void atualizarUltimoAcesso(Long idUsuario);

    /**
     * Carrega as funcionalidades efetivas do usuário combinando três fontes: 1.
     * PERFIL_FUNCIONALIDADES onde USUARIO_PERFIS.COD_STATUS = 'Ativo' 2.
     * PERFIL_FUNCIONALIDADES via GRUPO_PERFIS onde GRUPO_USUARIOS.COD_STATUS =
     * 'Ativo' 3. USUARIO_FUNCIONALIDADES diretas com COD_STATUS_USU_FUN =
     * 'Ativo'
     *
     * Implementado no adaptador JPA com JOIN otimizado.
     */
    Set<Functionality> carregarFuncionalidadesEfetivas(Long idUsuario);
}
