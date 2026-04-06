package br.sptrans.scd.channel.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class AddressChannel {

    @Setter private String codEndereco;
    @Setter private String codEmpregador;
    @Setter private String desLogradouro;
    @Setter private String codFornecedor;
    @Setter private String codTipoEndereco;
    @Setter private String codCEP;
    @Setter private String desBairro;
    @Setter private String desCidade;
    @Setter private String desUF;
    @Setter private Integer numDDD;
    @Setter private Integer numFone;
    @Setter private Integer numFax;
    @Setter private String desObs;
    @Setter private LocalDateTime dtCadastro;
    @Setter private LocalDateTime dtManutencao;
    @Setter private String stEnderecos;
    @Setter private LocalDateTime dtValidade;
    @Setter private Integer codSeq;
    @Setter private String desNumero;
    @Setter private User idUsuarioManutencao;
    @Setter private User idUsuarioCadastro;
    @Setter private SalesChannel codCanal;

    // -------------------------------------------------------------------------
    // Consultas de status
    // -------------------------------------------------------------------------

    public boolean isAtivo() {
        try {
            return ChannelDomainStatus.ACTIVE.equals(ChannelDomainStatus.fromCode(stEnderecos));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isInativo() {
        try {
            return ChannelDomainStatus.INACTIVE.equals(ChannelDomainStatus.fromCode(stEnderecos));
        } catch (Exception e) {
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Transições de status
    // -------------------------------------------------------------------------

    /**
     * Ativa o endereço do canal.
     *
     * @param operador usuário responsável pela operação
     */
    public void activate(User operador) {
        this.stEnderecos = ChannelDomainStatus.ACTIVE.getCode();
        this.idUsuarioManutencao = operador;
        this.dtManutencao = LocalDateTime.now();
    }

    /**
     * Inativa o endereço do canal.
     *
     * @param operador usuário responsável pela operação
     */
    public void inactivate(User operador) {
        this.stEnderecos = ChannelDomainStatus.INACTIVE.getCode();
        this.idUsuarioManutencao = operador;
        this.dtManutencao = LocalDateTime.now();
    }

    // -------------------------------------------------------------------------
    // Atualização de dados
    // -------------------------------------------------------------------------

    /**
     * Atualiza os dados do endereço.
     *
     * @param desLogradouro  novo logradouro
     * @param desNumero      novo número
     * @param desBairro      novo bairro
     * @param desCidade      nova cidade
     * @param desUF          nova UF
     * @param codCEP         novo CEP
     * @param numDDD         novo DDD
     * @param numFone        novo telefone
     * @param operador       usuário responsável pela operação
     */
    public void updateInfo(
            String desLogradouro,
            String desNumero,
            String desBairro,
            String desCidade,
            String desUF,
            String codCEP,
            Integer numDDD,
            Integer numFone,
            User operador) {
        this.desLogradouro = desLogradouro;
        this.desNumero = desNumero;
        this.desBairro = desBairro;
        this.desCidade = desCidade;
        this.desUF = desUF;
        this.codCEP = codCEP;
        this.numDDD = numDDD;
        this.numFone = numFone;
        this.idUsuarioManutencao = operador;
        this.dtManutencao = LocalDateTime.now();
    }
}
