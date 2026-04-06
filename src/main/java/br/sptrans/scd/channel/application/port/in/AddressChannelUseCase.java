package br.sptrans.scd.channel.application.port.in;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.domain.AddressChannel;

public interface AddressChannelUseCase {

    AddressChannel createAddressChannel(CreateAddressChannelCommand command);

    AddressChannel updateAddressChannel(String codEndereco, UpdateAddressChannelCommand command);

    AddressChannel findByAddressChannel(String codEndereco);

    Page<AddressChannel> findAllAddressChannels(String codCanal, Pageable pageable);

    void deleteAddressChannel(String codEndereco);

    // ── Commands ──────────────────────────────────────────────────────────────

    record CreateAddressChannelCommand(
            String codEndereco,
            String codEmpregador,
            String desLogradouro,
            String codFornecedor,
            String codTipoEndereco,
            String codCEP,
            String desBairro,
            String desCidade,
            String desUF,
            Integer numDDD,
            Integer numFone,
            Integer numFax,
            String desObs,
            String stEnderecos,
            String desNumero,
            String codCanal,
            User usuario) {}

    record UpdateAddressChannelCommand(
            String codEmpregador,
            String desLogradouro,
            String codFornecedor,
            String codTipoEndereco,
            String codCEP,
            String desBairro,
            String desCidade,
            String desUF,
            Integer numDDD,
            Integer numFone,
            Integer numFax,
            String desObs,
            String stEnderecos,
            String desNumero,
            String codCanal,
            Long idUsuario) {}
}
