package br.sptrans.scd.product.adapter.in.rest;

// ...

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.product.adapter.in.rest.dto.RegisterFareRequest;
import br.sptrans.scd.product.adapter.in.rest.dto.RegisterFeeRequest;
import br.sptrans.scd.product.adapter.in.rest.dto.UpdateFareRequest;
import br.sptrans.scd.product.adapter.in.rest.dto.UpdateFeeRequest;
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
    public ResponseEntity<?> createFare(@RequestBody RegisterFareRequest request) {
        var command = new FeeFareManagementUseCase.RegisterFareCommand(
            request.codProduto(),
            request.codVersao(),
            request.codCanal(),
            request.desTarifa(),
            request.valTarifa(),
            request.dtInicio(),
            request.dtFim(),
            request.idUsuario()
        );
        var created = feeFareManagementUseCase.createFare(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @PostMapping("/tarifa/{codTarifa}")
    public ResponseEntity<?> updateFare(@PathVariable String codTarifa, @RequestBody UpdateFareRequest request) {
        var command = new FeeFareManagementUseCase.UpdateFareCommand(
            request.desTarifa(),
            request.dtFim(),
            request.idUsuario()
        );
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
    public ResponseEntity<?> createFee(@RequestBody RegisterFeeRequest request) {
        var adm = request.taxaAdministrativa();
        var admCmd = new FeeFareManagementUseCase.RegisterAdministrativeFeeCommand(
            new java.math.BigDecimal(adm.recInicial()),
            new java.math.BigDecimal(adm.recFinal()),
            adm.valFixo(),
            adm.valPercentual()
        );
        var srv = request.taxaServico();
        var srvCmd = new FeeFareManagementUseCase.RegisterServiceFeeCommand(
            new java.math.BigDecimal(srv.recInicial()),
            new java.math.BigDecimal(srv.recFinal()),
            srv.valFixo(),
            srv.valPercentual(),
            srv.valMinimo()
        );
        FeeFareManagementUseCase.RegisterDestinyFeeCommand dstCmd = null;
        if (request.taxaDestino() != null) {
            var dst = request.taxaDestino();
            dstCmd = new FeeFareManagementUseCase.RegisterDestinyFeeCommand(
                dst.codCanalDestino()
            );
        }
        var command = new FeeFareManagementUseCase.RegisterFeeCommand(
            request.codProduto(),
            request.codCanal(),
            request.desTaxa(),
            request.dtInicio(),
            request.dtFim(),
            admCmd,
            srvCmd,
            dstCmd
        );
        var created = feeFareManagementUseCase.createFee(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @PostMapping("/taxa/{codTaxa}")
    public ResponseEntity<?> updateFee(@PathVariable Long codTaxa, @RequestBody UpdateFeeRequest request) {
        var adm = request.taxaAdministrativa();
        var admCmd = new FeeFareManagementUseCase.RegisterAdministrativeFeeCommand(
            new java.math.BigDecimal(adm.recInicial()),
            new java.math.BigDecimal(adm.recFinal()),
            adm.valFixo(),
            adm.valPercentual()
        );
        var srv = request.taxaServico();
        var srvCmd = new FeeFareManagementUseCase.RegisterServiceFeeCommand(
            new java.math.BigDecimal(srv.recInicial()),
            new java.math.BigDecimal(srv.recFinal()),
            srv.valFixo(),
            srv.valPercentual(),
            srv.valMinimo()
        );
        var command = new FeeFareManagementUseCase.UpdateFeeCommand(
            request.desTaxa(),
            request.dtFim(),
            admCmd,
            srvCmd
        );
        var updated = feeFareManagementUseCase.updateFee(codTaxa, command);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/taxas/{codProduto}/{codCanal}")
    public ResponseEntity<?> listFees(@PathVariable String codProduto, @PathVariable String codCanal) {
        var fees = feeFareManagementUseCase.listFees(codProduto, codCanal);
        return ResponseEntity.ok(fees);
    }

}
