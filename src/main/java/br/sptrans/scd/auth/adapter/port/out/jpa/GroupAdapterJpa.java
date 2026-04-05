package br.sptrans.scd.auth.adapter.port.out.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.port.out.jpa.mapper.GroupMapper;
import br.sptrans.scd.auth.adapter.port.out.jpa.repository.GroupJpaRepository;
import br.sptrans.scd.auth.adapter.port.out.jpa.repository.GroupProfileJpaRepository;
import br.sptrans.scd.auth.adapter.port.out.jpa.repository.GroupUserJpaRepository;
import br.sptrans.scd.auth.adapter.port.out.persistence.entity.GroupProfileEntityJpa;
import br.sptrans.scd.auth.adapter.port.out.persistence.entity.GroupProfileEntityJpaId;
import br.sptrans.scd.auth.adapter.port.out.persistence.entity.GroupUserEntityJpa;
import br.sptrans.scd.auth.adapter.port.out.persistence.entity.GroupUserEntityJpaId;
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

    @Override
    public Optional<Group> findById(String codGrupo) {
        return groupJpaRepository.findByCodGrupo(codGrupo)
                .map(GroupMapper::toDomain);
    }

    @Override
    public boolean existsByCode(String codGrupo) {
        return groupJpaRepository.existsByCodGrupo(codGrupo);
    }

    @Override
    public List<Group> listGroups(String codStatus) {
        return groupJpaRepository.findAllByCodStatus(codStatus)
                .stream()
                .map(GroupMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Group grupo) {
        groupJpaRepository.save(GroupMapper.toEntity(grupo));
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
    public List<GroupUser> listGroupUsers() {
        return groupUserJpaRepository.findAllGroupUsers()
                .stream()
                .map(gu -> {
                    GroupUser groupUser = new GroupUser();
                    // Conversão explícita da chave composta
                    var idJpa = gu.getId();
                    groupUser.setId(
                            idJpa != null ? new GroupUserKey(idJpa.getIdUsuario(), idJpa.getCodGrupo()) : null
                    );
                    groupUser.setCodStatus(gu.getCodStatus());
                    groupUser.setIdUsuarioManutencao(gu.getIdUsuarioManutencao());
                    groupUser.setDtModi(gu.getDtManutencao());
                    return groupUser;
                })
                .collect(Collectors.toList());
    }

    // Métodos migrados do GroupUserAdapterJpa
    @Override
    public Optional<GroupUser> findById_IdUsuarioAndId_CodGrupo(Long idUsuario, String codGrupo) {
        var id = new GroupUserEntityJpaId();
        id.setIdUsuario(idUsuario);
        id.setCodGrupo(codGrupo);
        return groupUserJpaRepository.findById(id)
                .map(gu -> {
                    GroupUser groupUser = new GroupUser();
                    groupUser.setId(new GroupUserKey(idUsuario, codGrupo));
                    groupUser.setCodStatus(gu.getCodStatus());
                    groupUser.setIdUsuarioManutencao(gu.getIdUsuarioManutencao());
                    groupUser.setDtModi(gu.getDtManutencao());
                    return groupUser;
                });
    }

    @Override
    public List<GroupUser> findById_IdUsuarioAndCodStatus(Long idUsuario, String codStatus) {
        return groupUserJpaRepository.findAll().stream()
                .filter(gu -> gu.getId() != null && idUsuario.equals(gu.getId().getIdUsuario()) && codStatus.equals(gu.getCodStatus()))
                .map(gu -> {
                    GroupUser groupUser = new GroupUser();
                    groupUser.setId(new GroupUserKey(gu.getId().getIdUsuario(), gu.getId().getCodGrupo()));
                    groupUser.setCodStatus(gu.getCodStatus());
                    groupUser.setIdUsuarioManutencao(gu.getIdUsuarioManutencao());
                    groupUser.setDtModi(gu.getDtManutencao());
                    return groupUser;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupUser> findById_CodGrupoAndCodStatus(String codGrupo, String codStatus) {
        return groupUserJpaRepository.findAll().stream()
                .filter(gu -> gu.getId() != null && codGrupo.equals(gu.getId().getCodGrupo()) && codStatus.equals(gu.getCodStatus()))
                .map(gu -> {
                    GroupUser groupUser = new GroupUser();
                    groupUser.setId(new GroupUserKey(gu.getId().getIdUsuario(), gu.getId().getCodGrupo()));
                    groupUser.setCodStatus(gu.getCodStatus());
                    groupUser.setIdUsuarioManutencao(gu.getIdUsuarioManutencao());
                    groupUser.setDtModi(gu.getDtManutencao());
                    return groupUser;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupUser> findById_IdUsuario(Long idUsuario) {
        return groupUserJpaRepository.findAll().stream()
                .filter(gu -> gu.getId() != null && idUsuario.equals(gu.getId().getIdUsuario()))
                .map(gu -> {
                    GroupUser groupUser = new GroupUser();
                    groupUser.setId(new GroupUserKey(gu.getId().getIdUsuario(), gu.getId().getCodGrupo()));
                    groupUser.setCodStatus(gu.getCodStatus());
                    groupUser.setIdUsuarioManutencao(gu.getIdUsuarioManutencao());
                    groupUser.setDtModi(gu.getDtManutencao());
                    return groupUser;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<GroupUser> findById(GroupUserKey id) {
        return findById_IdUsuarioAndId_CodGrupo(id.getIdUsuario(), id.getCodGrupo());
    }

    public List<GroupUser> findAllGroupUsers() {
        return listGroupUsers();
    }

    @Override
    public GroupUser save(GroupUser entity) {
        // Salva ou atualiza usando o repository JPA
        var id = new GroupUserEntityJpaId();
        id.setIdUsuario(entity.getId().getIdUsuario());
        id.setCodGrupo(entity.getId().getCodGrupo());
        var groupUserEntity = new GroupUserEntityJpa();
        groupUserEntity.setId(id);
        groupUserEntity.setCodStatus(entity.getCodStatus());
        groupUserEntity.setIdUsuarioManutencao(entity.getIdUsuarioManutencao());
        groupUserEntity.setDtManutencao(entity.getDtModi());
        groupUserJpaRepository.save(groupUserEntity);
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
                .map(this::toDomainGroupProfile);
    }

    @Override
    public List<GroupProfile> findByCodGrupoCodStatus(String codGrupo, String codStatus) {
        return groupProfileJpaRepository.findAll().stream()
                .filter(e -> e.getId() != null && codGrupo.equals(e.getId().getCodGrupo()) && codStatus.equals(e.getCodStatus()))
                .map(this::toDomainGroupProfile)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<GroupProfile> findByCodGrupoPerfil(GroupProfileKey id) {
        var idJpa = new GroupProfileEntityJpaId();
        idJpa.setCodGrupo(id.getCodGrupo());
        idJpa.setCodPerfil(id.getCodPerfil());
        return groupProfileJpaRepository.findById(idJpa)
                .map(this::toDomainGroupProfile);
    }

    @Override
    public List<GroupProfile> findAllGroupProfile() {
        return groupProfileJpaRepository.findAll().stream()
                .map(this::toDomainGroupProfile)
                .collect(Collectors.toList());
    }

    @Override
    public GroupProfile saveGroupProfile(GroupProfile entity) {
        var id = new GroupProfileEntityJpaId();
        id.setCodGrupo(entity.getId().getCodGrupo());
        id.setCodPerfil(entity.getId().getCodPerfil());
        var groupProfileEntity = new GroupProfileEntityJpa();
        groupProfileEntity.setId(id);
        groupProfileEntity.setCodStatus(entity.getCodStatus());
        // Datas e outros campos podem ser ajustados conforme necessário
        groupProfileJpaRepository.save(groupProfileEntity);
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

    // Conversão manual entre entidade e domínio para GroupProfile
    private GroupProfile toDomainGroupProfile(GroupProfileEntityJpa entity) {
        if (entity == null) {
            return null;
        }
        var domain = new GroupProfile();
        var id = entity.getId();
        if (id != null) {
            domain.setId(new GroupProfileKey(id.getCodGrupo(), id.getCodPerfil()));
        }
        domain.setCodStatus(entity.getCodStatus());
        // Datas e objetos relacionados podem ser expandidos conforme necessário
        return domain;
    }

}
