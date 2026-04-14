package br.sptrans.scd.product.adapter.in.rest;

// ...

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.product.adapter.in.rest.dto.FareListResponseDTO;
import br.sptrans.scd.product.adapter.in.rest.dto.FareResponseDTO;
import br.sptrans.scd.product.adapter.in.rest.dto.FeeListResponseDTO;
import br.sptrans.scd.product.adapter.in.rest.dto.FeeResponseDTO;
import br.sptrans.scd.product.adapter.in.rest.dto.RegisterFareRequest;
import br.sptrans.scd.product.adapter.in.rest.dto.RegisterFeeRequest;
import br.sptrans.scd.product.adapter.in.rest.dto.UpdateFareRequest;
import br.sptrans.scd.product.adapter.in.rest.dto.UpdateFeeRequest;
import br.sptrans.scd.product.application.port.in.FeeFareManagementUseCase;
import br.sptrans.scd.shared.security.CadPermissions;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/fee-fares")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tarifas e Taxas v1", description = "Endpoints para gerenciamento de tarifas e taxas")
public class FeeFareController {

    private final FeeFareManagementUseCase feeFareManagementUseCase;

        @PostMapping("/tarifa")
        @PreAuthorize("hasAuthority('" + CadPermissions.FEE_CADTAR + "')")
    public ResponseEntity<FareResponseDTO> createFare(@Valid @RequestBody RegisterFareRequest request) {
        var command = new FeeFareManagementUseCase.RegisterFareCommand(
                request.codProduto(),
                request.codVersao(),
                request.codCanal(),
                request.desTarifa(),
                request.valTarifa(),
                request.dtInicio(),
                request.dtFim(),
                request.idUsuario());
        var created = feeFareManagementUseCase.createFare(command);
        // Map domain to DTO (ajuste conforme necessário)
        FareResponseDTO dto = FareResponseDTO.builder()
                .codTarifa(created.getCodTarifa())
                .codProduto(created.getCodProduto())
                .codVersao(created.getCodVersao())
                .desTarifa(created.getDesTarifa())
                .valTarifa(created.getValTarifa() != null ? new java.math.BigDecimal(created.getValTarifa()) : null)
                .dtVigenciaInicio(created.getDtVigenciaInicio())
                .dtVigenciaFim(created.getDtVigenciaFim())
                .dtCadastro(created.getDtCadastro())
                .dtManutencao(created.getDtManutencao())
                .codStatus(created.getCodStatus())
                .idUsuarioCadastro(created.getIdUsuarioCadastro())
                .idUsuarioManutencao(created.getIdUsuarioManutencao())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

        @PutMapping("/tarifa/{codTarifa}")
        @PreAuthorize("hasAuthority('" + CadPermissions.FEE_ATUTAR + "')")
    public ResponseEntity<FareResponseDTO> updateFare(@PathVariable String codTarifa,
            @Valid @RequestBody UpdateFareRequest request) {
        var command = new FeeFareManagementUseCase.UpdateFareCommand(
                request.desTarifa(),
                request.dtFim(),
                request.idUsuario());
        var updated = feeFareManagementUseCase.updateFare(codTarifa, command);
        FareResponseDTO dto = FareResponseDTO.builder()
                .codTarifa(updated.getCodTarifa())
                .codProduto(updated.getCodProduto())
                .codVersao(updated.getCodVersao())
                .desTarifa(updated.getDesTarifa())
                .valTarifa(updated.getValTarifa() != null ? new java.math.BigDecimal(updated.getValTarifa()) : null)
                .dtVigenciaInicio(updated.getDtVigenciaInicio())
                .dtVigenciaFim(updated.getDtVigenciaFim())
                .dtCadastro(updated.getDtCadastro())
                .dtManutencao(updated.getDtManutencao())
                .codStatus(updated.getCodStatus())
                .idUsuarioCadastro(updated.getIdUsuarioCadastro())
                .idUsuarioManutencao(updated.getIdUsuarioManutencao())
                .build();
        return ResponseEntity.ok(dto);
    }

        @GetMapping("/tarifas/{codProduto}/{codCanal}")
        @PreAuthorize("hasAuthority('" + CadPermissions.FEE_BUSTAR + "')")
    public ResponseEntity<FareListResponseDTO> listFares(@PathVariable String codProduto,
            @PathVariable String codCanal) {
        var fares = feeFareManagementUseCase.listFares(codProduto, codCanal);
        var dtoList = fares.stream().map(fare -> FareResponseDTO.builder()
                .codTarifa(fare.getCodTarifa())
                .codProduto(fare.getCodProduto())
                .codVersao(fare.getCodVersao())
                .desTarifa(fare.getDesTarifa())
                .valTarifa(fare.getValTarifa() != null ? new java.math.BigDecimal(fare.getValTarifa()) : null)
                .dtVigenciaInicio(fare.getDtVigenciaInicio())
                .dtVigenciaFim(fare.getDtVigenciaFim())
                .dtCadastro(fare.getDtCadastro())
                .dtManutencao(fare.getDtManutencao())
                .codStatus(fare.getCodStatus())
                .idUsuarioCadastro(fare.getIdUsuarioCadastro())
                .idUsuarioManutencao(fare.getIdUsuarioManutencao())
                .build()).toList();
        return ResponseEntity.ok(FareListResponseDTO.builder().fares(dtoList).build());
    }

    // Taxas (Fee)

        @PostMapping("/taxa")
        @PreAuthorize("hasAuthority('" + CadPermissions.FEE_CADTAX + "')")
    public ResponseEntity<FeeResponseDTO> createFee(@RequestBody RegisterFeeRequest request) {
        var adm = request.taxaAdministrativa();
        var admCmd = new FeeFareManagementUseCase.RegisterAdministrativeFeeCommand(
                new java.math.BigDecimal(adm.recInicial()),
                new java.math.BigDecimal(adm.recFinal()),
                adm.valFixo(),
                adm.valPercentual());
        var srv = request.taxaServico();
        var srvCmd = new FeeFareManagementUseCase.RegisterServiceFeeCommand(
                new java.math.BigDecimal(srv.recInicial()),
                new java.math.BigDecimal(srv.recFinal()),
                srv.valFixo(),
                srv.valPercentual(),
                srv.valMinimo());
        FeeFareManagementUseCase.RegisterDestinyFeeCommand dstCmd = null;
        if (request.taxaDestino() != null) {
            var dst = request.taxaDestino();
            dstCmd = new FeeFareManagementUseCase.RegisterDestinyFeeCommand(
                    dst.codCanalDestino());
        }
        var command = new FeeFareManagementUseCase.RegisterFeeCommand(
                request.codProduto(),
                request.codCanal(),
                request.desTaxa(),
                request.dtInicio(),
                request.dtFim(),
                admCmd,
                srvCmd,
                dstCmd);
        var created = feeFareManagementUseCase.createFee(command);
        FeeResponseDTO dto = FeeResponseDTO.builder()
                .codTaxa(created.getCodTaxa())
                .codProduto(created.getCodProduto())
                .codCanal(created.getCodCanal())
                .desTaxa(created.getDesTaxa())
                .dtInicio(created.getDtInicio())

                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

        @PutMapping("/taxa/{codTaxa}")
        @PreAuthorize("hasAuthority('" + CadPermissions.FEE_ATUTAX + "')")
    public ResponseEntity<FeeResponseDTO> updateFee(@PathVariable Long codTaxa, @RequestBody UpdateFeeRequest request) {
        var adm = request.taxaAdministrativa();
        var admCmd = new FeeFareManagementUseCase.RegisterAdministrativeFeeCommand(
                new java.math.BigDecimal(adm.recInicial()),
                new java.math.BigDecimal(adm.recFinal()),
                adm.valFixo(),
                adm.valPercentual());
        var srv = request.taxaServico();
        var srvCmd = new FeeFareManagementUseCase.RegisterServiceFeeCommand(
                new java.math.BigDecimal(srv.recInicial()),
                new java.math.BigDecimal(srv.recFinal()),
                srv.valFixo(),
                srv.valPercentual(),
                srv.valMinimo());
        var command = new FeeFareManagementUseCase.UpdateFeeCommand(
                request.desTaxa(),
                request.dtFim(),
                admCmd,
                srvCmd);
        var updated = feeFareManagementUseCase.updateFee(codTaxa, command);
        FeeResponseDTO dto = FeeResponseDTO.builder()
                .codTaxa(updated.getCodTaxa())
                .codProduto(updated.getCodProduto())
                .codCanal(updated.getCodCanal())
                .desTaxa(updated.getDesTaxa())
                .dtInicio(updated.getDtInicio())
                .build();
        return ResponseEntity.ok(dto);
    }

        @GetMapping("/taxas/{codProduto}/{codCanal}")
        @PreAuthorize("hasAuthority('" + CadPermissions.FEE_BUSTAX + "')")
    public ResponseEntity<FeeListResponseDTO> listFees(@PathVariable String codProduto, @PathVariable String codCanal) {
        var fees = feeFareManagementUseCase.listFees(codProduto, codCanal);
        var dtoList = fees.stream().map(fee -> FeeResponseDTO.builder()
                .codTaxa(fee.getCodTaxa())
                .codProduto(fee.getCodProduto())
                .codCanal(fee.getCodCanal())
                .desTaxa(fee.getDesTaxa())
                .dtInicio(fee.getDtInicio())
                .dtFim(fee.getDtFinal())
                .build()).toList();
        return ResponseEntity.ok(FeeListResponseDTO.builder().fees(dtoList).build());
    }

}
