package br.sptrans.scd.creditrequest.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.sptrans.scd.channel.application.port.out.AgreementValidityPersistencePort;
import br.sptrans.scd.channel.application.port.out.MarketingDistribuitionChannelPersistencePort;
import br.sptrans.scd.channel.application.port.out.ProductChannelPersistencePort;
import br.sptrans.scd.channel.application.port.out.RechargeLimitPersistencePort;
import br.sptrans.scd.channel.application.port.out.SalesChannelPersistencePort;
import br.sptrans.scd.channel.domain.AgreementValidity;
import br.sptrans.scd.channel.domain.AgreementValidityKey;
import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.channel.domain.ProductChannelKey;
import br.sptrans.scd.channel.domain.RechargeLimit;
import br.sptrans.scd.channel.domain.RechargeLimitKey;
import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestResponse.ItemRejeitado;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestPort;
import br.sptrans.scd.product.application.port.out.repository.ProductVersionPort;
import br.sptrans.scd.product.domain.ProductVersion;
import br.sptrans.scd.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditRequestValidationService {

    private final MarketingDistribuitionChannelPersistencePort mdChannelRepository;
    private final AgreementValidityPersistencePort agreeValidRepository;
    private final ProductChannelPersistencePort productChannelRepository;
    private final RechargeLimitPersistencePort rechargeLimitRepository;
    private final SalesChannelPersistencePort salesChannelRepository;
    private final ProductVersionPort productVersionRepository;
    private final CreditRequestPort creditRequestRepository;

    public SalesChannel validarCanal(String codCanal) {
        SalesChannel canal = salesChannelRepository.findById(codCanal)
                .orElseThrow(() -> new IllegalStateException("Canal não encontrado: " + codCanal));
        if (!canal.isAtivo()) {
            throw new IllegalStateException("Canal inativo: " + codCanal);
        }
        return canal;
    }

    public void validarNumLote(String numLote, String codCanal) {
        if (creditRequestRepository.existsByNumLoteAndCodCanal(numLote, codCanal)) {
            throw new IllegalStateException("Número de lote já utilizado para este canal: " + numLote);
        }
    }

    public void validarDataLiberacao(LocalDateTime dataLiberacao) {
        if (dataLiberacao == null) {
            throw new IllegalStateException("Data de liberação de crédito é obrigatória");
        }
        if (dataLiberacao.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "Data de liberação inválida: " + dataLiberacao + " é anterior à data atual");
        }
    }

    public void validarSubordinadosSupercanal(String codCanal) {
        List<SalesChannel> subordinados = salesChannelRepository.findByCodCanalSuperior(codCanal);
        boolean temSubordinadoAtivo = subordinados.stream().anyMatch(SalesChannel::isAtivo);
        if (!temSubordinadoAtivo) {
            throw new IllegalStateException("Supercanal " + codCanal + " não possui subordinados ativos");
        }
    }

    /**
     * Valida a vigência da versão do produto para um item do pedido de crédito.
     * Se a vigência estiver expirada, adiciona motivo à lista de rejeitados.
     */
    public void validarVigenciaVersaoProduto(Long numSolicitacao, String cartao, String codProduto,
            String codCanal, String codVersao, LocalDateTime dataSolicitacao,
            List<ItemRejeitado> rejeitados) {
        Optional<ProductVersion> versaoOpt = productVersionRepository.findById(codVersao);
        if (versaoOpt.isEmpty()) {
            rejeitados.add(new ItemRejeitado(numSolicitacao, cartao, codProduto,
                    "Versão do produto não encontrada para o produto " + codProduto));
            return;
        }
        ProductVersion versao = versaoOpt.get();
        LocalDateTime validade = versao.getDtValidade();
        LocalDateTime data = dataSolicitacao != null ? dataSolicitacao : LocalDateTime.now();
        if (validade != null && data.isAfter(validade)) {
            rejeitados.add(new ItemRejeitado(numSolicitacao, cartao, codProduto,
                    "Versão do produto " + codVersao + " expirada para o produto " + codProduto));
            return;
        }
        if (!versao.isActive()) {
            rejeitados.add(new ItemRejeitado(numSolicitacao, cartao, codProduto,
                    "Versão do produto " + codVersao + " não está ativa para o produto " + codProduto));
        }
    }

    public void validarVigenciadoCanal(Long numSolicitacao, String cartao, String produto,
            String codCanal, String codCanalDistrib, String codProduto,
            List<ItemRejeitado> rejeitados) {
        boolean isAssocied = mdChannelRepository
                .findActiveByCanalDistrib(codCanal, codCanalDistrib)
                .isPresent();
        if (!isAssocied) {
            rejeitados.add(new ItemRejeitado(numSolicitacao, cartao, produto,
                    "Canal de distribuição " + codCanalDistrib
                            + " não associado ao canal de comercialização " + codCanal));
        }

        AgreementValidityKey key = new AgreementValidityKey(codCanal, codProduto);
        Optional<AgreementValidity> convOpt = agreeValidRepository.findById(key);
        if (convOpt.isEmpty()) {
            rejeitados.add(new ItemRejeitado(numSolicitacao, cartao, produto,
                    "Convênio não vigente para canal de distribuição " + codCanal
                            + " e produto " + codProduto));
            return;
        }
        AgreementValidity conv = convOpt.get();
        if (!conv.isVigente()) {
            rejeitados.add(new ItemRejeitado(numSolicitacao, cartao, produto,
                    "Convênio não vigente para canal de distribuição " + codCanal
                            + " e produto " + codProduto));
        }
    }

    public ProductChannel validarProdutoNoCanal(Long numSolicitacao, String cartao, String produto,
            String codCanal, String codProduto,
            List<ItemRejeitado> rejeitados) {
        ProductChannelKey key = new ProductChannelKey(codCanal, codProduto);
        Optional<ProductChannel> cpOpt = productChannelRepository.findById(key);
        if (cpOpt.isEmpty()) {
            rejeitados.add(new ItemRejeitado(numSolicitacao, cartao, produto,
                    "Produto " + codProduto + " não comercializado pelo canal " + codCanal));
            return null;
        }
        ProductChannel cp = cpOpt.get();
        if (!cp.isAtivo()) {
            rejeitados.add(new ItemRejeitado(numSolicitacao, cartao, produto,
                    "Produto " + codProduto + " inativo no canal " + codCanal));
            return null;
        }
        return cp;
    }

    public String validarLimites(String codCanal, String codProduto, BigDecimal valorTotal) {
        RechargeLimitKey key = new RechargeLimitKey(codCanal, codProduto);
        RechargeLimit limite = rechargeLimitRepository.findById(key)
                .orElseThrow(() -> new ValidationException(
                        "Não há limites configurados para o canal " + codCanal + " e produto " + codProduto));
        limite.validarLimites(valorTotal);
        return null;
    }

    // Helper para obter RechargeLimit sem exception
    public RechargeLimit getRechargeLimit(String codCanal, String codProduto) {
        RechargeLimitKey key = new RechargeLimitKey(codCanal, codProduto);
        return rechargeLimitRepository.findById(key).orElse(null);
    }
}
