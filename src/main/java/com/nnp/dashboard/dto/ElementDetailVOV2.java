package com.nnp.dashboard.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.nnp.dashboard.vo.CHElementDetailVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ElementDetailVOV2 implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String elementDtlId;
    private String elementId;

    private String elementDtlName;
    private String elementDtlType;
    private String elementDtlHome;
    private String elementDtlDesc;
    private String elementDtlURL;
    private String elementDtlFatNo;
    private String elementDtlSeq;
    private boolean isAssigned = false;

    private List<CHElementDetailVO> childElementDtls = new ArrayList<>();

}
