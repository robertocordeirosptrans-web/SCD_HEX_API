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
public class MarketingDistribuitionChannel {

    private final MarketingDistribuitionChannelKey id;

    @Setter private ChannelDomainStatus codStatus;

    private final LocalDateTime dtCadastro;

    @Setter private LocalDateTime dtManutencao;

    private final User idUsuarioCadastro;

    @Setter private User idUsuarioManutencao;


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
     * Ativa a distribuição de marketing entre canais.
     *
     * @param operador usuário responsável pela operação
     */
    public void activate(User operador) {
        this.codStatus = ChannelDomainStatus.ACTIVE;
        this.idUsuarioManutencao = operador;
        this.dtManutencao = LocalDateTime.now();
    }

    /**
     * Inativa a distribuição de marketing entre canais.
     *
     * @param operador usuário responsável pela operação
     */
    public void inactivate(User operador) {
        this.codStatus = ChannelDomainStatus.INACTIVE;
        this.idUsuarioManutencao = operador;
        this.dtManutencao = LocalDateTime.now();
    }
}
