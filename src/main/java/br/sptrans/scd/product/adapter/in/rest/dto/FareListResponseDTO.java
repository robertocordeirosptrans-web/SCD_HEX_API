package br.sptrans.scd.product.adapter.in.rest.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareListResponseDTO {
    private List<FareResponseDTO> fares;
}
