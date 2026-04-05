package br.sptrans.scd.shared.config;

import br.sptrans.scd.shared.idempotency.IdempotencyStore;
import br.sptrans.scd.shared.idempotency.InMemoryIdempotencyStore;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdempotencyConfig {
    @Bean
    public IdempotencyStore<CreateRequestResponse> idempotencyStore() {
        return new InMemoryIdempotencyStore<>();
    }
}
