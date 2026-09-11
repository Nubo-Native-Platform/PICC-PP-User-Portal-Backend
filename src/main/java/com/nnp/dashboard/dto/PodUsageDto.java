package com.nnp.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PodUsageDto {
    private String namespace;
    private String pod;
    private Double avgCpu;
    private Double avgMemory;
    private  Double avgStorage;
}
