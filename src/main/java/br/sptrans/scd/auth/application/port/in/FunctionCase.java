package br.sptrans.scd.auth.application.port.in;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.auth.adapter.in.rest.request.CreateFunctionalityRequest;
import br.sptrans.scd.auth.adapter.in.rest.request.DeactivateFunctionalityRequest;
import br.sptrans.scd.auth.adapter.in.rest.request.ReactivateFunctionalityRequest;
import br.sptrans.scd.auth.adapter.in.rest.request.UpdateFunctionalityRequest;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.FunctionalityKey;

public interface FunctionCase {
    // ══════════════════════════════════════════════════════════════════════════
    // FUNCIONALIDADES
    // ══════════════════════════════════════════════════════════════════════════
    /**
     * Cria uma nova funcionalidade. COD_FUNCIONALIDADE deve ser único.
     */
    Functionality createFunctionality(CreateFunctionalityRequest command);

    /**
     * Atualiza nome da funcionalidade. COD_FUNCIONALIDADE é imutável.
     */
    Functionality updateFunctionality(UpdateFunctionalityRequest command);

    Page<Functionality> listFunctionalities(Pageable pageable);

    Optional<Functionality> findById(FunctionalityKey key);

    /**
     * Inativa a funcionalidade (COD_STATUS = 'I'). Lança exceção se ainda houver
     * usuários com status Ativo vinculados.
     */
    void deactivateFunctionality(DeactivateFunctionalityRequest command);

    /**
     * Reativa uma funcionalidade inativa.
     */
    void reactivateFunctionality(ReactivateFunctionalityRequest command);

}
