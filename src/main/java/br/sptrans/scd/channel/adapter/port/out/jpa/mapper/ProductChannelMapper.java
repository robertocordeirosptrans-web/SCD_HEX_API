package br.sptrans.scd.channel.adapter.port.out.jpa.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import br.sptrans.scd.channel.adapter.port.in.rest.dto.ProductChResponseDTO;
import br.sptrans.scd.channel.adapter.port.out.jpa.projection.ProductChannelProjection;

import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.channel.domain.ProductChannelKey;
import br.sptrans.scd.channel.adapter.port.out.jpa.entity.ProductChannelEntityJpa;
import br.sptrans.scd.channel.adapter.port.out.jpa.entity.ProductChannelKeyEntityJpa;

@Component
public class ProductChannelMapper {

    public ProductChannelEntityJpa toEntity(ProductChannel domain) {
        if (domain == null) return null;
        ProductChannelEntityJpa entity = new ProductChannelEntityJpa();
        entity.setId(toEntityKey(domain.getId()));
        entity.setQtdLimiteComercializacao(domain.getQtdLimiteComercializacao());
        entity.setQtdMinimaEstoque(domain.getQtdMinimaEstoque());
        entity.setQtdMaximaEstoque(domain.getQtdMaximaEstoque());
        entity.setQtdMinimaRessuprimento(domain.getQtdMinimaRessuprimento());
        entity.setQtdMaximaRessuprimento(domain.getQtdMaximaRessuprimento());
        entity.setCodOrgaoEmissor(domain.getCodOrgaoEmissor());
        entity.setVlFace(domain.getVlFace());
        entity.setCodStatus(domain.getCodStatus());
        // Adicione outros campos conforme necessário
        return entity;
    }

    public ProductChannel toDomain(ProductChannelEntityJpa entity) {
        if (entity == null) return null;
        ProductChannel domain = new ProductChannel();
        domain.setId(toDomainKey(entity.getId()));
        domain.setQtdLimiteComercializacao(entity.getQtdLimiteComercializacao());
        domain.setQtdMinimaEstoque(entity.getQtdMinimaEstoque());
        domain.setQtdMaximaEstoque(entity.getQtdMaximaEstoque());
        domain.setQtdMinimaRessuprimento(entity.getQtdMinimaRessuprimento());
        domain.setQtdMaximaRessuprimento(entity.getQtdMaximaRessuprimento());
        domain.setCodOrgaoEmissor(entity.getCodOrgaoEmissor());
        domain.setVlFace(entity.getVlFace());
        domain.setCodStatus(entity.getCodStatus());
        // Adicione outros campos conforme necessário
        return domain;
    }

    public ProductChannelKeyEntityJpa toEntityKey(ProductChannelKey key) {
        if (key == null) return null;
        return new ProductChannelKeyEntityJpa(key.getCodCanal(), key.getCodProduto());
    }

    public ProductChannelKey toDomainKey(ProductChannelKeyEntityJpa entityKey) {
        if (entityKey == null) return null;
        return new ProductChannelKey(entityKey.getCodCanal(), entityKey.getCodProduto());
    }
    public ProductChResponseDTO toResponseDTO(ProductChannelProjection projection) {
        if (projection == null) {
            return null;
        }

        // IMPORTANTE: A ordem dos parâmetros deve ser EXATAMENTE a mesma do Record
        return new ProductChResponseDTO(
                // 1-3: Informações básicas do Produto
                projection.getCodProduto(),
                projection.getDesProduto(),
                projection.getStatusProduto(),
                // 4-5: Informações do Canal
                projection.getCodCanal(),
                projection.getStatusCanalProduto(),
                // 6-15: Informações do Canal-Produto
                projection.getCodConvenio(),
                projection.getCodOrgaoEmissor(),
                projection.getQtdLimiteComercializacao(),
                projection.getQtdMinimaEstoque(),
                projection.getQtdMaximaEstoque(),
                projection.getQtdMinimaRessuprimento(),
                projection.getQtdMaximaRessuprimento(),
                projection.getVlFace(),
                projection.getTipoOperHM(),
                projection.getFlgCarac(),
                projection.getCanaisDestino(),
                // 16-18: Informações da Vigência
                projection.getInicioValidade(),
                projection.getFimValidade(),
                projection.getStatusVigencia(),
                // 19-24: Limites de Recarga
                projection.getDtInicioValidadeLimite(),
                projection.getDtFimValidadeLimite(),
                projection.getVlMinimoRecarga(),
                projection.getVlMaximoRecarga(),
                projection.getVlMaximoSaldo(),
                projection.getStatusLimite(),
                // 25-28: Informações de Taxas
                projection.getIdTaxa(),
                projection.getTaxaInicio(),
                projection.getTaxaFim(),
                projection.getDscTaxa(),
                // 29-32: Taxa Administrativa
                projection.getTaxaAdmRecInicial(),
                projection.getTaxaAdmRecFinal(),
                projection.getTaxaAdmValFixo(),
                projection.getTaxaAdmPercentual(),
                // 33-37: Taxa de Serviço
                projection.getTaxaServRecInicial(),
                projection.getTaxaServRecFinal(),
                projection.getTaxaServValFixo(),
                projection.getTaxaServPercentual(),
                projection.getTaxaServValMinimo(),
                // 38-42: Taxa por Canal
                projection.getTaxaCanalInicio(),
                projection.getTaxaCanalFim(),
                projection.getTaxaCanalVlInicio(),
                projection.getTaxaCanalVlFinal(),
                projection.getTaxaCanalPercentual()
        );
    }

    public List<ProductChResponseDTO> toResponseDTOList(List<ProductChannelProjection> projections) {
        if (projections == null || projections.isEmpty()) {
            return List.of();
        }

        return projections.stream()
                .map(this::toResponseDTO)
                .filter(dto -> dto != null)
                .toList();
    }
}
