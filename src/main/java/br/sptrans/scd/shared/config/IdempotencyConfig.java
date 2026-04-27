package br.sptrans.scd.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestResponse;
import br.sptrans.scd.shared.idempotency.DatabaseIdempotencyStore;
import br.sptrans.scd.shared.idempotency.IdempotencyStore;
import br.sptrans.scd.shared.idempotency.IdempotencyTxHelper;

@Configuration
public class IdempotencyConfig {

    @Value("${idempotency.processing-timeout-minutes:5}")
    private int processingTimeoutMinutes;

    @Bean
    public IdempotencyStore<CreateRequestResponse> idempotencyStore(
            IdempotencyTxHelper txHelper,
            ObjectMapper objectMapper) {
        return new DatabaseIdempotencyStore<>(
                txHelper,
                objectMapper,
                new TypeReference<CreateRequestResponse>() {},
                processingTimeoutMinutes);
    }
}

