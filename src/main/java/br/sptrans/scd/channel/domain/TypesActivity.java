package br.sptrans.scd.channel.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class TypesActivity {

    private final String codAtividade;

    @Setter private String desAtividade;

    @Setter private String codStatus;

    private final LocalDateTime dtCadastro;

    @Setter private LocalDateTime dtManutencao;

    // -------------------------------------------------------------------------
    // Consultas de status
    // -------------------------------------------------------------------------

    public boolean isAtivo() {
        try {
            return ChannelDomainStatus.ACTIVE.equals(ChannelDomainStatus.fromCode(codStatus));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isInativo() {
        try {
            return ChannelDomainStatus.INACTIVE.equals(ChannelDomainStatus.fromCode(codStatus));
        } catch (Exception e) {
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Transições de status
    // -------------------------------------------------------------------------

    /**
     * Ativa o tipo de atividade.
     */
    public void activate() {
        this.codStatus = ChannelDomainStatus.ACTIVE.getCode();
        this.dtManutencao = LocalDateTime.now();
    }

    /**
     * Inativa o tipo de atividade.
     */
    public void inactivate() {
        this.codStatus = ChannelDomainStatus.INACTIVE.getCode();
        this.dtManutencao = LocalDateTime.now();
    }
}
