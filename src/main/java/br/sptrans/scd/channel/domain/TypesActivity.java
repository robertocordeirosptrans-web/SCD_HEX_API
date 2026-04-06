package br.sptrans.scd.channel.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class TypesActivity {

    private final String codAtividade;

    @Setter private String desAtividade;

    @Setter private ChannelDomainStatus codStatus;

    private final LocalDateTime dtCadastro;

    @Setter private LocalDateTime dtManutencao;

    // -------------------------------------------------------------------------
    // Consultas de status
    // -------------------------------------------------------------------------

    public boolean isAtivo() {
        return ChannelDomainStatus.ACTIVE.equals(codStatus);
    }

    public boolean isInativo() {
        return ChannelDomainStatus.INACTIVE.equals(codStatus);
    }

    // -------------------------------------------------------------------------
    // Transições de status
    // -------------------------------------------------------------------------

    /**
     * Ativa o tipo de atividade.
     */
    public void activate() {
        this.codStatus = ChannelDomainStatus.ACTIVE;
        this.dtManutencao = LocalDateTime.now();
    }

    /**
     * Inativa o tipo de atividade.
     */
    public void inactivate() {
        this.codStatus = ChannelDomainStatus.INACTIVE;
        this.dtManutencao = LocalDateTime.now();
    }
}
