
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
import br.sptrans.scd.channel.application.port.in.AddressChannelUseCase;
import br.sptrans.scd.channel.application.port.out.AddressChannelPersistencePort;
import br.sptrans.scd.channel.application.port.out.SalesChannelPersistencePort;
import br.sptrans.scd.channel.domain.AddressChannel;
import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.channel.domain.enums.ChannelErrorType;
import br.sptrans.scd.channel.domain.exception.ChannelException;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressChannelService implements AddressChannelUseCase {

    private static final Logger log = LoggerFactory.getLogger(AddressChannelService.class);

    private final AddressChannelPersistencePort addressChannelRepository;
    private final SalesChannelPersistencePort salesChannelRepository;
    private final UserResolverHelper userResolverHelper;

    @Override
    public AddressChannel createAddressChannel(CreateAddressChannelCommand cmd) {
        log.info("Criando endereço do canal. Código: {}", cmd.codEndereco());
        if (addressChannelRepository.existsById(cmd.codEndereco())) {
            throw new ChannelException(ChannelErrorType.ADDRESS_CHANNEL_CODE_ALREADY_EXISTS);
        }

        SalesChannel channel = salesChannelRepository.findById(cmd.codCanal())
                .orElseThrow(() -> new ChannelException(ChannelErrorType.SALES_CHANNEL_NOT_FOUND));

        User usuario = cmd.usuario();

        AddressChannel addressChannel = new AddressChannel(
                cmd.codEndereco(),
                cmd.codEmpregador(),
                cmd.desLogradouro(),
                cmd.codFornecedor(),
                cmd.codTipoEndereco(),
                cmd.codCEP(),
                cmd.desBairro(),
                cmd.desCidade(),
                cmd.desUF(),
                cmd.numDDD(),
                cmd.numFone(),
                cmd.numFax(),
                cmd.desObs(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                cmd.stEnderecos(),
                null,
                null,
                cmd.desNumero(),
                null,
                usuario,
                channel);

        AddressChannel saved = addressChannelRepository.save(addressChannel);
        log.info("Endereço do canal criado. Código: {}", saved.getCodEndereco());
        return saved;
    }

    @Override
    public AddressChannel updateAddressChannel(String codEndereco, UpdateAddressChannelCommand cmd) {
        log.info("Atualizando endereço do canal. Código: {}", codEndereco);
        AddressChannel existing = addressChannelRepository.findById(codEndereco)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.ADDRESS_CHANNEL_NOT_FOUND));

        SalesChannel channel = salesChannelRepository.findById(cmd.codCanal())
                .orElseThrow(() -> new ChannelException(ChannelErrorType.SALES_CHANNEL_NOT_FOUND));

        User usuario = userResolverHelper.resolve(cmd.idUsuario());

        AddressChannel updated = new AddressChannel(
                existing.getCodEndereco(),
                cmd.codEmpregador(),
                cmd.desLogradouro(),
                cmd.codFornecedor(),
                cmd.codTipoEndereco(),
                cmd.codCEP(),
                cmd.desBairro(),
                cmd.desCidade(),
                cmd.desUF(),
                cmd.numDDD(),
                cmd.numFone(),
                cmd.numFax(),
                cmd.desObs(),
                existing.getDtCadastro(),
                LocalDateTime.now(),
                cmd.stEnderecos(),
                existing.getDtValidade(),
                existing.getCodSeq(),
                cmd.desNumero(),
                existing.getIdUsuarioCadastro(),
                usuario,
                channel);

        AddressChannel saved = addressChannelRepository.save(updated);
        log.info("Endereço do canal atualizado. Código: {}", codEndereco);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "canais", key = "'address-' + #codEndereco")
    public AddressChannel findByAddressChannel(String codEndereco) {
        return addressChannelRepository.findById(codEndereco)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.ADDRESS_CHANNEL_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "canais", key = "'address-all-' + #codCanal + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<AddressChannel> findAllAddressChannels(String codCanal, Pageable pageable) {
        return addressChannelRepository.findAllByCanal(codCanal, pageable);
    }

    @Override
    @CacheEvict(value = "canais", key = "'address-' + #codEndereco")
    public void deleteAddressChannel(String codEndereco) {
        log.info("Removendo endereço do canal. Código: {}", codEndereco);
        if (!addressChannelRepository.existsById(codEndereco)) {
            throw new ChannelException(ChannelErrorType.ADDRESS_CHANNEL_NOT_FOUND);
        }
        addressChannelRepository.deleteById(codEndereco);
        log.info("Endereço do canal removido. Código: {}", codEndereco);
    }

}
