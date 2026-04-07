package br.sptrans.scd.product.application.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import br.sptrans.scd.product.domain.CatalogueEntity;

public abstract class AbstractCatalogueService<T extends CatalogueEntity<ID>, ID extends Serializable> {
    public abstract T create(T entity, Long idUsuario);
    public abstract T update(ID id, T entity, Long idUsuario);
    public abstract Optional<T> findById(ID id); // sem idUsuario
    public abstract List<T> findAll(Long idUsuario);
    public abstract void activate(ID id, Long idUsuario);
    public abstract void inactivate(ID id, Long idUsuario);
}