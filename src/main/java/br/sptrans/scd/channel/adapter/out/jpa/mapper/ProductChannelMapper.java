package br.sptrans.scd.channel.adapter.out.jpa.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.adapter.in.rest.dto.ProductChResponseDTO;
import br.sptrans.scd.channel.adapter.out.persistence.entity.ProductChannelEntityJpa;
import br.sptrans.scd.channel.adapter.out.persistence.entity.ProductChannelKeyEntityJpa;
import br.sptrans.scd.channel.application.port.out.query.ProductChannelProjection;
import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.channel.domain.ProductChannelKey;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductChannelMapper {

    // Mapeamento do domínio para o DTO de resposta
    default ProductChResponseDTO toResponseDTO(ProductChannel channel) {
        if (channel == null || channel.getId() == null) return null;
        Integer codCanal = null;
        try {
            codCanal = channel.getId().getCodCanal() != null ? Integer.valueOf(channel.getId().getCodCanal()) : null;
        } catch (NumberFormatException e) {}
        return new ProductChResponseDTO(
            channel.getId().getCodProduto(), // String
            null, // desProduto
            channel.getCodStatus() != null ? channel.getCodStatus().getCode() : null,
            codCanal, // Integer
            null, // statusCanalProduto
            channel.getCodConvenio(),
            channel.getCodOrgaoEmissor(),
            channel.getQtdLimiteComercializacao(),
            channel.getQtdMinimaEstoque(),
            channel.getQtdMaximaEstoque(),
            channel.getQtdMinimaRessuprimento(),
            channel.getQtdMaximaRessuprimento(),
            channel.getVlFace(),
            channel.getCodTipoOperHM(),
            channel.getFlgCarac(),
            null, // canaisDestino
            null, // inicioValidade
            null, // fimValidade
            null, // statusVigencia
            null, // dtInicioValidadeLimite
            null, // dtFimValidadeLimite
            null, // vlMinimoRecarga
            null, // vlMaximoRecarga
            null, // vlMaximoSaldo
            null, // statusLimite
            null, // idTaxa
            null, // taxaInicio
            null, // taxaFim
            null, // dscTaxa
            null, // taxaAdmRecInicial
            null, // taxaAdmRecFinal
            null, // taxaAdmValFixo
            null, // taxaAdmPercentual
            null, // taxaServRecInicial
            null, // taxaServRecFinal
            null, // taxaServValFixo
            null, // taxaServPercentual
            null, // taxaServValMinimo
            null, // taxaCanalInicio
            null, // taxaCanalFim
            null, // taxaCanalVlInicio
            null, // taxaCanalVlFinal
            null  // taxaCanalPercentual
        );
    }

    @Mapping(source = "idUsuarioCadastro.idUsuario", target = "idUsuarioCadastro")
    @Mapping(source = "idUsuarioManutencao.idUsuario", target = "idUsuarioManutencao")
    ProductChannelEntityJpa toEntity(ProductChannel domain);

    @Mapping(source = "entity.codStatus", target = "codStatus")
    @Mapping(source = "userCad", target = "idUsuarioCadastro")
    @Mapping(source = "userMan", target = "idUsuarioManutencao")
    ProductChannel toDomain(ProductChannelEntityJpa entity, User userCad, User userMan);

    ProductChannelKeyEntityJpa toEntityKey(ProductChannelKey key);

    ProductChannelKey toDomainKey(ProductChannelKeyEntityJpa entityKey);

    ProductChResponseDTO toResponseDTO(ProductChannelProjection projection);

    List<ProductChResponseDTO> toResponseDTOList(List<ProductChannelProjection> projections);

    default ChannelDomainStatus stringToChannelDomainStatus(String code) {
        if (code == null || code.isBlank()) return null;
        return ChannelDomainStatus.fromCode(code);
    }

    default String channelDomainStatusToString(ChannelDomainStatus status) {
        if (status == null) return null;
        return status.getCode();
    }
}
