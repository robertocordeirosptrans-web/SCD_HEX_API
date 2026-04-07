package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.product.adapter.out.jpa.mapper.FareMapper;
import br.sptrans.scd.product.adapter.out.jpa.repository.FareJpaRepository;
import br.sptrans.scd.product.adapter.out.persistence.entity.FareEntityJpa;
import br.sptrans.scd.product.application.port.out.repository.FarePort;
import br.sptrans.scd.product.domain.Fare;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor

public class FareAdapterJpa implements FarePort {

    private final FareJpaRepository fareJpaRepository;



    @Override
    public Optional<Fare> findById(String codTarifa) {
        return fareJpaRepository.findById(codTarifa)
                .map(FareMapper::toDomain);
    }

    @Override
    public Fare save(Fare fare) {
        FareEntityJpa entity = FareMapper.toEntity(fare);
        FareEntityJpa saved = fareJpaRepository.save(entity);
        return FareMapper.toDomain(saved);
    }


    @Override
    public void extendsValidity(String codTarifa, LocalDateTime dtFim, Long idUsuario) {
        fareJpaRepository.findById(codTarifa).ifPresent(entity -> {
            entity.setDtVigenciaFim(dtFim);
            entity.setDtManutencao(LocalDateTime.now());
            entity.setIdUsuarioManutencao(idUsuario);
            fareJpaRepository.save(entity);
        });
    }

    @Override
    public List<Fare> listByProductChannel(String codProduto, String codCanal) {

        return fareJpaRepository.findAll().stream()
                .filter(e -> e.getCodProduto() != null && e.getCodProduto().equals(codProduto))
                .sorted((a, b) -> a.getDtVigenciaInicio().compareTo(b.getDtVigenciaInicio()))
                .map(FareMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isConflictValidity(String codProduto, String codCanal,
            LocalDateTime dtInicio, LocalDateTime dtFim, Long excluirIdTaxa) {

        return fareJpaRepository.findAll().stream()
                .filter(e -> e.getCodProduto() != null && e.getCodProduto().equals(codProduto))
                .filter(e -> excluirIdTaxa == null || !e.getCodTarifa().equals(excluirIdTaxa.toString()))
                .anyMatch(e -> e.getDtVigenciaInicio().isBefore(dtFim)
                        && (e.getDtVigenciaFim() == null || e.getDtVigenciaFim().isAfter(dtInicio)));
    }

    @Override
    public Optional<Fare> searchCurrent(String codProduto, String codCanal,
            LocalDateTime dataOperacao) {

        return fareJpaRepository.findAll().stream()
                .filter(e -> e.getCodProduto() != null && e.getCodProduto().equals(codProduto))
                .filter(e -> e.getDtVigenciaInicio() != null && !e.getDtVigenciaInicio().isAfter(dataOperacao))
                .filter(e -> e.getDtVigenciaFim() == null || !e.getDtVigenciaFim().isBefore(dataOperacao))
                .sorted((a, b) -> b.getDtVigenciaInicio().compareTo(a.getDtVigenciaInicio()))
                .map(FareMapper::toDomain)
                .findFirst();
    }

   
}
