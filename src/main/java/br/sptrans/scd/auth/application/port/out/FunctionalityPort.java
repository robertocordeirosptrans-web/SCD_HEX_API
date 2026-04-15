package br.sptrans.scd.auth.application.port.out;

import java.util.Optional;

import org.springframework.data.domain.Page;

import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.FunctionalityKey;

public interface FunctionalityPort {

    Optional<Functionality> findById(FunctionalityKey id);

    Functionality save(Functionality functionality);

    void delete(FunctionalityKey id);

    void update(Functionality functionality);

    Page<Functionality> findAll(int page, int size);
}
