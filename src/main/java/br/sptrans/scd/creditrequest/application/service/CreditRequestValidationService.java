
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
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestRepository;
import br.sptrans.scd.product.application.port.out.repository.ProductVersionRepository;
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
    private final ProductVersionRepository productVersionRepository;

    public SalesChannel validarCanal(String codCanal) {
        SalesChannel canal = salesChannelRepository.findById(codCanal)
                .orElseThrow(() -> new IllegalStateException(
                "Canal não encontrado: " + codCanal));
        if (!"A".equalsIgnoreCase(canal.getStCanais())) {
            throw new IllegalStateException("Canal inativo: " + codCanal);
        }
        return canal;
    }

    public void validarNumLote(String numLote, String codCanal, CreditRequestRepository creditRequestRepository) {
        if (creditRequestRepository.existsByNumLoteAndCodCanal(numLote, codCanal)) {
            throw new IllegalStateException(
                    "Número de lote já utilizado para este canal: " + numLote);
        }
    }

    public void validarDataLiberacao(LocalDateTime dataLiberacao) {
        if (dataLiberacao == null) {
            throw new IllegalStateException("Data de liberação de crédito é obrigatória");
        }
        LocalDateTime hoje = LocalDateTime.now();
        if (dataLiberacao.isBefore(hoje)) {
            throw new IllegalStateException(
                    "Data de liberação inválida: " + dataLiberacao
                    + " é anterior à data atual " + hoje);
        }
    }

    public void validarSubordinadosSupercanal(String codCanal) {
        List<SalesChannel> subordinados = salesChannelRepository.findByCodCanalSuperior(codCanal);
        boolean temSubordinadoAtivo = subordinados.stream()
                .anyMatch(s -> "A".equalsIgnoreCase(s.getStCanais()));
        if (!temSubordinadoAtivo) {
            throw new IllegalStateException(
                    "Supercanal " + codCanal + " não possui subordinados ativos");
        }
    }

        /**
         * Valida a vigência da versão do produto para um item do pedido de crédito.
         * Se a vigência estiver expirada, adiciona motivo à lista de rejeitados.
         */
        public void validarVigenciaVersaoProduto(Long numSolicitacao, String cartao, String codProduto,
                String codCanal, String codVersao, LocalDateTime dataSolicitacao,
                List<ItemRejeitado> rejeitados) {
            // Busca a versão do produto
            Optional<ProductVersion> versaoOpt = productVersionRepository.findById(codProduto);
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


        // Convênio vigente para o canal de distribuição
        AgreementValidityKey key = new AgreementValidityKey(codCanal, codProduto);

        Optional<AgreementValidity> convOpt = agreeValidRepository.findById(key);
        if (convOpt.isEmpty() || convOpt.get().getDtInicioValidade().isBefore(LocalDateTime.now())) {
            rejeitados.add(new ItemRejeitado(numSolicitacao, cartao, produto,
                    "Convênio não vigente para canal de distribuição " + codCanal
                    + " e produto " + codProduto));
        }
    }

    public ProductChannel validarProdutoNoCanal(Long numSolicitacao, String cartao, String produto,
            String codCanal, String codProduto,
            List<ItemRejeitado> rejeitados) {
        ProductChannelKey key = new ProductChannelKey(codCanal, codProduto);
        Optional<ProductChannel> cpOpt = productChannelRepository
                .findById(key);
        if (cpOpt.isEmpty()) {
            rejeitados.add(new ItemRejeitado(numSolicitacao, cartao, produto,
                    "Produto " + codProduto + " não comercializado pelo canal " + codCanal));
            return null;
        }
        ProductChannel cp = cpOpt.get();
        if (cp.getCodStatus() != null && !"A".equalsIgnoreCase(cp.getCodStatus())) {
            rejeitados.add(new ItemRejeitado(numSolicitacao, cartao, produto,
                    "Produto " + codProduto + " inativo no canal " + codCanal));
            return null;
        }

        return cp;
    }

    public String validarLimites(String codCanal, String codProduto, BigDecimal valorTotal) {
        RechargeLimitKey key = new RechargeLimitKey(codCanal, codProduto);
        Optional<RechargeLimit> limiteOpt = rechargeLimitRepository.findById(key);
        if (limiteOpt.isEmpty()) {
            throw new ValidationException(
                    "Não há limites configurados para o canal " + codCanal + " e produto " + codProduto);
        }
        RechargeLimit limite = limiteOpt.get();

        if (limite.getDtFimValidade() != null && limite.getDtFimValidade().isBefore(LocalDateTime.now())) {
            throw new ValidationException(
                    "Limite de recarga expirado para o canal " + codCanal + " e produto " + codProduto);
        }

        if (limite.getVlMinimoRecarga() != null
                && valorTotal.compareTo(limite.getVlMinimoRecarga()) < 0) {
            throw new ValidationException(
                    "Valor " + valorTotal + " abaixo do limite mínimo de recarga "
                    + limite.getVlMinimoRecarga());
        }
        if (limite.getVlMaximoRecarga() != null
                && valorTotal.compareTo(limite.getVlMaximoRecarga()) > 0) {
            throw new ValidationException(
                    "Valor " + valorTotal + " acima do limite máximo de recarga "
                    + limite.getVlMaximoRecarga());
        }
        return null;
    }
}
