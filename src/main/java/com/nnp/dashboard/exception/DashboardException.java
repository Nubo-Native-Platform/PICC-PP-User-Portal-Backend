package com.nnp.dashboard.exception;

public class DashboardException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -4025085269928881747L;

	public DashboardException(String s) {
		super(s);
	}

	public DashboardException(String s, Exception ex) {
		super(s, ex);
	}

}
