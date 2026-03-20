package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

import br.sptrans.scd.channel.application.port.out.AddressChannelRepository;
import br.sptrans.scd.channel.domain.AddressChannel;
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.AddressChannelJpaRepository;
import br.sptrans.scd.channel.adapter.port.out.jpa.entity.AddressChannelEntityJpa;


@Repository
@RequiredArgsConstructor
public class AddressChannelAdapterJpa implements AddressChannelRepository {
    private final AddressChannelJpaRepository repository;

    @Override
    public Optional<AddressChannel> findById(String codEndereco) {
        return repository.findByCodEndereco(codEndereco)
                .map(this::toDomain);
    }

    @Override
    public boolean existsById(String codEndereco) {
        return repository.existsByCodEndereco(codEndereco);
    }

    @Override
    public List<AddressChannel> findAllByCanal(String codCanal) {
        if (codCanal != null && !codCanal.isBlank()) {
            return repository.findAllByCodCanal(codCanal).stream().map(this::toDomain).toList();
        }
        return repository.findAllOrderByCodEndereco().stream().map(this::toDomain).toList();
    }

    @Override
    public AddressChannel save(AddressChannel ac) {
        AddressChannelEntityJpa entity = toEntity(ac);
        AddressChannelEntityJpa saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(String codEndereco) {
        repository.deleteById(codEndereco);
    }

    private AddressChannel toDomain(AddressChannelEntityJpa entity) {
        if (entity == null) return null;
        br.sptrans.scd.auth.domain.User manutencao = null;
        if (entity.getIdUsuarioManutencao() != null) {
            manutencao = new br.sptrans.scd.auth.domain.User();
            manutencao.setIdUsuario(entity.getIdUsuarioManutencao());
        }
        br.sptrans.scd.auth.domain.User cadastro = null;
        if (entity.getIdUsuarioCadastro() != null) {
            cadastro = new br.sptrans.scd.auth.domain.User();
            cadastro.setIdUsuario(entity.getIdUsuarioCadastro());
        }
        br.sptrans.scd.channel.domain.SalesChannel canal = null;
        if (entity.getCodCanal() != null) {
            canal = new br.sptrans.scd.channel.domain.SalesChannel();
            canal.setCodCanal(entity.getCodCanal());
        }
        return new AddressChannel(
                entity.getCodEndereco(),
                entity.getCodEmpregador(),
                entity.getDesLogradouro(),
                entity.getCodFornecedor(),
                entity.getCodTipoEndereco(),
                entity.getCodCEP(),
                entity.getDesBairro(),
                entity.getDesCidade(),
                entity.getDesUF(),
                entity.getNumDDD(),
                entity.getNumFone(),
                entity.getNumFax(),
                entity.getDesObs(),
                entity.getDtCadastro(),
                entity.getDtManutencao(),
                entity.getStEnderecos(),
                entity.getDtValidade(),
                entity.getCodSeq(),
                entity.getDesNumero(),
                manutencao,
                cadastro,
                canal
        );
    }

    private AddressChannelEntityJpa toEntity(AddressChannel domain) {
        if (domain == null) return null;
        AddressChannelEntityJpa entity = new AddressChannelEntityJpa();
        entity.setCodEndereco(domain.getCodEndereco());
        entity.setCodEmpregador(domain.getCodEmpregador());
        entity.setDesLogradouro(domain.getDesLogradouro());
        entity.setCodFornecedor(domain.getCodFornecedor());
        entity.setCodTipoEndereco(domain.getCodTipoEndereco());
        entity.setCodCEP(domain.getCodCEP());
        entity.setDesBairro(domain.getDesBairro());
        entity.setDesCidade(domain.getDesCidade());
        entity.setDesUF(domain.getDesUF());
        entity.setNumDDD(domain.getNumDDD());
        entity.setNumFone(domain.getNumFone());
        entity.setNumFax(domain.getNumFax());
        entity.setDesObs(domain.getDesObs());
        entity.setDtCadastro(domain.getDtCadastro());
        entity.setDtManutencao(domain.getDtManutencao());
        entity.setStEnderecos(domain.getStEnderecos());
        entity.setDtValidade(domain.getDtValidade());
        entity.setCodSeq(domain.getCodSeq());
        entity.setDesNumero(domain.getDesNumero());
        entity.setIdUsuarioManutencao(domain.getIdUsuarioManutencao() != null ? domain.getIdUsuarioManutencao().getIdUsuario() : null);
        entity.setIdUsuarioCadastro(domain.getIdUsuarioCadastro() != null ? domain.getIdUsuarioCadastro().getIdUsuario() : null);
        entity.setCodCanal(domain.getCodCanal() != null ? domain.getCodCanal().getCodCanal() : null);
        return entity;
    }
}
