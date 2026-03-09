package br.sptrans.scd.product.application.port.out;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import br.sptrans.scd.product.domain.Fare;

public interface FareRepository {

    Optional<Fare> findById(String codTarifa);

    Fare save(Fare tarifa);

    void extendsValidity(String codTarifa, LocalDateTime dtFinal, Long idUsuario);

    /**
     * Lista tarifas de um produto/canal ordenadas por dtInicial. Se codCanal
     * for null, retorna todas do produto.
     */
    List<Fare> listByProductChannel(String codProduto, String codCanal);

    /**
     * Verifica se há sobreposição de vigência com outra tarifa para o mesmo
     * produto/canal, excluindo o próprio ID (para atualizações).
     */
    boolean isConflictValidity(String codProduto, String codCanal,
            LocalDateTime dtInicial, LocalDateTime dtFinal,
            Long excluirIdTaxa);

    /**
     * Busca a tarifa vigente para um produto/canal em uma data específica.
     */
    Optional<Fare> searchCurrent(String codProduto, String codCanal,
            LocalDateTime dataOperacao);
}
