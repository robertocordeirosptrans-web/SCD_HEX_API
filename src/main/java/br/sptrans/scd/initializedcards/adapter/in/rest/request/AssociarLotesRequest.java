package br.sptrans.scd.initializedcards.adapter.in.rest.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AssociarLotesRequest {

    @NotEmpty
    private List<Long> idsLotes;
}
