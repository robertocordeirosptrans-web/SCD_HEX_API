package br.sptrans.scd.initializedcards.adapter.port.out.jpa.mapper;

import org.springframework.stereotype.Component;

import br.sptrans.scd.initializedcards.adapter.port.out.persistence.entity.TbLotSCDEntityJpa;
import br.sptrans.scd.initializedcards.domain.TbLotSCD;

@Component
public class TbLotSCDMapper {
    public TbLotSCD toDomain(TbLotSCDEntityJpa entity) {
        if (entity == null) return null;
        return new TbLotSCD(
            entity.getIdLote(),
            entity.getStatus(),
            entity.getDtGeracao(),
            entity.getQtdCartoesLote(),
            entity.getCodTipoCartao()
        );
    }

    public TbLotSCDEntityJpa toEntity(TbLotSCD domain) {
        if (domain == null) return null;
        return new TbLotSCDEntityJpa(
            domain.getIdLote(),
            domain.getStatus(),
            domain.getDtGeracao(),
            domain.getQtdCartoesLote(),
            domain.getCodTipoCartao()
        );
    }
}
