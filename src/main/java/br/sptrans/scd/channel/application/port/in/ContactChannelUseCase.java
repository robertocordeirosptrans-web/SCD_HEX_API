package br.sptrans.scd.channel.application.port.in;

import java.util.List;

import br.sptrans.scd.channel.domain.ContactChannel;

public interface ContactChannelUseCase {

    ContactChannel createContactChannel(CreateContactChannelCommand command);

    ContactChannel updateContactChannel(String codContato, UpdateContactChannelCommand command);

    ContactChannel findByContactChannel(String codContato);

    List<ContactChannel> findAllContactChannels(String codCanal);

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
            Long idUsuario) {}

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
