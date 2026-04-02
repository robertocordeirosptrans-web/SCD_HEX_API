package br.sptrans.scd.auth.application.port.out;

import java.util.Set;

import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.Profile;

/**
 * Porto de saída — carregamento de perfis e funcionalidades (autorização).
 * <p>Segregado conforme ISP: isola as consultas de autorização — usadas
 * apenas no momento de construção do contexto de segurança — das demais
 * operações de leitura e escrita do usuário.</p>
 */
public interface AuthorizationRepository {

    /**
     * Carrega as funcionalidades efetivas do usuário combinando:
     * <ol>
     *   <li>Perfil → funcionalidades (via {@code USUARIO_PERFIS} ativos)</li>
     *   <li>Grupo → perfil → funcionalidades (via {@code GRUPO_PERFIS} ativos)</li>
     *   <li>Funcionalidades diretas ({@code USUARIO_FUNCIONALIDADES} ativas)</li>
     * </ol>
     */
    Set<Functionality> carregarFuncionalidadesEfetivas(Long idUsuario);

    /**
     * Carrega os perfis ativos vinculados ao usuário
     * (via {@code USUARIO_PERFIS} e {@code GRUPO_PERFIS}).
     */
    Set<Profile> carregarPerfisEfetivos(Long idUsuario);
}
