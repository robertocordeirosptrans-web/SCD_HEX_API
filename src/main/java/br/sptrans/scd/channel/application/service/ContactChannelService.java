package br.sptrans.scd.channel.application.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.application.port.in.ContactChannelUseCase;
import br.sptrans.scd.channel.application.port.out.ContactChannelPersistencePort;
import br.sptrans.scd.channel.application.port.out.SalesChannelPersistencePort;
import br.sptrans.scd.channel.domain.ContactChannel;
import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.channel.domain.enums.ChannelErrorType;
import br.sptrans.scd.channel.domain.exception.ChannelException;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ContactChannelService implements ContactChannelUseCase {

    private static final Logger log = LoggerFactory.getLogger(ContactChannelService.class);

    private final ContactChannelPersistencePort contactChannelRepository;
    private final SalesChannelPersistencePort salesChannelRepository;
    private final UserResolverHelper userResolverHelper;

    @Override
    public ContactChannel createContactChannel(CreateContactChannelCommand cmd) {
        log.info("Criando contato do canal. Código: {}", cmd.codContato());
        if (contactChannelRepository.existsById(cmd.codContato())) {
            throw new ChannelException(ChannelErrorType.CONTACT_CHANNEL_CODE_ALREADY_EXISTS);
        }

        User usuario = cmd.usuario();
        SalesChannel channel = salesChannelRepository.findById(cmd.codCanal())
                .orElseThrow(() -> new ChannelException(ChannelErrorType.SALES_CHANNEL_NOT_FOUND));

        ContactChannel contactChannel = ContactChannel.criar(
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
                channel);
        ContactChannel saved = contactChannelRepository.save(contactChannel);
        log.info("Contato do canal criado. Código: {}", saved.getCodContato());
        return saved;
    }

    @Override
    public ContactChannel updateContactChannel(String codContato, UpdateContactChannelCommand cmd) {
        log.info("Atualizando contato do canal. Código: {}", codContato);
        ContactChannel existing = contactChannelRepository.findById(codContato)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.CONTACT_CHANNEL_NOT_FOUND));

        User usuario = userResolverHelper.resolve(cmd.idUsuario());
        SalesChannel channel = salesChannelRepository.findById(cmd.codCanal())
                .orElseThrow(() -> new ChannelException(ChannelErrorType.SALES_CHANNEL_NOT_FOUND));
                
        existing.atualizar(
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
                usuario,
                channel);
        ContactChannel saved = contactChannelRepository.save(existing);
        log.info("Contato do canal atualizado. Código: {}", codContato);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "canais", key = "'contact-' + #codContato")
    public ContactChannel findByContactChannel(String codContato) {
        return contactChannelRepository.findById(codContato)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.CONTACT_CHANNEL_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "canais", key = "'contact-all-' + #codCanal + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ContactChannel> findAllContactChannels(String codCanal, Pageable pageable) {
        return contactChannelRepository.findAllByCanal(codCanal, pageable);
    }

    @Override
    @CacheEvict(value = "canais", key = "'contact-' + #codContato")
    public void deleteContactChannel(String codContato) {
        log.info("Removendo contato do canal. Código: {}", codContato);
        if (!contactChannelRepository.existsById(codContato)) {
            throw new ChannelException(ChannelErrorType.CONTACT_CHANNEL_NOT_FOUND);
        }
        contactChannelRepository.deleteById(codContato);
        log.info("Contato do canal removido. Código: {}", codContato);
    }

}
