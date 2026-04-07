package br.sptrans.scd.channel.adapter.in.rest.dto;

import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProductChannelRequest(
    @NotBlank @Size(max = 20) String codCanal,
    @NotBlank @Size(max = 20) String codProduto,
    @NotNull Integer qtdLimiteComercializacao,
    @NotNull @Min(0) Integer qtdMinimaEstoque,
    @NotNull Integer qtdMaximaEstoque,
    @NotNull @Min(0) Integer qtdMinimaRessuprimento,
    @NotNull Integer qtdMaximaRessuprimento,
    @NotNull @Min(1) @Max(999) Integer codOrgaoEmissor,
    @NotNull @Min(1) @Max(99) Integer vlFace,
    @NotNull ChannelDomainStatus codStatus,
    @NotNull @Min(1) @Max(999) Integer codConvenio,
    @NotNull @Min(1) @Max(999) Integer codTipoOperHM,
    @NotBlank @Size(min = 1, max = 1, message = "A caracteristica é até 1 caractere") String flgCarac
) {}