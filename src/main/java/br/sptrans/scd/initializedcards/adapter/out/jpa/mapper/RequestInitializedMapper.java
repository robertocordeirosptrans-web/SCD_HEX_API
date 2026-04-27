package br.sptrans.scd.initializedcards.adapter.out.jpa.mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.domain.ClassificationPerson;
import br.sptrans.scd.channel.adapter.out.persistence.entity.SalesChannelEntityJpa;
import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.initializedcards.adapter.out.persistence.entity.RICEntityJpa;
import br.sptrans.scd.initializedcards.adapter.out.persistence.entity.RICEntityJpaKey;
import br.sptrans.scd.initializedcards.domain.RequestInitializedCards;
import br.sptrans.scd.product.adapter.out.persistence.entity.ProductEntityJpa;
import br.sptrans.scd.product.domain.Product;

@Component
public class RequestInitializedMapper {

    public RequestInitializedCards toDomain(RICEntityJpa entity) {
        if (entity == null) return null;
        RequestInitializedCards domain = new RequestInitializedCards();
        RICEntityJpaKey key = entity.getId();
        if (key != null) {
            domain.setCodTipoCanal(key.getCodTipoCanal());
            domain.setCodCanal(toSalesChannel(key.getCodCanal(), entity.getSalesChannel()));
            domain.setNrSolicitacao(key.getNrSolicitacao());
        }
        domain.setCodAdquirente(entity.getCodAdquirente());
        domain.setCodProduto(toProduct(entity.getCodProduto(), entity.getProduto()));
        domain.setQtdSolicitada(entity.getQtdSolicitada());
        domain.setQtdAtendida(entity.getQtdAtendida());
        domain.setQtdRecebida(entity.getQtdRecebida());
        domain.setFlgTipoSaida(entity.getFlgTipoSaida());
        domain.setFlgTipoVolume(entity.getFlgTipoVolume());
        domain.setFlgAssociaUsuario(entity.getFlgAssociacaoUsuario());
        domain.setFlgGerarArquivo(entity.getFlgGeraArquivo());
        domain.setFlgRespEntregaRetirada(entity.getFlgRespEntregaRetirada());
        domain.setDesRespEntrega(entity.getDesNomeRespEntrega());
        domain.setCodTipoRespEntrega(entity.getCodTipoDoctoRespEntrega());
        domain.setCodDoctoRespEntrega(entity.getCodDoctoRespEntrega());
        domain.setCodEnderecoEntrega(entity.getCodEnderecoEntrega());
        domain.setDesRespRecebimento(entity.getDesNomeRespRecebimento());
        domain.setCodTipoDoctoRespRecebe(entity.getCodTipoDoctoRespRecebe());
        domain.setCodDoctoRespRecebe(entity.getCodDoctoRespRecebe());
        domain.setDesMotivoDiferencaRecebe(entity.getDesMotivoDiferencaRecebe());
        domain.setFlgAprovado(entity.getFlgAprovado());
        // idUsuarioAprovacao, idUsuarioCadastro, idUsuarioManutencao: FK - not loaded here
        domain.setDtPrevistaEntrega(toLocalDateTime(entity.getDtPrevistaEntrega()));
        domain.setDtSolicitacao(toLocalDateTime(entity.getDtSolicitacao()));
        domain.setDtAprovacao(toLocalDateTime(entity.getDtAprovacao()));
        domain.setDtAssociacaoLoteSCP(toLocalDateTime(entity.getDtAssociacaoLoteScp()));
        domain.setDtGeracaoArquivo(toLocalDateTime(entity.getDtGeracaoArquivo()));
        domain.setDtAssociacaoUsuario(toLocalDateTime(entity.getDtAssociacaoUsuario()));
        domain.setDtEnvio(toLocalDateTime(entity.getDtEnvio()));
        domain.setDtRecebimento(toLocalDateTime(entity.getDtRecebimento()));
        domain.setDtDevolucao(toLocalDateTime(entity.getDtDevolucao()));
        domain.setFlgFaseSolicitacao(entity.getFlgFaseSolicitacao());
        domain.setStSolicitaCartaoInicializad(entity.getStSolicitaCartaoInicializad());
        domain.setDtCadastro(toLocalDateTime(entity.getDtCadastro()));
        domain.setDtManutencao(toLocalDateTime(entity.getDtManutencao()));
        domain.setDtCancelamento(toLocalDateTime(entity.getDtCancelamento()));
        return domain;
    }

    public RICEntityJpa toEntity(RequestInitializedCards domain) {
        if (domain == null) return null;
        RICEntityJpa entity = new RICEntityJpa();
        RICEntityJpaKey key = new RICEntityJpaKey();
        key.setCodTipoCanal(domain.getCodTipoCanal());
        key.setCodCanal(domain.getCodCanal() != null ? domain.getCodCanal().getCodCanal() : null);
        key.setNrSolicitacao(domain.getNrSolicitacao());
        entity.setId(key);
        entity.setCodAdquirente(domain.getCodAdquirente());
        entity.setCodProduto(domain.getCodProduto() != null ? domain.getCodProduto().getCodProduto() : null);
        entity.setQtdSolicitada(domain.getQtdSolicitada());
        entity.setQtdAtendida(domain.getQtdAtendida());
        entity.setQtdRecebida(domain.getQtdRecebida());
        entity.setFlgTipoSaida(domain.getFlgTipoSaida());
        entity.setFlgTipoVolume(domain.getFlgTipoVolume());
        entity.setFlgAssociacaoUsuario(domain.getFlgAssociaUsuario());
        entity.setFlgGeraArquivo(domain.getFlgGerarArquivo());
        entity.setFlgRespEntregaRetirada(domain.getFlgRespEntregaRetirada());
        entity.setDesNomeRespEntrega(domain.getDesRespEntrega());
        entity.setCodTipoDoctoRespEntrega(domain.getCodTipoRespEntrega());
        entity.setCodDoctoRespEntrega(domain.getCodDoctoRespEntrega());
        entity.setCodEnderecoEntrega(domain.getCodEnderecoEntrega());
        entity.setDesNomeRespRecebimento(domain.getDesRespRecebimento());
        entity.setCodTipoDoctoRespRecebe(domain.getCodTipoDoctoRespRecebe());
        entity.setCodDoctoRespRecebe(domain.getCodDoctoRespRecebe());
        entity.setDesMotivoDiferencaRecebe(domain.getDesMotivoDiferencaRecebe());
        entity.setFlgAprovado(domain.getFlgAprovado());
        entity.setIdUsuarioAprovacao(domain.getIdUsuarioAprovacao() != null ? domain.getIdUsuarioAprovacao().getIdUsuario() : null);
        entity.setDtPrevistaEntrega(toDate(domain.getDtPrevistaEntrega()));
        entity.setDtSolicitacao(toDate(domain.getDtSolicitacao()));
        entity.setDtAprovacao(toDate(domain.getDtAprovacao()));
        entity.setDtAssociacaoLoteScp(toDate(domain.getDtAssociacaoLoteSCP()));
        entity.setDtGeracaoArquivo(toDate(domain.getDtGeracaoArquivo()));
        entity.setDtAssociacaoUsuario(toDate(domain.getDtAssociacaoUsuario()));
        entity.setDtEnvio(toDate(domain.getDtEnvio()));
        entity.setDtRecebimento(toDate(domain.getDtRecebimento()));
        entity.setDtDevolucao(toDate(domain.getDtDevolucao()));
        entity.setFlgFaseSolicitacao(domain.getFlgFaseSolicitacao());
        entity.setStSolicitaCartaoInicializad(domain.getStSolicitaCartaoInicializad());
        entity.setDtCadastro(toDate(domain.getDtCadastro()));
        entity.setDtManutencao(toDate(domain.getDtManutencao()));
        entity.setDtCancelamento(toDate(domain.getDtCancelamento()));
        entity.setIdUsuarioCadastro(domain.getIdUsuarioCadastro() != null ? domain.getIdUsuarioCadastro().getIdUsuario() : null);
        entity.setIdUsuarioManutencao(domain.getIdUsuarioManutencao() != null ? domain.getIdUsuarioManutencao().getIdUsuario() : null);
        return entity;
    }

    private SalesChannel toSalesChannel(String codCanal, SalesChannelEntityJpa entity) {
        if (codCanal == null && entity == null) return null;
        SalesChannel sc = new SalesChannel();
        sc.setCodCanal(codCanal);
        if (entity != null) {
            sc.setDesCanal(entity.getDesCanal());
            if (entity.getClassificacaoPessoa() != null) {
                ClassificationPerson cp = new ClassificationPerson();
                cp.setCodClassificacaoPessoa(entity.getClassificacaoPessoa().getCodClassificacaoPessoa());
                cp.setDesClassificacaoPessoa(entity.getClassificacaoPessoa().getDesClassificacaoPessoa());
                sc.setCodClassificacaoPessoa(cp);
            }
        }
        return sc;
    }

    private Product toProduct(String codProduto, ProductEntityJpa entity) {
        if (codProduto == null && entity == null) return null;
        Product p = new Product();
        p.setCodProduto(codProduto);
        if (entity != null) {
            p.setDesProduto(entity.getDesProduto());
        }
        return p;
    }

    public LocalDateTime toLocalDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public Date toDate(LocalDateTime ldt) {
        if (ldt == null) return null;
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }
}
