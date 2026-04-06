package br.sptrans.scd.channel.adapter.port.out.jpa.converter;

import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ChannelStatusConverter implements AttributeConverter<ChannelDomainStatus, String> {
    @Override
    public String convertToDatabaseColumn(ChannelDomainStatus status) {
        return status != null ? status.getCode() : null;
    }

    @Override
    public ChannelDomainStatus convertToEntityAttribute(String dbData) {
        return dbData != null ? ChannelDomainStatus.fromCode(dbData) : null;
    }
}
