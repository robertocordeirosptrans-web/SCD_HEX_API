package br.sptrans.scd.creditrequest.application.service;

import br.sptrans.scd.channel.application.port.out.AgreementValidityRepository;
import br.sptrans.scd.channel.application.port.out.MarketingDistribuitionChannelRepository;
import br.sptrans.scd.channel.application.port.out.ProductChannelRepository;
import br.sptrans.scd.channel.application.port.out.RechargeLimitRepository;
import br.sptrans.scd.channel.application.port.out.SalesChannelRepository;
import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.channel.domain.RechargeLimit;
import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestResponse.ItemRejeitado;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreditRequestValidationService {
    private final MarketingDistribuitionChannelRepository mdChannelRepository;
    private final AgreementValidityRepository agreeValidRepository;
    private final ProductChannelRepository productChannelRepository;
    private final RechargeLimitRepository rechargeLimitRepository;
    private final SalesChannelRepository salesChannelRepository;



    public SalesChannel validarCanal(String codCanal) {
        SalesChannel canal = salesChannelRepository.findById(codCanal)
                .orElseThrow(() -> new IllegalStateException(
                        "Canal não encontrado: " + codCanal));
        if (!"A".equalsIgnoreCase(canal.getStCanais())) {
            throw new IllegalStateException("Canal inativo: " + codCanal);
        }
        return canal;
    }

    public void validarNumLote(String numLote, String codCanal, br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestRepository creditRequestRepository) {
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

    public void validarVigenciadoCanal(Long numSolicitacao, String cartao, String produto,
                                       String codCanalDistrib, String codProduto,
                                       List<ItemRejeitado> rejeitados) {
        boolean canalDistribAtivo = mdChannelRepository
                .findActiveByCanalDistrib(codCanalDistrib)
                .isPresent();
        if (!canalDistribAtivo) {
            rejeitados.add(new ItemRejeitado(numSolicitacao, cartao, produto,
                    "Canal de distribuição inativo ou não encontrado: " + codCanalDistrib));
            return;
        }
        // Convênio vigente para o canal de distribuição
         agreeValidRepository.findByIdOptimized(codCanalDistrib, codProduto)
                .ifPresentOrElse(conv -> {
                  LocalDateTime agora = LocalDateTime.now();
                   boolean vigente = "A".equalsIgnoreCase(conv.getStatus())
                           && (conv.getDataInicioValidade() == null
                           || !conv.getDataInicioValidade().isAfter(agora))
                           && (conv.getDataFimValidade() == null
                           || !conv.getDataFimValidade().isBefore(agora));
                   if (!vigente) {
                       rejeitados.add(new ItemRejeitado(numSolicitacao, cartao, produto,
                               "Convênio não vigente para canal de distribuição: " + codCanalDistrib));
                   }
               }, () -> {
                   // Sem convênio cadastrado — não bloqueia, apenas avisa
               });
    }

    public ProductChannel validarProdutoNoCanal(Long numSolicitacao, String cartao, String produto,
                                                String codCanal, String codProduto,
                                                List<ItemRejeitado> rejeitados) {
        Optional<ProductChannel> cpOpt = productChannelRepository
                .findById(codCanal, codProduto);
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
        Optional<RechargeLimit> limiteOpt = rechargeLimitRepository.findById(codCanal, codProduto);
        if (limiteOpt.isEmpty()) {
            return null; // Sem limites configurados → não bloqueia
        }
        RechargeLimit limite = limiteOpt.get();
        if (limite.getVlMinimoRecarga() != null
                && valorTotal.compareTo(limite.getVlMinimoRecarga()) < 0) {
            return "Valor " + valorTotal + " abaixo do limite mínimo de recarga "
                    + limite.getVlMinimoRecarga();
        }
        if (limite.getVlMaximoRecarga() != null
                && valorTotal.compareTo(limite.getVlMaximoRecarga()) > 0) {
            return "Valor " + valorTotal + " acima do limite máximo de recarga "
                    + limite.getVlMaximoRecarga();
        }
        return null;
    }
}
