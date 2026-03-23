package br.sptrans.scd.channel.adapter.port.out.jpa.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import br.sptrans.scd.channel.adapter.port.in.rest.dto.ProductChResponseDTO;
import br.sptrans.scd.channel.adapter.port.out.jpa.projection.ProductChannelProjection;

@Component
public class ProductChannelMapper {

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
