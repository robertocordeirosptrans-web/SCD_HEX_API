package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.product.adapter.out.jpa.mapper.FareMapper;
import br.sptrans.scd.product.adapter.out.jpa.repository.FareJpaRepository;
import br.sptrans.scd.product.adapter.port.out.persistence.entity.FareEntityJpa;
import br.sptrans.scd.product.application.port.out.repository.FareRepository;
import br.sptrans.scd.product.domain.Fare;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional
public class FareAdapterJpa implements FareRepository {

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
    public void extendsValidity(String codTarifa, LocalDateTime dtFinal, Long idUsuario) {
        fareJpaRepository.findById(codTarifa).ifPresent(entity -> {
            entity.setDtVigenciaFim(dtFinal);
            entity.setDtManutencao(LocalDateTime.now());
            entity.setIdUsuarioManutencao(idUsuario);
            fareJpaRepository.save(entity);
        });
    }

    @Override
    public List<Fare> listByProductChannel(String codProduto, String codCanal) {

        return fareJpaRepository.findAll().stream()
                .filter(e -> e.getCodProduto() != null && e.getCodProduto().equals(codProduto))
                .sorted((a, b) -> a.getDtVigenciaIni().compareTo(b.getDtVigenciaIni()))
                .map(FareMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isConflictValidity(String codProduto, String codCanal,
            LocalDateTime dtInicial, LocalDateTime dtFinal, Long excluirIdTaxa) {

        return fareJpaRepository.findAll().stream()
                .filter(e -> e.getCodProduto() != null && e.getCodProduto().equals(codProduto))
                .filter(e -> excluirIdTaxa == null || !e.getCodTarifa().equals(excluirIdTaxa.toString()))
                .anyMatch(e -> e.getDtVigenciaIni().isBefore(dtFinal)
                        && (e.getDtVigenciaFim() == null || e.getDtVigenciaFim().isAfter(dtInicial)));
    }

    @Override
    public Optional<Fare> searchCurrent(String codProduto, String codCanal,
            LocalDateTime dataOperacao) {

        return fareJpaRepository.findAll().stream()
                .filter(e -> e.getCodProduto() != null && e.getCodProduto().equals(codProduto))
                .filter(e -> e.getDtVigenciaIni() != null && !e.getDtVigenciaIni().isAfter(dataOperacao))
                .filter(e -> e.getDtVigenciaFim() == null || !e.getDtVigenciaFim().isBefore(dataOperacao))
                .sorted((a, b) -> b.getDtVigenciaIni().compareTo(a.getDtVigenciaIni()))
                .map(FareMapper::toDomain)
                .findFirst();
    }

   
}
