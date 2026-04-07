package br.sptrans.scd.creditrequest.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.creditrequest.domain.Situation;

public interface SituationPort {
    /**
     * Retorna todas as situações cadastradas.
     */
    List<Situation> findAll();

    /**
     * Busca uma única situação pelo código.
     */
    Optional<Situation> findById(String codSituacao);
}
