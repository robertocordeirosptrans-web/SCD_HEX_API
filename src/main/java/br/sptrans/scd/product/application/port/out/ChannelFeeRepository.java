package br.sptrans.scd.product.application.port.out;

import java.util.Optional;

import br.sptrans.scd.product.domain.ChannelFee;
import br.sptrans.scd.product.domain.ChannelFeeKey;

/**
 * Output Port para operações de persistência de TaxasScanal.
 *
 * Define o contrato que qualquer implementação de repositório deve seguir.
 * Abstrai a tecnologia de persistência da camada de domínio.
 */
public interface ChannelFeeRepository {
        /**
     * Busca uma taxa de sub-canal pelo ID composto.
     *
     * @param id ID composto (codCanal, codProduto)
     * @return Optional contendo a taxa ou vazio se não encontrada
     */
    Optional<ChannelFee> findById(ChannelFeeKey id);

    /**
     * Salva ou atualiza uma taxa de sub-canal.
     *
     * @param taxasScanal Taxa de sub-canal a ser salva
     * @return Taxa de sub-canal salva
     */
    ChannelFee save(ChannelFee taxasScanal);

    /**
     * Verifica se uma taxa de sub-canal existe pelo ID composto.
     *
     * @param id ID composto
     * @return true se existe, false caso contrário
     */
    boolean existsByKey(ChannelFeeKey id);
}
