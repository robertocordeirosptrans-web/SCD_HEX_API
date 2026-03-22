package br.sptrans.scd.channel.adapter.port.out.jpa.mapper;

import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.channel.domain.ProductChannelKey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductChannelMapper {
    ProductChannelMapper INSTANCE = Mappers.getMapper(ProductChannelMapper.class);

    // Exemplo de mapeamento: adapte conforme necessário
    @Mapping(target = "id", source = "id")
    @Mapping(target = "canal", ignore = true)
    @Mapping(target = "produto", ignore = true)
    @Mapping(target = "idUsuarioCadastro", source = "idUsuarioCadastro")
    @Mapping(target = "idUsuarioManutencao", source = "idUsuarioManutencao")
    br.sptrans.scd.channel.domain.ProductChannel toDomain(br.sptrans.scd.channel.adapter.port.out.jpa.entity.ProductChannelEntityJpa entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "canal", ignore = true)
    @Mapping(target = "produto", ignore = true)
    @Mapping(target = "idUsuarioCadastro", source = "idUsuarioCadastro")
    @Mapping(target = "idUsuarioManutencao", source = "idUsuarioManutencao")
    br.sptrans.scd.channel.adapter.port.out.jpa.entity.ProductChannelEntityJpa toEntity(br.sptrans.scd.channel.domain.ProductChannel domain);

    // Conversão para chave de entidade JPA
    default br.sptrans.scd.channel.adapter.port.out.jpa.entity.ProductChannelKeyEntityJpa toEntityKey(br.sptrans.scd.channel.domain.ProductChannelKey key) {
        if (key == null) return null;
        return new br.sptrans.scd.channel.adapter.port.out.jpa.entity.ProductChannelKeyEntityJpa(key.getCodCanal(), key.getCodProduto());
    }
    // Conversão de chave de entidade JPA para domínio
    default br.sptrans.scd.channel.domain.ProductChannelKey toDomainKey(br.sptrans.scd.channel.adapter.port.out.jpa.entity.ProductChannelKeyEntityJpa key) {
        if (key == null) return null;
        return new br.sptrans.scd.channel.domain.ProductChannelKey(key.getCodCanal(), key.getCodProduto());
    }
}
