package com.nnp.dashboard.vo;

import java.io.Serializable;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NnpAccSupportVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String supTktId;

	private NnpAccountVO nnpAccount;

	private ZonedDateTime tktDt;

	private ZonedDateTime tktResDt;

	private String tktCategory;

	private String tktPriority;

	private ZonedDateTime tktStart;

	private String tktTitle;

}
