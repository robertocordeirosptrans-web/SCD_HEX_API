package br.sptrans.scd.channel.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.application.port.in.ProductChannelUseCase;
import br.sptrans.scd.channel.application.port.out.ProductChannelRepository;
import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.channel.domain.ProductChannelKey;
import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.channel.domain.enums.ChannelErrorType;
import br.sptrans.scd.channel.domain.exception.ChannelException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductChannelService implements ProductChannelUseCase {

    private final ProductChannelRepository repository;
    private final UserRepository userRepository;

    @Override
    public ProductChannel createProductChannel(CreateProductChannelCommand cmd) {
        ProductChannelKey key = new ProductChannelKey(cmd.codCanal(), cmd.codProduto());
        if (repository.existsById(key)) {
            throw new ChannelException(ChannelErrorType.PRODUCT_CHANNEL_ALREADY_EXISTS);
        }

        User usuCad = resolveUser(cmd.idUsuarioCadastro());
        SalesChannel canal = new SalesChannel(
                cmd.codCanal(), null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null);

        ProductChannel entity = new ProductChannel();
        entity.setId(key);
        entity.setQtdLimiteComercializacao(cmd.qtdLimiteComercializacao());
        entity.setQtdMinimaEstoque(cmd.qtdMinimaEstoque());
        entity.setQtdMaximaEstoque(cmd.qtdMaximaEstoque());
        entity.setQtdMinimaRessuprimento(cmd.qtdMinimaRessuprimento());
        entity.setQtdMaximaRessuprimento(cmd.qtdMaximaRessuprimento());
        entity.setCodOrgaoEmissor(cmd.codOrgaoEmissor());
        entity.setVlFace(cmd.vlFace());
        entity.setCodStatus(cmd.codStatus());
        entity.setCodConvenio(cmd.codConvenio());
        entity.setTipoOperHM(cmd.tipoOperHM());
        entity.setFlgCarac(cmd.flgCarac());
        entity.setIdUsuarioCadastro(usuCad);
        entity.setCanal(canal);

        return repository.save(entity);
    }

    @Override
    public ProductChannel updateProductChannel(String codCanal, String codProduto,
            UpdateProductChannelCommand cmd) {
        ProductChannelKey key = new ProductChannelKey(codCanal, codProduto);
        ProductChannel existing = repository.findById(key)
                .orElseThrow(() -> new ChannelException(ChannelErrorType.PRODUCT_CHANNEL_NOT_FOUND));

        User usuMan = resolveUser(cmd.idUsuarioManutencao());

        existing.setQtdLimiteComercializacao(cmd.qtdLimiteComercializacao());
        existing.setQtdMinimaEstoque(cmd.qtdMinimaEstoque());
        existing.setQtdMaximaEstoque(cmd.qtdMaximaEstoque());
        existing.setQtdMinimaRessuprimento(cmd.qtdMinimaRessuprimento());
        existing.setQtdMaximaRessuprimento(cmd.qtdMaximaRessuprimento());
        existing.setCodOrgaoEmissor(cmd.codOrgaoEmissor());
        existing.setVlFace(cmd.vlFace());
        existing.setCodStatus(cmd.codStatus());
        existing.setCodConvenio(cmd.codConvenio());
        existing.setTipoOperHM(cmd.tipoOperHM());
        existing.setFlgCarac(cmd.flgCarac());
        existing.setIdUsuarioManutencao(usuMan);

        return repository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductChannel findProductChannel(String codCanal, String codProduto) {
        return repository.findById(new ProductChannelKey(codCanal, codProduto))
                .orElseThrow(() -> new ChannelException(ChannelErrorType.PRODUCT_CHANNEL_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductChannel> findAllProductChannels() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductChannel> findByCodCanal(String codCanal) {
        return repository.findByCodCanal(codCanal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductChannel> findByCodProduto(String codProduto) {
        return repository.findByCodProduto(codProduto);
    }

    @Override
    public void deleteProductChannel(String codCanal, String codProduto) {
        ProductChannelKey key = new ProductChannelKey(codCanal, codProduto);
        if (!repository.existsById(key)) {
            throw new ChannelException(ChannelErrorType.PRODUCT_CHANNEL_NOT_FOUND);
        }
        repository.deleteById(key);
    }

    private User resolveUser(Long idUsuario) {
        if (idUsuario == null) return null;
        return userRepository.findById(idUsuario).orElse(null);
    }
}
