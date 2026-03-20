package br.sptrans.scd.channel.adapter.port.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.sptrans.scd.channel.adapter.port.out.jpa.entity.TypesActivityEntityJpa;


public interface TypesActivityJpaRepository extends JpaRepository<TypesActivityEntityJpa, String>, JpaSpecificationExecutor<TypesActivityEntityJpa>{
    
}
