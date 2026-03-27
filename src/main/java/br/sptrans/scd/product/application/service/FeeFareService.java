package br.sptrans.scd.product.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.application.port.in.FeeFareManagementUseCase;
import br.sptrans.scd.product.application.port.out.repository.AdministrativeFeeRepository;
import br.sptrans.scd.product.application.port.out.repository.DestinyFeeRepository;
import br.sptrans.scd.product.application.port.out.repository.FareRepository;
import br.sptrans.scd.product.application.port.out.repository.FeeRepository;
import br.sptrans.scd.product.application.port.out.repository.ProductRepository;
import br.sptrans.scd.product.application.port.out.repository.ServiceFeeRepository;
import br.sptrans.scd.product.domain.AdministrativeFee;
import br.sptrans.scd.product.domain.DestinyFee;
import br.sptrans.scd.product.domain.Fare;
import br.sptrans.scd.product.domain.Fee;
import br.sptrans.scd.product.domain.Product;
import br.sptrans.scd.product.domain.ServiceFee;
import br.sptrans.scd.product.domain.enums.DomainStatus;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FeeFareService implements FeeFareManagementUseCase {

    private final FareRepository fareRepository;
    private final FeeRepository feeRepository;
    private final AdministrativeFeeRepository administrativeFeeRepository;
    private final ServiceFeeRepository serviceFeeRepository;
    private final DestinyFeeRepository destinyFeeRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // =========================================================================
    // Gestão de Tarifas (Fare)
    // =========================================================================

    @Override
    public Fare createFare(RegisterFareCommand command) {
        boolean conflict = fareRepository.isConflictValidity(
                command.codProduto(), command.codCanal(),
                command.dtInicial(), command.dtFinal(), null);
        if (conflict) {
            throw new ProductException(ProductErrorType.FARE_VALIDITY_CONFLICT);
        }

        User usuario = resolveUser(command.idUsuario());
        Product produto = productRepository.findById(command.codProduto())
                .orElseThrow(() -> new ProductException(ProductErrorType.PRODUCT_NOT_FOUND));

        Fare fare = new Fare(
                UUID.randomUUID().toString(),
                command.codVersao(),
                command.dtInicial(),
                command.dtFinal(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                command.dscTarifa(),
                DomainStatus.ACTIVE.getCode(),
                command.vlTarifa() != null ? command.vlTarifa().intValue() : null,
                usuario,
                null,
                produto
        );

        return fareRepository.save(fare);
    }

    @Override
    public Fare updateFare(String codTarifa, UpdateFareCommand command) {
        fareRepository.findById(codTarifa)
                .orElseThrow(() -> new ProductException(ProductErrorType.FARE_NOT_FOUND));

        fareRepository.extendsValidity(codTarifa, command.dtFinal(), command.idUsuario());

        return fareRepository.findById(codTarifa)
                .orElseThrow(() -> new ProductException(ProductErrorType.FARE_NOT_FOUND));
    }

    @Override
    public List<Fare> listFares(String codProduto, String codCanal) {
        return fareRepository.listByProductChannel(codProduto, codCanal);
    }

    // =========================================================================
    // Gestão de Taxas (Fee)
    // =========================================================================

    @Override
    public Fee createFee(RegisterFeeCommand command) {
        Fee fee = new Fee(
                null,
                command.dtInicial(),
                command.dscTaxa(),
                command.dtFinal(),
                command.codCanal(),
                command.codProduto(),
                null,
                null,
                null,
                null,
                null
        );

        Fee savedFee = feeRepository.save(fee);

        AdministrativeFee taxaAdm = new AdministrativeFee(
                null,
                command.taxaAdministrativa().recInicial().byteValue(),
                command.taxaAdministrativa().recFinal().byteValue(),
                command.taxaAdministrativa().valFixo(),
                command.taxaAdministrativa().valPercentual(),
                savedFee
        );
        administrativeFeeRepository.save(taxaAdm);

        ServiceFee taxaSrv = new ServiceFee(
                null,
                command.taxaServico().recInicial().byteValue(),
                command.taxaServico().recFinal().byteValue(),
                command.taxaServico().valFixo(),
                command.taxaServico().valPercentual(),
                command.taxaServico().valMinimo(),
                savedFee
        );
        serviceFeeRepository.save(taxaSrv);

        if (command.taxaDestino() != null) {
            DestinyFee taxaDes = new DestinyFee(null, command.taxaDestino().codCanalDestino());
            destinyFeeRepository.save(taxaDes);
        }

        return feeRepository.findByIdFee(savedFee.getCodTaxa()).orElse(savedFee);
    }

    @Override
    public Fee updateFee(Long codTaxa, UpdateFeeCommand command) {
        Fee existing = feeRepository.findByIdFee(codTaxa)
                .orElseThrow(() -> new ProductException(ProductErrorType.FEE_NOT_FOUND));

        Fee updated = new Fee(
                existing.getCodTaxa(),
                existing.getDtInicial(),
                command.dscTaxa(),
                command.dtFinal(),
                existing.getCodCanal(),
                existing.getCodProduto(),
                existing.getCanal(),
                existing.getProduto(),
                null,
                null,
                existing.getTaxaDes()
        );

        Fee savedFee = feeRepository.save(updated);

        if (existing.getTaxaAdministrativa() != null) {
            AdministrativeFee taxaAdm = new AdministrativeFee(
                    existing.getTaxaAdministrativa().getCodTaxaAdm(),
                    command.taxaAdministrativa().recInicial().byteValue(),
                    command.taxaAdministrativa().recFinal().byteValue(),
                    command.taxaAdministrativa().valFixo(),
                    command.taxaAdministrativa().valPercentual(),
                    savedFee
            );
            administrativeFeeRepository.save(taxaAdm);
        }

        if (existing.getTaxaServico() != null) {
            ServiceFee taxaSrv = new ServiceFee(
                    existing.getTaxaServico().getCodTaxaSrv(),
                    command.taxaServico().recInicial().byteValue(),
                    command.taxaServico().recFinal().byteValue(),
                    command.taxaServico().valFixo(),
                    command.taxaServico().valPercentual(),
                    command.taxaServico().valMinimo(),
                    savedFee
            );
            serviceFeeRepository.save(taxaSrv);
        }

        return feeRepository.findByIdFee(codTaxa).orElse(savedFee);
    }

    @Override
    public List<Fee> listFees(String codProduto, String codCanal) {
        return feeRepository.findByCanalProduto(codCanal, codProduto);
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private User resolveUser(Long idUsuario) {
        if (idUsuario == null) return null;
        return userRepository.findById(idUsuario).orElse(null);
    }
}
