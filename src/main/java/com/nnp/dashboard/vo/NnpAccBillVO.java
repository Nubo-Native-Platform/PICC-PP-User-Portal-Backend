package com.nnp.dashboard.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NnpAccBillVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String accBillId;

	private Float accBillAdjAmount;

	private Float accBillAmount;

	private String accBillComment;

	private String accBillContact;

	private LocalDateTime accBillDt;

	private Float accBillOpenBal;

	private LocalDateTime accBillPayDt;

	private String accBillStatus;

	private Float accBillTaxPct;

	private Float accBillPayAmount;

	private NnpAccountVO nnpAccount;

}
