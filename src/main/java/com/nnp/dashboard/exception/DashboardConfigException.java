package com.nnp.dashboard.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class DashboardConfigException extends RuntimeException {


	@Serial
    private static final long serialVersionUID = -4025085269928881747L;

	private final DashboardConfigExceptionMessage dashboardConfigExceptionMessage;


    public DashboardConfigException(DashboardConfigExceptionMessage dashboardConfigExceptionMessage) {
		super(dashboardConfigExceptionMessage.getMessage());
		this.dashboardConfigExceptionMessage = dashboardConfigExceptionMessage;
	}

	public DashboardConfigException(DashboardConfigExceptionMessage dashboardConfigExceptionMessage, Exception ex) {
		super(dashboardConfigExceptionMessage.getMessage(), ex);
		this.dashboardConfigExceptionMessage = dashboardConfigExceptionMessage;
	}

	public DashboardConfigException(String code, String message, Exception ex) {
		super(message, ex);
		this.dashboardConfigExceptionMessage = new DashboardConfigExceptionMessage(code, message);
	}

	public DashboardConfigException(String code, String message) {
		super(message);
		this.dashboardConfigExceptionMessage = new DashboardConfigExceptionMessage(code, message);
	}
}
