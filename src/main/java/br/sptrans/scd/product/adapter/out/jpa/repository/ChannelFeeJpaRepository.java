package br.sptrans.scd.product.adapter.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.sptrans.scd.product.adapter.out.jpa.entity.ChannelFeeEntityJpa;
import br.sptrans.scd.product.adapter.out.jpa.entity.ChannelFeeKeyEntityJpa;

public interface ChannelFeeJpaRepository extends JpaRepository<ChannelFeeEntityJpa, ChannelFeeKeyEntityJpa>, JpaSpecificationExecutor<ChannelFeeEntityJpa> {
}
