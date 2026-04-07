package br.sptrans.scd.product.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.application.port.in.FeeFareManagementUseCase;
import br.sptrans.scd.product.application.port.out.gateway.LiminarGateway;
import br.sptrans.scd.product.application.port.out.repository.FarePort;
import br.sptrans.scd.product.application.port.out.repository.FeePersistencePort;
import br.sptrans.scd.product.application.port.out.repository.ProductPort;
import br.sptrans.scd.product.domain.AdministrativeFee;
import br.sptrans.scd.product.domain.DestinyFee;
import br.sptrans.scd.product.domain.Fare;
import br.sptrans.scd.product.domain.Fee;
import br.sptrans.scd.product.domain.Product;
import br.sptrans.scd.product.domain.ServiceFee;
import br.sptrans.scd.product.domain.enums.ProductDomainStatus;
import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.product.domain.exception.ProductException;
import br.sptrans.scd.product.domain.vo.TaxaCalculada;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FeeFareService implements FeeFareManagementUseCase {

    private final FarePort fareRepository;
    private final FeePersistencePort feeRepository;
    private final ProductPort productRepository;
    private final UserResolverHelper userResolverHelper;
    private final LiminarGateway liminarGateway;

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_FEEFARE");

    /**
     * Calcula as taxas administrativas e de serviço para um pedido, aplicando
     * isenção por liminar judicial quando aplicável.
     *
     * @param valorTotal   valor base para cálculo das taxas
     * @param codigoCanal  código do canal
     * @param numeroPedido número do pedido (para verificação de liminar)
     * @param idTaxa       identificador da taxa vigente para canal/produto
     * @return objeto com valores calculados das taxas
     */
    @Transactional(readOnly = true)
    public TaxaCalculada calcularTaxas(BigDecimal valorTotal, String codigoCanal, String numeroPedido, Long idTaxa) {
        // Canal 152: verifica liminar antes de buscar as taxas
        final int CANAL_LOJA_VIRTUAL = 152;
        try {
            if (Integer.parseInt(codigoCanal) == CANAL_LOJA_VIRTUAL
                    && liminarGateway.empresaPossuiIsencaoTaxa(numeroPedido)) {
                // Tem liminar → zera tudo
                return TaxaCalculada.isenta();
            }
        } catch (NumberFormatException e) {
            // Canal não numérico → não aplica regra de liminar
        }

        // Sem liminar → busca taxas normais no banco
        AdministrativeFee taxaAdm = feeRepository.findAdmById(idTaxa)
                .orElseThrow(() -> new ProductException(ProductErrorType.FEE_NOT_FOUND));
        ServiceFee taxaServ = feeRepository.findSrvById(idTaxa)
                .orElseThrow(() -> new ProductException(ProductErrorType.FEE_NOT_FOUND));

        BigDecimal base = valorTotal != null ? valorTotal : BigDecimal.ZERO;

        BigDecimal valorTaxaAdm = taxaAdm.calculateValue(base);
        BigDecimal valorTaxaServ = taxaServ.calculateValue(base);

        return new TaxaCalculada(valorTaxaAdm, valorTaxaServ);
    }



    // =========================================================================
    // Gestão de Tarifas (Fare)
    // =========================================================================

    @Override
    public Fare createFare(RegisterFareCommand command) {
        boolean conflict = fareRepository.isConflictValidity(
                command.codProduto(), command.codCanal(),
                command.dtInicio(), command.dtFim(), null);
        if (conflict) {
            throw new ProductException(ProductErrorType.FARE_VALIDITY_CONFLICT);
        }

        User usuario = userResolverHelper.resolve(command.idUsuario());
        Product produto = productRepository.findById(command.codProduto())
                .orElseThrow(() -> new ProductException(ProductErrorType.PRODUCT_NOT_FOUND));

        Fare fare = new Fare(
                UUID.randomUUID().toString(),
                command.codVersao(),
                command.dtInicio(),
                command.dtFim(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                command.desTarifa(),
                ProductDomainStatus.ACTIVE.getCode(),
                command.valTarifa() != null ? command.valTarifa().intValue() : null,
                usuario,
                null,
                produto);

        auditLogger.info("[AUDIT] Usuário {} criou Tarifa {} para Produto {}", usuario.getCodLogin(), fare.getCodTarifa(), produto.getCodProduto());

        return fareRepository.save(fare);
    }

    @Override
    public Fare updateFare(String codTarifa, UpdateFareCommand command) {
        Fare fare = fareRepository.findById(codTarifa)
                .orElseThrow(() -> new ProductException(ProductErrorType.FARE_NOT_FOUND));

        User usuario = userResolverHelper.resolve(command.idUsuario());
        fare.extendValidity(command.dtFim(), usuario);
        auditLogger.info("[AUDIT] Usuário {} atualizou Tarifa {}", usuario.getCodLogin(), fare.getCodTarifa());
        return fareRepository.save(fare);
    }

    @Override
    public List<Fare> listFares(String codProduto, String codCanal) {
        return fareRepository.listByProductChannel(codProduto, codCanal);
    }

    // =========================================================================
    // Gestão de Taxas (Fee)
    // =========================================================================

    @Override
    public List<Fee> findByCanalProduto(String codCanal, String codProduto) {
        return feeRepository.findByCanalProduto(codCanal, codProduto);
    }

    @Override
    public Fee createFee(RegisterFeeCommand command) {
        Fee fee = new Fee(
                null,
                command.dtInicio(),
                command.desTaxa(),
                command.dtFim(),
                command.codCanal(),
                command.codProduto(),
                null,
                null,
                null,
                null,
                null);

        Fee savedFee = feeRepository.save(fee);

        AdministrativeFee taxaAdm = new AdministrativeFee(
                null,
                command.taxaAdministrativa().recInicial().byteValue(),
                command.taxaAdministrativa().recFinal().byteValue(),
                command.taxaAdministrativa().valFixo(),
                command.taxaAdministrativa().valPercentual(),
                savedFee);
        feeRepository.save(taxaAdm);

        ServiceFee taxaSrv = new ServiceFee(
                null,
                command.taxaServico().recInicial().byteValue(),
                command.taxaServico().recFinal().byteValue(),
                command.taxaServico().valFixo(),
                command.taxaServico().valPercentual(),
                command.taxaServico().valMinimo(),
                savedFee);
        feeRepository.save(taxaSrv);

        if (command.taxaDestino() != null) {
            DestinyFee taxaDes = new DestinyFee(null, command.taxaDestino().codCanalDestino());
            feeRepository.save(taxaDes);
        }

        return feeRepository.findByIdFee(savedFee.getCodTaxa()).orElse(savedFee);
    }

    @Override
    public Fee updateFee(Long codTaxa, UpdateFeeCommand command) {
        Fee existing = feeRepository.findByIdFee(codTaxa)
                .orElseThrow(() -> new ProductException(ProductErrorType.FEE_NOT_FOUND));

        existing.update(command.desTaxa(), command.dtFim());
        Fee savedFee = feeRepository.save(existing);

        auditLogger.info("[AUDIT] Taxa atualizada: {} por comando {}", codTaxa, command);

        if (existing.getTaxaAdministrativa() != null) {
            AdministrativeFee taxaAdm = new AdministrativeFee(
                    existing.getTaxaAdministrativa().getCodTaxaAdm(),
                    command.taxaAdministrativa().recInicial().byteValue(),
                    command.taxaAdministrativa().recFinal().byteValue(),
                    command.taxaAdministrativa().valFixo(),
                    command.taxaAdministrativa().valPercentual(),
                    savedFee);
            feeRepository.save(taxaAdm);
        }

        if (existing.getTaxaServico() != null) {
            ServiceFee taxaSrv = new ServiceFee(
                    existing.getTaxaServico().getCodTaxaSrv(),
                    command.taxaServico().recInicial().byteValue(),
                    command.taxaServico().recFinal().byteValue(),
                    command.taxaServico().valFixo(),
                    command.taxaServico().valPercentual(),
                    command.taxaServico().valMinimo(),
                    savedFee);
            feeRepository.save(taxaSrv);
        }

        return feeRepository.findByIdFee(codTaxa).orElse(savedFee);
    }

    @Override
    public List<Fee> listFees(String codProduto, String codCanal) {
        return feeRepository.findByCanalProduto(codCanal, codProduto);
    }
}
