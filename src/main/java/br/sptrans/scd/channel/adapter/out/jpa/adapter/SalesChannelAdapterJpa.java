
package br.sptrans.scd.channel.adapter.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.application.port.out.ClassificationPort;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.adapter.in.rest.dto.SubSalesChannelProjection;
import br.sptrans.scd.channel.adapter.out.jpa.mapper.SalesChannelMapper;
import br.sptrans.scd.channel.adapter.out.jpa.repository.SalesChannelJpaRepository;
import br.sptrans.scd.channel.adapter.out.persistence.entity.SalesChannelEntityJpa;
import br.sptrans.scd.channel.application.port.out.SalesChannelPersistencePort;
import br.sptrans.scd.channel.application.port.out.TypesActivityPersistencePort;
import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SalesChannelAdapterJpa implements SalesChannelPersistencePort {

    private final SalesChannelJpaRepository repository;
    private final SalesChannelMapper mapper;
    private final TypesActivityPersistencePort typesActivityPort;
    private final ClassificationPort classificationPort;
    private final UserResolverHelper userResolverHelper;

    private SalesChannel enrichDomain(SalesChannelEntityJpa entity, SalesChannel domain) {
        if (entity.getCodAtividade() != null) {
            typesActivityPort.findById(entity.getCodAtividade())
                    .ifPresent(domain::setCodAtividade);
        }
        if (entity.getCodClassificacaoPessoa() != null) {
            classificationPort.findById(entity.getCodClassificacaoPessoa())
                    .ifPresent(domain::setCodClassificacaoPessoa);
        }
        if (entity.getIdUsuarioCadastro() != null) {
            User userCadastro = userResolverHelper.resolve(entity.getIdUsuarioCadastro());
            if (userCadastro != null) {
                domain.setIdUsuarioCadastro(userCadastro);
            }
        }
        if (entity.getIdUsuarioManutencao() != null) {
            User userManutencao = userResolverHelper.resolve(entity.getIdUsuarioManutencao());
            if (userManutencao != null) {
                domain.setIdUsuarioManutencao(userManutencao);
            }
        }
        return domain;
    }

    @Override
    public Optional<SalesChannel> findById(String codCanal) {
        return repository.findByCodCanal(codCanal)
                .map(entity -> enrichDomain(entity, mapper.toDomain(entity)));
    }

    @Override
    public boolean existsById(String codCanal) {
        return repository.existsByCodCanal(codCanal);
    }

    @Override
    public Page<SalesChannel> findAll(Specification<SalesChannelEntityJpa> spec, Pageable pageable) {
        return repository.findAll(spec, pageable)
                .map(entity -> enrichDomain(entity, mapper.toDomain(entity)));
    }

    @Override
    public SalesChannel save(SalesChannel sc) {
        SalesChannelEntityJpa entity = mapper.toEntity(sc);
        SalesChannelEntityJpa saved = repository.save(entity);
        return enrichDomain(saved, mapper.toDomain(saved));
    }

    @Override
    public void updateStatus(String codCanal, String stCanais, User usuario) {
        Long idUsuario = usuario != null ? usuario.getIdUsuario() : null;
        repository.updateStatus(stCanais, idUsuario, codCanal);
    }

    @Override
    public void deleteById(String codCanal) {
        repository.deleteById(codCanal);
    }

    @Override
    public List<SalesChannel> findByCodCanalSuperior(String codCanalSuperior) {
        return repository.findByCodCanalSuperior(codCanalSuperior)
                .stream()
                .map(entity -> enrichDomain(entity, mapper.toDomain(entity)))
                .toList();
    }

    @Override
    public Page<SubSalesChannelProjection> findSubChannelsByCodCanalSuperior(String codCanalSuperior,
            Pageable pageable) {
        return repository.findSubChannelsByCodCanalSuperior(codCanalSuperior, pageable);
    }

}
