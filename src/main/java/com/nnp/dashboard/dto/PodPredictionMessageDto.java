package com.nnp.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PodPredictionMessageDto {
    private Long timestamp;
    private String namespace;
    private String pod;
    private Double cpu;
    private Double cpuProbability;
    private Double memory;
    private Double memoryProbability;
    private Double storage;
    private Double storageProbability;
}