package br.sptrans.scd.product.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.product.domain.Fee;

public interface FeePort {
    Fee save(Fee taxa);
    Optional<Fee> findByIdFee(Long codTaxa);
    List<Fee> findByCanal(String codCanal);
    List<Fee> findByProduto(String codProduto);
    List<Fee> findByCanalProduto(String codCanal, String codProduto);
    Optional<Fee> findAtivaByCanalProduto(String codCanal, String codProduto);
}
