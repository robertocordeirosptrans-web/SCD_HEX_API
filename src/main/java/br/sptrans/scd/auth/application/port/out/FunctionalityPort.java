package br.sptrans.scd.auth.application.port.out;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.FunctionalityKey;

public interface FunctionalityPort {

    Optional<Functionality> findById(FunctionalityKey id);

    Functionality save(Functionality functionality);

    void delete(FunctionalityKey id);

    void update(Functionality functionality);

    Page<Functionality> findAll(int page, int size);

    /**
     * Lista funcionalidades com filtros opcionais por codSistema, codModulo e nomFuncionalidade.
     * Os parâmetros são opcionais (null = sem filtro).
     */
    Page<Functionality> findWithFilters(String codSistema, String codModulo, String nomFuncionalidade, Pageable pageable);
}
