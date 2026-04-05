package br.sptrans.scd.channel.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.application.port.in.AddressChannelUseCase;
import br.sptrans.scd.channel.application.port.out.AddressChannelPersistencePort;
import br.sptrans.scd.channel.domain.AddressChannel;
import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.channel.domain.enums.ChannelErrorType;
import br.sptrans.scd.channel.domain.exception.ChannelException;
import br.sptrans.scd.shared.helper.UserResolverHelperImpl;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressChannelService implements AddressChannelUseCase {

    private final AddressChannelPersistencePort addressChannelRepository;
    private final UserResolverHelperImpl userResolverHelper;

    @Override
    public AddressChannel createAddressChannel(CreateAddressChannelCommand cmd) {
        if (addressChannelRepository.existsById(cmd.codEndereco())) {
            throw new ChannelException(ChannelErrorType.ADDRESS_CHANNEL_CODE_ALREADY_EXISTS);
        }

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
                cmd.codCanal() != null
                        ? new SalesChannel(cmd.codCanal(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
                        : null
        );

        return addressChannelRepository.save(addressChannel);
    }

    @Override
    public AddressChannel updateAddressChannel(String codEndereco, UpdateAddressChannelCommand cmd) {
        AddressChannel existing = addressChannelRepository.findById(codEndereco)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.ADDRESS_CHANNEL_NOT_FOUND));

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
                cmd.codCanal() != null
                        ? new SalesChannel(cmd.codCanal(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
                        : null
        );

        return addressChannelRepository.save(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressChannel findByAddressChannel(String codEndereco) {
        return addressChannelRepository.findById(codEndereco)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.ADDRESS_CHANNEL_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressChannel> findAllAddressChannels(String codCanal) {
        return addressChannelRepository.findAllByCanal(codCanal);
    }

    @Override
    public void deleteAddressChannel(String codEndereco) {
        if (!addressChannelRepository.existsById(codEndereco)) {
            throw new ChannelException(ChannelErrorType.ADDRESS_CHANNEL_NOT_FOUND);
        }
        addressChannelRepository.deleteById(codEndereco);
    }

}
