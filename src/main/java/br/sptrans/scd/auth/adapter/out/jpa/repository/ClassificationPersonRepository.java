package br.sptrans.scd.auth.adapter.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.sptrans.scd.auth.adapter.out.persistence.entity.ClassificationPersonEntity;

public interface ClassificationPersonRepository extends JpaRepository<ClassificationPersonEntity, String>, JpaSpecificationExecutor<ClassificationPersonEntity>{
    
}
