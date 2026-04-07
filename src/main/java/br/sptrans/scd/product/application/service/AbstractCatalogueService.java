package br.sptrans.scd.product.application.service;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.product.domain.CatalogueEntity;

public abstract class AbstractCatalogueService<T extends CatalogueEntity<ID>, ID extends Serializable, CREATE_CMD, UPDATE_CMD> {
    public abstract T create(CREATE_CMD command);
    public abstract T update(ID id, UPDATE_CMD command);
    public abstract Optional<T> findById(ID id);
    public abstract Page<T> findAll(String codStatus, Pageable pageable);
    public abstract void activate(ID id, Long idUsuario);
    public abstract void inactivate(ID id, Long idUsuario);
    public abstract void delete(ID id);
}