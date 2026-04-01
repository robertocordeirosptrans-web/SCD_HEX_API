package br.sptrans.scd.product.adapter.out.jpa.mapper;

import br.sptrans.scd.product.adapter.port.out.persistence.entity.ChannelFeeEntityJpa;
import br.sptrans.scd.product.adapter.port.out.persistence.entity.ChannelFeeKeyEntityJpa;
import br.sptrans.scd.product.domain.ChannelFee;
import br.sptrans.scd.product.domain.ChannelFeeKey;

public interface ChannelFeeMapper {
    static ChannelFee toDomain(ChannelFeeEntityJpa entity) {
        if (entity == null) return null;
        ChannelFee fee = new ChannelFee();
        fee.setId(toDomainKey(entity.getId()));
        fee.setVltInicio(entity.getVltInicio());
        fee.setVltFinal(entity.getVltFinal());
        fee.setVlPercentual(entity.getVlPercentual());
        fee.setDtInicio(entity.getDtInicio());
        fee.setDtFinal(entity.getDtFinal());
        fee.setDtManutencao(entity.getDtManutencao());
        // fee.setIdUsuarioManutencao(null); // relacionamento não mapeado
        return fee;
    }

    static ChannelFeeEntityJpa toEntity(ChannelFee fee) {
        if (fee == null) return null;
        ChannelFeeEntityJpa entity = new ChannelFeeEntityJpa();
        entity.setId(toEntityKey(fee.getId()));
        entity.setVltInicio(fee.getVltInicio());
        entity.setVltFinal(fee.getVltFinal());
        entity.setVlPercentual(fee.getVlPercentual());
        entity.setDtInicio(fee.getDtInicio());
        entity.setDtFinal(fee.getDtFinal());
        entity.setDtManutencao(fee.getDtManutencao());
        // entity.setIdUsuarioManutencao(null); // relacionamento não mapeado
        return entity;
    }

    static ChannelFeeKey toDomainKey(ChannelFeeKeyEntityJpa entityKey) {
        if (entityKey == null) return null;
        return new ChannelFeeKey(
            entityKey.getCodCanal(),
            entityKey.getCodProduto()
        );
    }

    static ChannelFeeKeyEntityJpa toEntityKey(ChannelFeeKey domainKey) {
        if (domainKey == null) return null;
        return new ChannelFeeKeyEntityJpa(
            domainKey.getCodCanal(),
            domainKey.getCodProduto()
        );
    }
}
