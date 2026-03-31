package br.sptrans.scd.creditrequest.adapter.port.out.jpa.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestEJpa;
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpaKey;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreditRequestDTO;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreditRequestItemsDTO;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.CreditRequestItemsKey;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreditRequestMapper {
    // Conversão de domínio para entidade JPA
    default CreditRequestItemsEJpa toEntityItem(CreditRequestItems items) {
        if (items == null) {
            return null;
        }
        CreditRequestItemsEJpa entity = new CreditRequestItemsEJpa();
        CreditRequestItemsEJpaKey key = new CreditRequestItemsEJpaKey();
        key.setNumSolicitacao(items.getId().getNumSolicitacao());
        key.setNumSolicitacaoItem(items.getId().getNumSolicitacaoItem());
        key.setCodCanal(items.getId().getCodCanal());
        entity.setId(key);
        entity.setCodProduto(items.getCodProduto());
        entity.setCodSituacao(items.getCodSituacao());
        entity.setVlItem(items.getVlItem());
        entity.setDtRecarga(items.getDtRecarga());
        entity.setVlCarregado(items.getVlCarregado());
        entity.setDtCadastro(items.getDtCadastro());
        entity.setDtManutencao(items.getDtManutencao());
        entity.setVlTxadm(items.getVlTxadm());
        entity.setVlTxserv(items.getVlTxserv());
        entity.setVlTxtotal(items.getVlTxtotal());
        entity.setCodVersao(items.getCodVersao());
        entity.setNumLogicoCartao(items.getNumLogicoCartao());
        entity.setQtdItem(items.getQtdItem());
        entity.setIdUsuarioCartao(items.getIdUsuarioCartao());
        entity.setCodTipoDocumento(items.getCodTipoDocumento());
        entity.setVlAjuste(items.getVlAjuste());
        entity.setFlgAjuste(items.getFlgAjuste());
        entity.setIdFuncionario(items.getIdFuncionario());
        entity.setCodAssinaturaHsm(items.getCodAssinaturaHsm());
        entity.setSeqRecarga(items.getSeqRecarga());
        entity.setDtEnvioHm(items.getDtEnvioHm());
        entity.setDtRetornoHm(items.getDtRetornoHm());
        entity.setIdUsuarioCadastro(items.getIdUsuarioCadastro());
        entity.setIdUsuarioManutencao(items.getIdUsuarioManutencao());
        entity.setDtAssinatura(items.getDtAssinatura());
        entity.setDtPagtoEconomica(items.getDtPagtoEconomica());
        entity.setSqPid(items.getSqPid());
        entity.setDtInicProcesso(items.getDtInicProcesso());
        entity.setSqRecarga(items.getSqRecarga());
        entity.setFlgEvento(items.getFlgEvento());
        entity.setVlEvento(items.getVlEvento());
        entity.setFlgOutrasVias(items.getFlgOutrasVias());
        entity.setCodAssdigRecarga(items.getCodAssdigRecarga());
        entity.setVlAutorizacaoHm(items.getVlAutorizacaoHm());
        entity.setFlgLiminarLoja(items.getFlgLiminarLoja());
        entity.setCodProdutoHm(items.getCodProdutoHm());
        entity.setQtdDiasUtilizados(items.getQtdDiasUtilizados());
        // Adicione outros campos conforme necessário
        return entity;
    }


    @Mapping(target = "itens", source = "itens", qualifiedByName = "mapItens")
    CreditRequestDTO toDTO(CreditRequest entity);

    List<CreditRequestDTO> toDTOList(List<CreditRequest> entities);

    @Mapping(target = "dtEnvioHm", ignore = true)
    @Mapping(target = "sqPid", ignore = true)
    @Mapping(target = "dtInicProcesso", ignore = true)
    @Mapping(target = "vlServicoRecarga", ignore = true)
    @Mapping(target = "vlServicoAdm", ignore = true)
    @Mapping(target = "flgEvento", ignore = true)
    @Mapping(target = "vlEvento", ignore = true)
    @Mapping(target = "itens", ignore = true)
    CreditRequest toEntity(CreditRequestDTO dto);

    @Named("mapItens")
    default List<CreditRequestItemsDTO> mapItens(List<CreditRequestItems> itens) {
        if (itens == null) {
            return List.of();
        }
        return itens.stream()
            .map(this::itemToDTO)
            .toList();
    }

    @Mapping(target = "numSolicitacaoItem", source = "id.numSolicitacaoItem")
    @Mapping(target = "vlTxadm", source = "vlTxadm")
    @Mapping(target = "vlTxserv", source = "vlTxserv")
    @Mapping(target = "vlTxtotal", source = "vlTxtotal")
    CreditRequestItemsDTO itemToDTO(CreditRequestItems entity);

    // Conversão de entidade JPA para domínio
    default CreditRequest toDomain(CreditRequestEJpa entity) {
        if (entity == null) {
            return null;
        }
        CreditRequest cr = new CreditRequest();
        cr.setNumSolicitacao(entity.getId().getNumSolicitacao());
        cr.setCodCanal(entity.getId().getCodCanal());
        cr.setIdUsuarioCadastro(entity.getIdUsuarioCadastro());
        cr.setCodTipoDocumento(entity.getCodTipoDocumento());
        cr.setCodSituacao(entity.getCodSituacao());
        cr.setCodFormaPagto(entity.getCodFormaPagto());
        cr.setDtSolicitacao(entity.getDtSolicitacao());
        cr.setDtPrevLiberacao(entity.getDtPrevLiberacao());
        cr.setDtAceite(entity.getDtAceite());
        cr.setDtConfirmaPagto(entity.getDtConfirmaPagto());
        cr.setDtPagtoEconomica(entity.getDtPagtoEconomica());
        cr.setCodUsuarioPortador(entity.getCodUsuarioPortador());
        cr.setDtLiberacaoEfetiva(entity.getDtLiberacaoEfetiva());
        cr.setCodEnderecoEntrega(entity.getCodEnderecoEntrega());
        cr.setNumLote(entity.getNumLote());
        cr.setDtFinanceira(entity.getDtFinanceira());
        cr.setVlTotal(entity.getVlTotal());
        cr.setDtCadastro(entity.getDtCadastro());
        cr.setFlgCanc(entity.getFlgCanc());
        cr.setDtManutencao(entity.getDtManutencao());
        cr.setDtEnvioHm(entity.getDtEnvioHm());
        cr.setIdUsuarioManutencao(entity.getIdUsuarioManutencao());
        cr.setFlgBloq(entity.getFlgBloq());
        cr.setCodFormaPagto(entity.getCodFormaPagto());

        // Adicione outros campos conforme necessário
        cr.setItens(toDomainItens(entity.getItens()));
        return cr;
    }

    default List<CreditRequestItems> toDomainItens(List<CreditRequestItemsEJpa> itensJpa) {
        if (itensJpa == null) {
            return List.of();
        }
        return itensJpa.stream().map(this::toDomainItem).toList();
    }

    default CreditRequestItems toDomainItem(CreditRequestItemsEJpa itemJpa) {
        if (itemJpa == null) {
            return null;
        }
        CreditRequestItems item = new CreditRequestItems();
        CreditRequestItemsKey key = new CreditRequestItemsKey();
        key.setNumSolicitacao(itemJpa.getId().getNumSolicitacao());
        key.setNumSolicitacaoItem(itemJpa.getId().getNumSolicitacaoItem());
        key.setCodCanal(itemJpa.getId().getCodCanal());
        item.setId(key);
        item.setCodProduto(itemJpa.getCodProduto());
        item.setCodSituacao(itemJpa.getCodSituacao());
        item.setVlItem(itemJpa.getVlItem());
        item.setDtRecarga(itemJpa.getDtRecarga());
        item.setVlCarregado(itemJpa.getVlCarregado());
        item.setDtCadastro(itemJpa.getDtCadastro());
        item.setDtManutencao(LocalDateTime.now());
        item.setVlTxadm(itemJpa.getVlTxadm());
        item.setVlTxserv(itemJpa.getVlTxserv());
        item.setVlTxtotal(itemJpa.getVlTxtotal());
        item.setCodVersao(itemJpa.getCodVersao());
        item.setNumLogicoCartao(itemJpa.getNumLogicoCartao());
        item.setQtdItem(itemJpa.getQtdItem());
        item.setIdUsuarioCartao(itemJpa.getIdUsuarioCartao());
        item.setCodTipoDocumento(itemJpa.getCodTipoDocumento());
        item.setVlAjuste(itemJpa.getVlAjuste());
        item.setFlgAjuste(itemJpa.getFlgAjuste());
        item.setIdFuncionario(itemJpa.getIdFuncionario());
        item.setCodAssinaturaHsm(itemJpa.getCodAssinaturaHsm());
        item.setSeqRecarga(itemJpa.getSeqRecarga());
        item.setDtEnvioHm(itemJpa.getDtEnvioHm());
        item.setDtRetornoHm(itemJpa.getDtRetornoHm());
        item.setIdUsuarioCadastro(itemJpa.getIdUsuarioCadastro());
        item.setIdUsuarioManutencao(itemJpa.getIdUsuarioManutencao());
        item.setDtAssinatura(itemJpa.getDtAssinatura());
        item.setDtPagtoEconomica(itemJpa.getDtPagtoEconomica());
        item.setSqPid(itemJpa.getSqPid());
        item.setDtInicProcesso(itemJpa.getDtInicProcesso());
        item.setSqRecarga(itemJpa.getSqRecarga());
        item.setFlgEvento(itemJpa.getFlgEvento());
        item.setVlEvento(itemJpa.getVlEvento());
        item.setFlgOutrasVias(itemJpa.getFlgOutrasVias());
        item.setCodAssdigRecarga(itemJpa.getCodAssdigRecarga());
        item.setVlAutorizacaoHm(itemJpa.getVlAutorizacaoHm());
        item.setFlgLiminarLoja(itemJpa.getFlgLiminarLoja());
        item.setCodProdutoHm(itemJpa.getCodProdutoHm());
        item.setQtdDiasUtilizados(itemJpa.getQtdDiasUtilizados());
        item.setCodTipoDocumento(itemJpa.getCodTipoDocumento());
 
        return item;
    }

}
