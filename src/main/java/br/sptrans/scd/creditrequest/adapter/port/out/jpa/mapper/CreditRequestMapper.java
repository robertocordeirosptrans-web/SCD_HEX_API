package br.sptrans.scd.creditrequest.adapter.port.out.jpa.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestEJpa;
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreditRequestDTO;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreditRequestItemsDTO;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.CreditRequestItemsKey;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreditRequestMapper {


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
        item.setDtManutencao(itemJpa.getDtManutencao());
        item.setVlTxadm(itemJpa.getVlTxadm());
        item.setVlTxserv(itemJpa.getVlTxserv());
        item.setVlTxtotal(itemJpa.getVlTxtotal());
        // Adicione outros campos conforme necessário
        return item;
    }

}
