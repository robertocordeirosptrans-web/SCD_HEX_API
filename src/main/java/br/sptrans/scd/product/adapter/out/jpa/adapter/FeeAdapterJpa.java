package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.product.adapter.out.jpa.mapper.AdministrativeFeeMapper;
import br.sptrans.scd.product.adapter.out.jpa.mapper.ChannelFeeMapper;
import br.sptrans.scd.product.adapter.out.jpa.mapper.DestinyFeeMapper;
import br.sptrans.scd.product.adapter.out.jpa.mapper.FeeMapper;
import br.sptrans.scd.product.adapter.out.jpa.mapper.ServiceFeeMapper;
import br.sptrans.scd.product.adapter.out.jpa.repository.AdministrativeFeeJpaRepository;
import br.sptrans.scd.product.adapter.out.jpa.repository.ChannelFeeJpaRepository;
import br.sptrans.scd.product.adapter.out.jpa.repository.DestinyFeeJpaRepository;
import br.sptrans.scd.product.adapter.out.jpa.repository.FeeJpaRepository;
import br.sptrans.scd.product.adapter.out.jpa.repository.ServiceFeeJpaRepository;
import br.sptrans.scd.product.adapter.out.persistence.entity.AdministrativeFeeEntityJpa;
import br.sptrans.scd.product.adapter.out.persistence.entity.ChannelFeeEntityJpa;
import br.sptrans.scd.product.adapter.out.persistence.entity.ChannelFeeKeyEntityJpa;
import br.sptrans.scd.product.adapter.out.persistence.entity.DestinyFeeEntityJpa;
import br.sptrans.scd.product.adapter.out.persistence.entity.FeeEntityJpa;
import br.sptrans.scd.product.adapter.out.persistence.entity.ServiceFeeEntityJpa;
import br.sptrans.scd.product.application.port.out.repository.FeePersistencePort;
import br.sptrans.scd.product.domain.AdministrativeFee;
import br.sptrans.scd.product.domain.ChannelFee;
import br.sptrans.scd.product.domain.ChannelFeeKey;
import br.sptrans.scd.product.domain.DestinyFee;
import br.sptrans.scd.product.domain.Fee;
import br.sptrans.scd.product.domain.ServiceFee;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FeeAdapterJpa implements FeePersistencePort {

    private final FeeJpaRepository feeRepository;
    private final AdministrativeFeeJpaRepository administrativeFeeRepository;
    private final ServiceFeeJpaRepository serviceFeeRepository;
    private final ChannelFeeJpaRepository channelFeeRepository;
    private final DestinyFeeJpaRepository destinyFeeRepository;
    private final FeeMapper feeMapper;
    private final AdministrativeFeeMapper administrativeFeeMapper;
    private final ServiceFeeMapper serviceFeeMapper;
    private final ChannelFeeMapper channelFeeMapper;
    private final DestinyFeeMapper destinyFeeMapper;

    // ================= Fee =================
    @Override
    public Fee save(Fee taxa) {
        FeeEntityJpa entity = feeMapper.toEntity(taxa);
        FeeEntityJpa saved = feeRepository.save(entity);
        return feeMapper.toDomain(saved);
    }

    @Override
    public Optional<Fee> findByIdFee(Long codTaxa) {
        return feeRepository.findById(codTaxa).map(feeMapper::toDomain);
    }

    @Override
    public List<Fee> findByCanal(String codCanal) {
        return feeRepository.findAll().stream()
                .filter(f -> codCanal.equals(f.getCodCanal()))
                .map(feeMapper::toDomain)
                .toList();
    }

    @Override
    public List<Fee> findByProduto(String codProduto) {
        return feeRepository.findAll().stream()
                .filter(f -> codProduto.equals(f.getCodProduto()))
                .map(feeMapper::toDomain)
                .toList();
    }

    @Override
    public List<Fee> findByCanalProduto(String codCanal, String codProduto) {
        return feeRepository.findAll().stream()
                .filter(f -> codCanal.equals(f.getCodCanal()) && codProduto.equals(f.getCodProduto()))
                .map(feeMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Fee> findAtivaByCanalProduto(String codCanal, String codProduto) {
        return feeRepository.findAll().stream()
                .filter(f -> codCanal.equals(f.getCodCanal()) && codProduto.equals(f.getCodProduto()))
                .map(feeMapper::toDomain)
                .findFirst();
    }

    @Override
    public boolean existsSrvById(Long id) {
        return serviceFeeRepository.existsById(id);
    }

    @Override
    public boolean existsById(Long codTaxa) {
        return feeRepository.existsById(codTaxa);
    }

    // ========== AdministrativeFee ==========
    @Override
    public Optional<AdministrativeFee> findAdmById(Long codTaxaAdm) {
        return administrativeFeeRepository.findById(codTaxaAdm).map(administrativeFeeMapper::toDomain);
    }

    @Override
    public AdministrativeFee save(AdministrativeFee taxasAdm) {
        AdministrativeFeeEntityJpa entity = administrativeFeeMapper.toEntity(taxasAdm);
        AdministrativeFeeEntityJpa saved = administrativeFeeRepository.save(entity);
        return administrativeFeeMapper.toDomain(saved);
    }

    // ========== ServiceFee ==========
    @Override
    public Optional<ServiceFee> findSrvById(Long codTaxaSrv) {
        return serviceFeeRepository.findById(codTaxaSrv).map(serviceFeeMapper::toDomain);
    }

    @Override
    public ServiceFee save(ServiceFee taxasServico) {
        ServiceFeeEntityJpa entity = serviceFeeMapper.toEntity(taxasServico);
        ServiceFeeEntityJpa saved = serviceFeeRepository.save(entity);
        return serviceFeeMapper.toDomain(saved);
    }

    // ========== ChannelFee ==========
    @Override
    public Optional<ChannelFee> findById(ChannelFeeKey id) {
        ChannelFeeKeyEntityJpa entityKey = channelFeeMapper.toEntityKey(id);
        return channelFeeRepository.findById(entityKey).map(channelFeeMapper::toDomain);
    }

    @Override
    public ChannelFee save(ChannelFee taxasScanal) {
        ChannelFeeEntityJpa entity = channelFeeMapper.toEntity(taxasScanal);
        ChannelFeeEntityJpa saved = channelFeeRepository.save(entity);
        return channelFeeMapper.toDomain(saved);
    }

    @Override
    public boolean existsByKey(ChannelFeeKey id) {
        ChannelFeeKeyEntityJpa entityKey = channelFeeMapper.toEntityKey(id);
        return channelFeeRepository.existsById(entityKey);
    }

    // ========== DestinyFee ==========
    @Override
    public Optional<DestinyFee> findDesById(Long id) {
        return destinyFeeRepository.findById(id).map(destinyFeeMapper::toDomain);
    }

    @Override
    public DestinyFee save(DestinyFee taxasDes) {
        DestinyFeeEntityJpa entity = destinyFeeMapper.toEntity(taxasDes);
        DestinyFeeEntityJpa saved = destinyFeeRepository.save(entity);
        return destinyFeeMapper.toDomain(saved);
    }

    @Override
    public boolean existsByDesId(DestinyFee id) {
        if (id.getCodTaxaDes() == null) {
            return false;
        }
        return destinyFeeRepository.existsById(id.getCodTaxaDes());
    }
}
