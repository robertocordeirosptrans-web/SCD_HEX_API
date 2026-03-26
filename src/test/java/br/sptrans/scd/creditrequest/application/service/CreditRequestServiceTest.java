package br.sptrans.scd.creditrequest.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestCredit;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestResponse;


@SpringBootTest
@ActiveProfiles("local")
class CreditRequestServiceTest {

    @Autowired
    private CreditRequestService service;

    @Test
    void testCreateCreditRequest_doesNotSaveToDatabase() {
        // Arrange
        String idempotencyKey = "test-key";
        var now = java.time.LocalDateTime.now();
        CreateRequestCredit.CreditRequest pedido = new CreateRequestCredit.CreditRequest(
                115168L,
                "23"
        );
        CreateRequestCredit.ItemRequest item = new CreateRequestCredit.ItemRequest(
                9000000000021593L,
                "212809436",
                "701",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(10),
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
        CreateRequestCredit request = new CreateRequestCredit(
                "10",
                "115168",
                now,
                LocalDateTime.of(2026, 3, 27, 14, 30, 0),
                "Teste do SCD",
                List.of(pedido),
                List.of(item)
        );


        // Act
        CreateRequestResponse response = service.createCreditRequest(request, idempotencyKey);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.pedidosProcessados().size());
        assertEquals(0, response.pedidosRejeitados().size());
   
    }
}
