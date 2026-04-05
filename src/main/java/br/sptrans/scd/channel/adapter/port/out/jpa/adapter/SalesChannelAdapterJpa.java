package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;

<<<<<<< HEAD
=======

>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
import java.util.List;
import java.util.Optional;
import br.sptrans.scd.channel.adapter.port.out.jpa.entity.SalesChannelEntityJpa;

import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.SalesChannelMapper;
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.SalesChannelJpaRepository;

import org.springframework.stereotype.Repository;

<<<<<<< HEAD
=======
import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.SalesChannelMapper;
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.SalesChannelJpaRepository;
import br.sptrans.scd.channel.adapter.port.out.persistence.entity.SalesChannelEntityJpa;
>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
import br.sptrans.scd.channel.application.port.out.SalesChannelRepository;
import br.sptrans.scd.channel.domain.SalesChannel;
import lombok.RequiredArgsConstructor;



@Repository
@RequiredArgsConstructor
public class SalesChannelAdapterJpa implements SalesChannelRepository {

<<<<<<< HEAD
    private final SalesChannelJpaRepository repository;
    private final SalesChannelMapper mapper;

    @Override
    public Optional<SalesChannel> findById(String codCanal) {
        return repository.findByCodCanal(codCanal)
=======
    private final SalesChannelJpaRepository jpaRepository;
    private final SalesChannelMapper mapper;



    @Override
    public Optional<SalesChannel> findById(String codCanal) {
        return jpaRepository.findByCodCanal(codCanal)
>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(String codCanal) {
<<<<<<< HEAD
        return repository.existsByCodCanal(codCanal);
=======
        return jpaRepository.existsByCodCanal(codCanal);
>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
    }

    @Override
    public List<SalesChannel> findAll(String stCanais) {
<<<<<<< HEAD
        List<SalesChannelEntityJpa> entities = repository.findAllByStCanais(stCanais);
=======
        List<SalesChannelEntityJpa> entities = jpaRepository.findAllByStCanais(stCanais);
>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public SalesChannel save(SalesChannel sc) {
        SalesChannelEntityJpa entity = mapper.toEntity(sc);
<<<<<<< HEAD
        SalesChannelEntityJpa saved = repository.save(entity);
=======
        SalesChannelEntityJpa saved = jpaRepository.save(entity);
>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
        return mapper.toDomain(saved);
    }

    @Override
    public void updateStatus(String codCanal, String stCanais, Long idUsuario) {
<<<<<<< HEAD
        repository.updateStatus(stCanais, idUsuario, codCanal);
=======
        jpaRepository.updateStatus(stCanais, idUsuario, codCanal);
>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
    }

    @Override
    public void deleteById(String codCanal) {
<<<<<<< HEAD
        repository.deleteById(codCanal);
    }

=======
        jpaRepository.deleteById(codCanal);
    }


    @Override
    public List<SalesChannel> findByCodCanalSuperior(String codCanalSuperior) {
        return jpaRepository.findByCodCanalSuperior(codCanalSuperior)
                .stream().map(mapper::toDomain).toList();
    }


>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
}
