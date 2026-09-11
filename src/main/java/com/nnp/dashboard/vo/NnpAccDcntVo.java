package com.nnp.dashboard.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NnpAccDcntVo implements Serializable {

	private static final long serialVersionUID = 1L;

    private String accDcntId;

    private String accDcntDescription;

    private LocalDateTime accDcntEndTs;

    private String accDcntPercent;

    private LocalDateTime accDcntStartTs;

    private String accId;

    private String apiId;

    private Boolean active;

    private String createdBy;

    private String modifiedBy;

    private OffsetDateTime createdOn;

    private OffsetDateTime modifiedOn;
    
    
    @JsonIgnore
    private NnpAccountVO nnpAccount;  

   
}
