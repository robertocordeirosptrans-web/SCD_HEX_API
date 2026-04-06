package br.sptrans.scd.channel.application.port.in;

import java.util.List;

import br.sptrans.scd.channel.domain.TypesActivity;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;

public interface TypesActivityUseCase {

        TypesActivity createTypesActivity(CreateTypesActivityCommand command);

        TypesActivity updateTypesActivity(String codAtividade, UpdateTypesActivityCommand command);

        TypesActivity findByTypesActivity(String codAtividade);

        List<TypesActivity> findAllTypesActivities(ChannelDomainStatus codStatus);

        void activateTypesActivity(String codAtividade);

        void inactivateTypesActivity(String codAtividade);

        void deleteTypesActivity(String codAtividade);

        // ── Commands ──────────────────────────────────────────────────────────────

        record CreateTypesActivityCommand(
                        String codAtividade,
                        String desAtividade) {
        }

        record UpdateTypesActivityCommand(
                        String desAtividade) {
        }
}
