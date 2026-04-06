package br.sptrans.scd.channel.application.port.in;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.domain.ContactChannel;

public interface ContactChannelUseCase {

    ContactChannel createContactChannel(CreateContactChannelCommand command);

    ContactChannel updateContactChannel(String codContato, UpdateContactChannelCommand command);

    ContactChannel findByContactChannel(String codContato);

    Page<ContactChannel> findAllContactChannels(String codCanal, Pageable pageable);

    void deleteContactChannel(String codContato);

    // ── Commands ──────────────────────────────────────────────────────────────

    record CreateContactChannelCommand(
            String codContato,
            String codFornecedor,
            String codEmpregador,
            String desContato,
            String desEmailContato,
            Integer numDDD,
            Integer numFone,
            Integer numFoneRamal,
            Integer numFax,
            Integer numFaxRamal,
            String stEntidadeContato,
            String desComentarios,
            String codTipoDocumento,
            String codDocumento,
            String codCanal,
            User usuario) {}

    record UpdateContactChannelCommand(
            String codFornecedor,
            String codEmpregador,
            String desContato,
            String desEmailContato,
            Integer numDDD,
            Integer numFone,
            Integer numFoneRamal,
            Integer numFax,
            Integer numFaxRamal,
            String stEntidadeContato,
            String desComentarios,
            String codTipoDocumento,
            String codDocumento,
            String codCanal,
            Long idUsuario) {}
}
