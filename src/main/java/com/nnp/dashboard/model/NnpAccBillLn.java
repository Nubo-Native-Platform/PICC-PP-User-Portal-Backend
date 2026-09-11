package com.nnp.dashboard.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nnp_acc_bill_ln", schema = "portal")
@Getter
@Setter
public class NnpAccBillLn implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "acc_bill_ln_id", nullable = false)
	private String accBillLnId;

	@Column(name = "acc_bill_ln_amount")
	private float accBillLnAmount;

	@Column(name = "acc_pod")
	private int accPod;

	@Column(name = "acc_ai_usage")
	private int accAIUsage;

	@Column(name = "acc_bill_ln_dt")
	private LocalDateTime accBillLnDt;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
	@JoinColumn(name = "acc_bill_id")
	private NnpAccBill accBill;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
	@JoinColumn(name = "acc_comp_charge")
	private NnpAccountPlanComp accCompCharge;

}
