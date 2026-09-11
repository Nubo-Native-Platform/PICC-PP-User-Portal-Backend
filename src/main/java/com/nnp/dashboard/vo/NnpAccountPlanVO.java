package com.nnp.dashboard.vo;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NnpAccountPlanVO {

    private String accPlanId;
    private NnpAccountVO nnpAccount;
    private NnpPlanVO nnpPlan; 
    private String baseDcnt;
    private String accPlanStatus;
    private LocalDateTime planStDt;
    private LocalDateTime planEnDt;
    private Boolean active;
    private String createdBy;
    private String modifiedBy;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;

   
}
