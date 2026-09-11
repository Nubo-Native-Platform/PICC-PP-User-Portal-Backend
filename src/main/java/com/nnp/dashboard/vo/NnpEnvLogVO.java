package com.nnp.dashboard.vo;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NnpEnvLogVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long logId;

	private LocalDate logDate;

	private Integer hour;

	private String appName;

	private Integer totErr;

	private Integer totMsg;

}
