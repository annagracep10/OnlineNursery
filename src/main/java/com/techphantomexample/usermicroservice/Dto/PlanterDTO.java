package com.techphantomexample.usermicroservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanterDTO extends BaseProductDTO {

    private String material;
    private String dimensions;
    private String color;

}