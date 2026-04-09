package br.sptrans.scd.creditrequest.adapter.out.jpa.mapper;

import org.springframework.stereotype.Component;

import br.sptrans.scd.creditrequest.application.port.in.dto.CreditRequestDTO;
import br.sptrans.scd.creditrequest.application.port.out.mapper.CreditRequestDTOMapper;
import br.sptrans.scd.creditrequest.domain.CreditRequest;

@Component
public class CreditRequestDTOMapperImpl implements CreditRequestDTOMapper {
    @Override
    public CreditRequestDTO toDTO(CreditRequest creditRequest) {
        if (creditRequest == null) return null;
        // Mapeamento simples, pode ser expandido conforme necessário
        return CreditRequestDTO.builder()
                .numSolicitacao(creditRequest.getNumSolicitacao())
                .codCanal(creditRequest.getCodCanal())
                .idUsuarioCadastro(creditRequest.getIdUsuarioCadastro())
                .codTipoDocumento(creditRequest.getCodTipoDocumento())
                .codSituacao(creditRequest.getCodSituacao() != null ? creditRequest.getCodSituacao().getCode() : null)
                .codFormaPagto(creditRequest.getCodFormaPagto())
                .dtSolicitacao(creditRequest.getDtSolicitacao())
                .dtPrevLiberacao(creditRequest.getDtPrevLiberacao())
                .dtAceite(creditRequest.getDtAceite())
                .dtConfirmaPagto(creditRequest.getDtConfirmaPagto())
                .dtPagtoEconomica(creditRequest.getDtPagtoEconomica())
                .codUsuarioPortador(creditRequest.getCodUsuarioPortador())
                .dtLiberacaoEfetiva(creditRequest.getDtLiberacaoEfetiva())
                .codEnderecoEntrega(creditRequest.getCodEnderecoEntrega())
                .numLote(creditRequest.getNumLote())
                .dtFinanceira(creditRequest.getDtFinanceira())
                .vlTotal(creditRequest.getVlTotal())
                .dtCadastro(creditRequest.getDtCadastro())
                .flgCanc(creditRequest.getFlgCanc())
                .dtManutencao(creditRequest.getDtManutencao())
                .idUsuarioManutencao(creditRequest.getIdUsuarioManutencao())
                .flgBloq(creditRequest.getFlgBloq())
                .vlPago(creditRequest.getVlPago())
                // .itens(...) // Mapear itens se necessário
                .build();
    }
}
