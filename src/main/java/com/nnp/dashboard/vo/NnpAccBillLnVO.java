package com.nnp.dashboard.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NnpAccBillLnVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String accBillLnId;

	private float accBillLnAmount;

	private int accCall;

	private int accVol;

	private LocalDateTime accBillLnDt;

	private NnpAccBillVO accBill;
		
}
