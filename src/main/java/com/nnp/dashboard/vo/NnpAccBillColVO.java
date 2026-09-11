package com.nnp.dashboard.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NnpAccBillColVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String accBillColId;

	private float payAmount;

	private LocalDateTime payDt;

	private String payMode;

	private String payRef;

	private NnpAccBillVO accBill;

	
}
