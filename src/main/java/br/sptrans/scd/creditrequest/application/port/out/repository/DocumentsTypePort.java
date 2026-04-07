package br.sptrans.scd.creditrequest.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.creditrequest.domain.DocumentsType;

public interface DocumentsTypePort {
    
    /**
     * Retorna todos os tipos de documento cadastrados.
     */
    List<DocumentsType> findAll();

    /**
     * Busca um único tipo de documento pelo código.
     */
    Optional<DocumentsType> findById(String codTipoDocumento);
}
