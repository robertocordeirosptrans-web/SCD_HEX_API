package br.sptrans.scd.product.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.product.domain.Fee;

public interface FeeRepository {

    /**
     * Salva ou atualiza uma Taxa.
     *
     * @param taxa entidade a salvar
     * @return entidade salva
     */
    Fee save(Fee taxa);

    /**
     * Busca Taxa pelo ID.
     *
     * @param codTaxa código da taxa
     * @return Optional com a entidade se encontrada
     */
    Optional<Fee> findByIdFee(Long codTaxa);

    /**
     * Busca todas as Taxas de um canal.
     *
     * @param codCanal código do canal
     * @return lista de Taxas agrupadas por canal
     */
    List<Fee> findByCanal(String codCanal);

    /**
     * Busca todas as Taxas de um produto.
     *
     * @param codProduto código do produto
     * @return lista de Taxas agrupadas por produto
     */
    List<Fee> findByProduto(String codProduto);

    /**
     * Busca todas as Taxas de um canal-produto.
     *
     * @param codCanal código do canal
     * @param codProduto código do produto
     * @return lista de Taxas agrupadas por canal-produto
     */
    List<Fee> findByCanalProduto(String codCanal, String codProduto);

    /**
     * Busca Taxa ativa para um canal-produto numa data.
     *
     * @param codCanal código do canal
     * @param codProduto código do produto
     * @return Optional com a entidade ativa se encontrada
     */
    Optional<Fee> findAtivaByCanalProduto(String codCanal, String codProduto);

    /**
     * Busca Taxa ativa para um canal e produto (alias para
     * findAtivaByCanalProduto).
     *
     * @param codCanal código do canal
     * @param codProduto código do produto
     * @return Optional com a entidade ativa se encontrada
     */
    default Optional<Fee> findActiveByCanalAndProduto(String codCanal, String codProduto) {
        return findAtivaByCanalProduto(codCanal, codProduto);
    }


    /**
     * Verifica se uma Taxa existe.
     *
     * @param codTaxa código da taxa
     * @return true se existe, false caso contrário
     */
    boolean existsById(Long codTaxa);
}
