package br.sptrans.scd.product.adapter.port.in.rest;

// ...

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.product.application.port.in.FeeFareManagementUseCase;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/fee-fares")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tarifas e Taxas v1", description = "Endpoints para gerenciamento de tarifas e taxas")
public class FeeFareController {

    private final FeeFareManagementUseCase feeFareManagementUseCase;


    @PostMapping("/tarifa")
    public ResponseEntity<?> createFare(@RequestBody FeeFareManagementUseCase.RegisterFareCommand command) {
        var created = feeFareManagementUseCase.createFare(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/tarifa/{codTarifa}")
    public ResponseEntity<?> updateFare(@PathVariable String codTarifa, @RequestBody FeeFareManagementUseCase.UpdateFareCommand command) {
        var updated = feeFareManagementUseCase.updateFare(codTarifa, command);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/tarifas/{codProduto}/{codCanal}")
    public ResponseEntity<?> listFares(@PathVariable String codProduto, @PathVariable String codCanal) {
        var fares = feeFareManagementUseCase.listFares(codProduto, codCanal);
        return ResponseEntity.ok(fares);
    }

    // Taxas (Fee)

    @PostMapping("/taxa")
    public ResponseEntity<?> createFee(@RequestBody FeeFareManagementUseCase.RegisterFeeCommand command) {
        var created = feeFareManagementUseCase.createFee(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/taxa/{codTaxa}")
    public ResponseEntity<?> updateFee(@PathVariable Long codTaxa, @RequestBody FeeFareManagementUseCase.UpdateFeeCommand command) {
        var updated = feeFareManagementUseCase.updateFee(codTaxa, command);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/taxas/{codProduto}/{codCanal}")
    public ResponseEntity<?> listFees(@PathVariable String codProduto, @PathVariable String codCanal) {
        var fees = feeFareManagementUseCase.listFees(codProduto, codCanal);
        return ResponseEntity.ok(fees);
    }

}
