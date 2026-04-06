package br.sptrans.scd.product.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.product.domain.Technology;

public interface TechnologyPort {
    Optional<Technology> findById(String codTecnologia);
    boolean existsById(String codTecnologia);
    List<Technology> findAll(String codStatus);
    Page<Technology> findAll(String codStatus, Pageable pageable);
    Technology save(Technology tecnologia);
    void updateStatus(String codTecnologia, String codStatus, Long idUsuario);
    void deleteById(String codTecnologia);
}
