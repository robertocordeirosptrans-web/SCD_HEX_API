package br.sptrans.scd.channel.adapter.in.rest.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAddressChannelRequest(
                String codEndereco,
                @Size(max = 20, message = "Código do empregador deve ter no máximo 20 caracteres") String codEmpregador,
                @Size(max = 60, message = "Logradouro deve ter no máximo 60 caracteres") String desLogradouro,
                @Size(max = 20, message = "Código do fornecedor deve ter no máximo 20 caracteres") String codFornecedor,
                @Size(max = 20, message = "Tipo de endereço deve ter no máximo 20 caracteres") String codTipoEndereco,
                @Size(max = 20, message = "CEP deve ter no máximo 20 caracteres") String codCEP,
                @Size(max = 60, message = "Bairro deve ter no máximo 60 caracteres") String desBairro,
                @Size(max = 60, message = "Cidade deve ter no máximo 60 caracteres") String desCidade,
                @Size(max = 60, message = "UF deve ter no máximo 60 caracteres") String desUF,
                Integer numDDD,
                Integer numFone,
                Integer numFax,
                @Size(max = 1024, message = "Observações deve ter no máximo 1024 caracteres") String desObs,
                LocalDateTime dtCadastro,
                LocalDateTime dtManutencao,
                @Size(max = 1, message = "Status deve ter no máximo 1 caractere") String stEnderecos,
                LocalDateTime dtValidade,
                @NotNull(message = "Código sequencial é obrigatório") Integer codSeq,
                @Size(max = 10, message = "Número deve ter no máximo 10 caracteres") String desNumero,
                @JsonProperty("idUsuarioManutencao") Long idUsuarioManutencaoId,
                @JsonProperty("idUsuarioCadastro") Long idUsuarioCadastroId,
                @JsonProperty("codCanal") String codCanalId) {
}