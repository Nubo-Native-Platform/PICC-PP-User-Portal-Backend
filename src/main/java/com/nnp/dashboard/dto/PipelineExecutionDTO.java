package com.nnp.dashboard.dto;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PipelineExecutionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String envId;
    private String plDate;
    private Integer hour;
    private Long avgPlTime;
    private Integer plSuccess;
    private Integer plFail;
    private Double successPercentage;
    private Double failurePercentage;

}