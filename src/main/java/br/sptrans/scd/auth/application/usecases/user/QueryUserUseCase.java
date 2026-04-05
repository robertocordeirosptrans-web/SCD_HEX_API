package br.sptrans.scd.auth.application.usecases.user;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.application.port.in.UserManagementUseCase;
import br.sptrans.scd.auth.application.port.out.UserQueryPort;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Use Case — Consultar Informações de Usuário
 * 
 * Responsável por: - Listar usuários com paginação e filtros - Contar usuários
 * - Buscar usuário por ID com contexto completo
 * 
 * Portos utilizados: - Output Port: UserReader — buscar e listar usuários
 */
@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QueryUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(QueryUserUseCase.class);

    private final UserQueryPort userReader;

    /**
     * Lista usuários com paginação, filtros e ordenação.
     * 
     * @param filtro critérios de filtro
     * @param page página (0-indexed)
     * @param size tamanho da página
     * @param sortBy campo para ordenação
     * @param sortDir direção (ASC/DESC)
     * @return lista de usuários
     */
    @Cacheable(value = "usuarios", keyGenerator = "listUsersPaginatedKeyGenerator")
    public List<User> listUsersPaginated(
        UserManagementUseCase.UserFilterRequest filtro,
        int page,
        int size,
        String sortBy,
        String sortDir) {
        
        log.debug("Listando usuários. Página: {}, Tamanho: {}, OrderBy: {} {}", 
            page, size, sortBy, sortDir);
        
        int offset = page * size;
        return userReader.findAllPaginated(
            filtro.codStatus(),
            filtro.nomUsuario(),
            filtro.nomEmail(),
            filtro.codPerfil(),
            offset,
            size,
            sortBy,
            sortDir);
    }

    /**
     * Conta total de usuários com os filtros aplicados.
     * 
     * @param filtro critérios de filtro
     * @return total de registros
     */
    @Cacheable(value = "usuarios", keyGenerator = "countUsersKeyGenerator")
    public long countUsers(UserManagementUseCase.UserFilterRequest filtro) {
        log.debug("Contando usuários. Filtros: {}", filtro);
        
        return userReader.countAll(
            filtro.codStatus(),
            filtro.nomUsuario(),
            filtro.nomEmail(),
            filtro.codPerfil());
    }

    /**
     * Busca usuário por ID com contexto completo (perfis, grupos,
     * funcionalidades).
     * 
     * @param idUsuario ID do usuário
     * @return usuário encontrado
     * @throws ResourceNotFoundException se usuário não encontrado
     */
    public User findById(Long idUsuario) {
        log.debug("Buscando usuário por ID: {}", idUsuario);
        
        return userReader.findById(idUsuario)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado. ID: {}", idUsuario);
                    return new ResourceNotFoundException("Usuário", "id", idUsuario);
                });
    }
}
