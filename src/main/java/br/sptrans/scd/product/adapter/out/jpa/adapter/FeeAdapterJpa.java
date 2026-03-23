package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.product.adapter.out.jpa.entity.AdministrativeFeeEntityJpa;
import br.sptrans.scd.product.adapter.out.jpa.entity.ChannelFeeEntityJpa;
import br.sptrans.scd.product.adapter.out.jpa.entity.ChannelFeeKeyEntityJpa;
import br.sptrans.scd.product.adapter.out.jpa.entity.DestinyFeeEntityJpa;
import br.sptrans.scd.product.adapter.out.jpa.entity.FeeEntityJpa;
import br.sptrans.scd.product.adapter.out.jpa.entity.ServiceFeeEntityJpa;
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
import br.sptrans.scd.product.application.port.out.AdministrativeFeeRepository;
import br.sptrans.scd.product.application.port.out.ChannelFeeRepository;
import br.sptrans.scd.product.application.port.out.DestinyFeeRepository;
import br.sptrans.scd.product.application.port.out.FeeRepository;
import br.sptrans.scd.product.application.port.out.ServiceFeeRepository;
import br.sptrans.scd.product.domain.AdministrativeFee;
import br.sptrans.scd.product.domain.ChannelFee;
import br.sptrans.scd.product.domain.ChannelFeeKey;
import br.sptrans.scd.product.domain.DestinyFee;
import br.sptrans.scd.product.domain.Fee;
import br.sptrans.scd.product.domain.ServiceFee;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Repository
@RequiredArgsConstructor
@Transactional
public class FeeAdapterJpa implements AdministrativeFeeRepository, FeeRepository, ChannelFeeRepository, DestinyFeeRepository, ServiceFeeRepository {

    private final FeeJpaRepository feeRepository;
    private final AdministrativeFeeJpaRepository administrativeFeeRepository;
    private final ServiceFeeJpaRepository serviceFeeRepository;
    private final ChannelFeeJpaRepository channelFeeRepository;
    private final DestinyFeeJpaRepository destinyFeeRepository;

    // ================= Fee =================
    @Override
    public Fee save(Fee taxa) {
        FeeEntityJpa entity = FeeMapper.toEntity(taxa);
        FeeEntityJpa saved = feeRepository.save(entity);
        return FeeMapper.toDomain(saved);
    }

    @Override
    public Optional<Fee> findByIdFee(Long codTaxa) {
        return feeRepository.findById(codTaxa).map(FeeMapper::toDomain);
    }

    @Override
    public List<Fee> findByCanal(String codCanal) {
        return feeRepository.findAll().stream()
                .filter(f -> codCanal.equals(f.getCodCanal()))
                .map(FeeMapper::toDomain)
                .toList();
    }

    @Override
    public List<Fee> findByProduto(String codProduto) {
        return feeRepository.findAll().stream()
                .filter(f -> codProduto.equals(f.getCodProduto()))
                .map(FeeMapper::toDomain)
                .toList();
    }

    @Override
    public List<Fee> findByCanalProduto(String codCanal, String codProduto) {
        return feeRepository.findAll().stream()
                .filter(f -> codCanal.equals(f.getCodCanal()) && codProduto.equals(f.getCodProduto()))
                .map(FeeMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Fee> findAtivaByCanalProduto(String codCanal, String codProduto) {
        return feeRepository.findAll().stream()
                .filter(f -> codCanal.equals(f.getCodCanal()) && codProduto.equals(f.getCodProduto()))
                .map(FeeMapper::toDomain)
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
        return administrativeFeeRepository.findById(codTaxaAdm).map(AdministrativeFeeMapper::toDomain);
    }

    @Override
    public AdministrativeFee save(AdministrativeFee taxasAdm) {
        AdministrativeFeeEntityJpa entity = AdministrativeFeeMapper.toEntity(taxasAdm);
        AdministrativeFeeEntityJpa saved = administrativeFeeRepository.save(entity);
        return AdministrativeFeeMapper.toDomain(saved);
    }

    // ========== ServiceFee ==========
    @Override
    public Optional<ServiceFee> findSrvById(Long codTaxaSrv) {
        return serviceFeeRepository.findById(codTaxaSrv).map(ServiceFeeMapper::toDomain);
    }

    @Override
    public ServiceFee save(ServiceFee taxasServico) {
        ServiceFeeEntityJpa entity = ServiceFeeMapper.toEntity(taxasServico);
        ServiceFeeEntityJpa saved = serviceFeeRepository.save(entity);
        return ServiceFeeMapper.toDomain(saved);
    }

    // ========== ChannelFee ==========
    @Override
    public Optional<ChannelFee> findById(ChannelFeeKey id) {
        ChannelFeeKeyEntityJpa entityKey = ChannelFeeMapper.toEntityKey(id);
        return channelFeeRepository.findById(entityKey).map(ChannelFeeMapper::toDomain);
    }

    @Override
    public ChannelFee save(ChannelFee taxasScanal) {
        ChannelFeeEntityJpa entity = ChannelFeeMapper.toEntity(taxasScanal);
        ChannelFeeEntityJpa saved = channelFeeRepository.save(entity);
        return ChannelFeeMapper.toDomain(saved);
    }

    @Override
    public boolean existsByKey(ChannelFeeKey id) {
        ChannelFeeKeyEntityJpa entityKey = ChannelFeeMapper.toEntityKey(id);
        return channelFeeRepository.existsById(entityKey);
    }

    // ========== DestinyFee ==========
    @Override
    public Optional<DestinyFee> findDesById(Long id) {
        return destinyFeeRepository.findById(id).map(DestinyFeeMapper::toDomain);
    }

    @Override
    public DestinyFee save(DestinyFee taxasDes) {
        DestinyFeeEntityJpa entity = DestinyFeeMapper.toEntity(taxasDes);
        DestinyFeeEntityJpa saved = destinyFeeRepository.save(entity);
        return DestinyFeeMapper.toDomain(saved);
    }

    @Override
    public boolean existsByDesId(DestinyFee id) {
        if (id.getCodTaxaDes() == null) {
            return false;
        }
        return destinyFeeRepository.existsById(id.getCodTaxaDes());
    }
}
