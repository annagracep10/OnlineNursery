package com.techphantomexample.usermicroservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeedDTO extends BaseProductDTO {

    private String seedType;
    private int germinationTime;
    private String season;


}
