package br.sptrans.scd.channel.application.port.in;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.channel.domain.TypesActivity;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;

public interface TypesActivityUseCase {

        TypesActivity createTypesActivity(CreateTypesActivityCommand command);

        TypesActivity updateTypesActivity(String codAtividade, UpdateTypesActivityCommand command);

        TypesActivity findByTypesActivity(String codAtividade);

        Page<TypesActivity> findAllTypesActivities(ChannelDomainStatus codStatus, Pageable pageable);

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
