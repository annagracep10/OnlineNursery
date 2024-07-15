package com.techphantomexample.usermicroservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CombinedProductDTO {

    private List<PlantDTO> plants;
    private List<PlanterDTO> planters;
    private List<SeedDTO> seeds;

}