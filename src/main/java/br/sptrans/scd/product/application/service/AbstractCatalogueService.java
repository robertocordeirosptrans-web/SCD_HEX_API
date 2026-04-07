package br.sptrans.scd.product.application.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import br.sptrans.scd.product.domain.CatalogueEntity;

public abstract class AbstractCatalogueService<T extends CatalogueEntity<ID>, ID extends Serializable> {
    public abstract T create(T entity);
    public abstract T update(ID id, T entity);
    public abstract Optional<T> findById(ID id);
    public abstract List<T> findAll();
    public abstract void activate(ID id);
    public abstract void inactivate(ID id);
    public abstract void resolveUser(T entity);
}
