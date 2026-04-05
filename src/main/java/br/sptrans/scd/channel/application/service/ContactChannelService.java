package br.sptrans.scd.channel.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.application.port.in.ContactChannelUseCase;
import br.sptrans.scd.channel.application.port.out.ContactChannelRepository;
import br.sptrans.scd.channel.domain.ContactChannel;
import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.channel.domain.enums.ChannelErrorType;
import br.sptrans.scd.channel.domain.exception.ChannelException;
import br.sptrans.scd.shared.helper.UserResolverHelperImpl;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ContactChannelService implements ContactChannelUseCase {

    private final ContactChannelRepository contactChannelRepository;
    private final UserResolverHelperImpl userResolverHelper;

    @Override
    public ContactChannel createContactChannel(CreateContactChannelCommand cmd) {
        if (contactChannelRepository.existsById(cmd.codContato())) {
            throw new ChannelException(ChannelErrorType.CONTACT_CHANNEL_CODE_ALREADY_EXISTS);
        }

        User usuario = userResolverHelper.resolve(cmd.idUsuario());

        ContactChannel contactChannel = new ContactChannel(
                cmd.codContato(),
                cmd.codFornecedor(),
                cmd.codEmpregador(),
                cmd.desContato(),
                cmd.desEmailContato(),
                cmd.numDDD(),
                cmd.numFone(),
                cmd.numFoneRamal(),
                cmd.numFax(),
                cmd.numFaxRamal(),
                cmd.stEntidadeContato(),
                cmd.desComentarios(),
                cmd.codTipoDocumento(),
                cmd.codDocumento(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                usuario,
                cmd.codCanal() != null
                        ? new SalesChannel(cmd.codCanal(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
                        : null
        );

        return contactChannelRepository.save(contactChannel);
    }

    @Override
    public ContactChannel updateContactChannel(String codContato, UpdateContactChannelCommand cmd) {
        ContactChannel existing = contactChannelRepository.findById(codContato)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.CONTACT_CHANNEL_NOT_FOUND));

        User usuario = userResolverHelper.resolve(cmd.idUsuario());

        ContactChannel updated = new ContactChannel(
                existing.getCodContato(),
                cmd.codFornecedor(),
                cmd.codEmpregador(),
                cmd.desContato(),
                cmd.desEmailContato(),
                cmd.numDDD(),
                cmd.numFone(),
                cmd.numFoneRamal(),
                cmd.numFax(),
                cmd.numFaxRamal(),
                cmd.stEntidadeContato(),
                cmd.desComentarios(),
                cmd.codTipoDocumento(),
                cmd.codDocumento(),
                existing.getDtCadastro(),
                LocalDateTime.now(),
                usuario,
                existing.getIdUsuarioCadastro(),
                cmd.codCanal() != null
                        ? new SalesChannel(cmd.codCanal(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
                        : null
        );

        return contactChannelRepository.save(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ContactChannel findByContactChannel(String codContato) {
        return contactChannelRepository.findById(codContato)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.CONTACT_CHANNEL_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactChannel> findAllContactChannels(String codCanal) {
        return contactChannelRepository.findAllByCanal(codCanal);
    }

    @Override
    public void deleteContactChannel(String codContato) {
        if (!contactChannelRepository.existsById(codContato)) {
            throw new ChannelException(ChannelErrorType.CONTACT_CHANNEL_NOT_FOUND);
        }
        contactChannelRepository.deleteById(codContato);
    }

}
