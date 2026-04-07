package br.sptrans.scd.product.domain;

import java.io.Serializable;

public interface CatalogueEntity<ID extends Serializable> extends Serializable {
    ID getId();
    void setId(ID id);
    boolean isActive();
    void setActive(boolean active);
}
