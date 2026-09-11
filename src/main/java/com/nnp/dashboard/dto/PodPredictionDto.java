package com.nnp.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PodPredictionDto {
    private String name;
    private List<PodPredictionMessageDto> predictions;
}
