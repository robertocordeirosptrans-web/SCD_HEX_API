
package br.sptrans.scd.auth.adapter.out.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.out.jpa.mapper.GroupMapper;
import br.sptrans.scd.auth.adapter.out.jpa.repository.GroupCustomProjection;
import br.sptrans.scd.auth.adapter.out.jpa.repository.GroupJpaRepository;
import br.sptrans.scd.auth.adapter.out.jpa.repository.GroupProfileJpaRepository;
import br.sptrans.scd.auth.adapter.out.jpa.repository.GroupUserCustomProjection;
import br.sptrans.scd.auth.adapter.out.jpa.repository.GroupUserJpaRepository;
import br.sptrans.scd.auth.adapter.out.persistence.entity.GroupProfileEntityJpaId;
import br.sptrans.scd.auth.adapter.out.persistence.entity.GroupUserEntityJpaId;
import br.sptrans.scd.auth.application.port.out.GroupPort;
import br.sptrans.scd.auth.domain.Group;
import br.sptrans.scd.auth.domain.GroupProfile;
import br.sptrans.scd.auth.domain.GroupProfileKey;
import br.sptrans.scd.auth.domain.GroupUser;
import br.sptrans.scd.auth.domain.GroupUserKey;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
@SuppressWarnings("null")
public class GroupAdapterJpa implements GroupPort {

    private final GroupJpaRepository groupJpaRepository;
    private final GroupProfileJpaRepository groupProfileJpaRepository;
    private final GroupUserJpaRepository groupUserJpaRepository;
    private final GroupMapper groupMapper;

    @Override
    public Optional<Group> findById(String codGrupo) {
        return groupJpaRepository.findByCodGrupo(codGrupo)
                .map(groupMapper::toDomain);
    }

    @Override
    public Optional<GroupUser> findById(GroupUserKey id) {
        var idJpa = new GroupUserEntityJpaId();
        idJpa.setIdUsuario(id.getIdUsuario());
        idJpa.setCodGrupo(id.getCodGrupo());
        return groupUserJpaRepository.findById(idJpa)
                .map(groupMapper::toDomain);
    }

    @Override
    public boolean existsByCode(String codGrupo) {
        return groupJpaRepository.existsByCodGrupo(codGrupo);
    }

    public Page<Group> listGroups(String nomGrupo, String codStatus, Pageable pageable) {
        return groupJpaRepository.findByNomGrupoAndCodStatus(nomGrupo, codStatus, pageable).map(groupMapper::toDomain);
    }

    @Override
    public Page<GroupUserCustomProjection> listCustomUsersByGroup(String codGrupo, Pageable pageable) {
        return groupUserJpaRepository.findCustomUsersByGroup(codGrupo, pageable);
    }

    @Override
    public java.util.List<GroupUser> listGroupUsers() {
        return groupUserJpaRepository.findAll().stream()
                .map(groupMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupUser> listGroupUsersByCodGrupo(String codGrupo) {
        return groupUserJpaRepository.findAll().stream()
                .filter(e -> e.getId() != null && codGrupo.equals(e.getId().getCodGrupo())
                        && "A".equals(e.getCodStatus()))
                .map(groupMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Group grupo) {
        groupJpaRepository.save(groupMapper.toEntity(grupo));
    }

    @Override
    public void updateStatus(String codGrupo, String codStatus, Long idUsuarioManutencao) {
        groupJpaRepository.findByCodGrupo(codGrupo).ifPresent(entity -> {
            entity.setCodStatus(codStatus);
            entity.setIdUsuarioManutencao(idUsuarioManutencao);
            groupJpaRepository.save(entity);
        });
    }

    @Override
    public void associateProfilesToGroup(String codGrupo, String codPerfil, Long idUsuarioManutencao) {
        groupProfileJpaRepository.associateProfileToGroup(codGrupo, codPerfil, idUsuarioManutencao);
    }

    @Override
    public void disassociateProfileFromGroup(String codGrupo, String codPerfil, Long idUsuarioManutencao) {
        groupProfileJpaRepository.disassociateProfileFromGroup(codGrupo, codPerfil, idUsuarioManutencao);
    }

    @Override
    public boolean isProfileAssociate(String codGrupo, String codPerfil) {
        return groupProfileJpaRepository.countActiveProfileAssociation(codGrupo, codPerfil) > 0;
    }

    @Override
    public long countUserActive(String codGrupo) {
        return groupUserJpaRepository.countActiveUsersByGroup(codGrupo);
    }

    @Override
    public long count() {
        return groupUserJpaRepository.count();
    }

    @Override
    public Page<GroupUser> listGroupUsers(Pageable pageable) {
        return groupUserJpaRepository.findAllGroupUsers(pageable).map(groupMapper::toDomain);
    }

    @Override
    public GroupUser save(GroupUser entity) {
        groupUserJpaRepository.save(groupMapper.toEntity(entity));
        return entity;
    }

    @Override

    public void delete(GroupUser entity) {
        deleteById(entity.getId());
    }

    @Override

    public void deleteById(GroupUserKey id) {
        var idJpa = new GroupUserEntityJpaId();
        idJpa.setIdUsuario(id.getIdUsuario());
        idJpa.setCodGrupo(id.getCodGrupo());
        groupUserJpaRepository.deleteById(idJpa);
    }

    public long countGroupUsers() {
        return groupUserJpaRepository.count();
    }

    // Métodos do GroupProfileRepository
    @Override
    public Optional<GroupProfile> findByCodGrupoAndCodPerfil(String codGrupo, String codPerfil) {
        var id = new GroupProfileEntityJpaId();
        id.setCodGrupo(codGrupo);
        id.setCodPerfil(codPerfil);
        return groupProfileJpaRepository.findById(id)
                .map(groupMapper::toDomain);
    }

    @Override
    public List<GroupProfile> findByCodGrupoCodStatus(String codGrupo, String codStatus) {
        return groupProfileJpaRepository.findAll().stream()
                .filter(e -> e.getId() != null && codGrupo.equals(e.getId().getCodGrupo())
                        && codStatus.equals(e.getCodStatus()))
                .map(groupMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<GroupProfile> findByCodGrupoPerfil(GroupProfileKey id) {
        var idJpa = new GroupProfileEntityJpaId();
        idJpa.setCodGrupo(id.getCodGrupo());
        idJpa.setCodPerfil(id.getCodPerfil());
        return groupProfileJpaRepository.findById(idJpa)
                .map(groupMapper::toDomain);
    }

    @Override
    public List<GroupProfile> findAllGroupProfile() {
        return groupProfileJpaRepository.findAll().stream()
                .map(groupMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<GroupProfile> findAllGroupProfile(Pageable pageable) {
        return groupProfileJpaRepository.findAllWithUser(pageable).map(groupMapper::toDomain);
    }

    @Override
    public GroupProfile saveGroupProfile(GroupProfile entity) {
        groupProfileJpaRepository.save(groupMapper.toEntity(entity));
        return entity;
    }

    @Override
    public void deleteGroupProfile(GroupProfile entity) {
        deleteByIdGroupProfile(entity.getId());
    }

    @Override
    public void deleteByIdGroupProfile(GroupProfileKey id) {
        var idJpa = new GroupProfileEntityJpaId();
        idJpa.setCodGrupo(id.getCodGrupo());
        idJpa.setCodPerfil(id.getCodPerfil());
        groupProfileJpaRepository.deleteById(idJpa);
    }

    @Override
    public long countGroupProfile() {
        return groupProfileJpaRepository.count();
    }

    @Override
    public List<GroupUser> findById_IdUsuarioAndCodStatus(Long idUsuario, String codStatus) {
        return groupUserJpaRepository.findAll().stream()
                .filter(e -> e.getId() != null && idUsuario.equals(e.getId().getIdUsuario())
                        && codStatus.equals(e.getCodStatus()))
                .map(groupMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<GroupCustomProjection> listCustomGroupsByUser(Long idUsuario, Pageable pageable) {
        return groupUserJpaRepository.findGroupsByUserId(idUsuario, pageable);
    }

}
