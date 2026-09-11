package com.nnp.dashboard.exception;

import com.nnp.dashboard.utils.LogUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class DashboardConfigExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleException(RuntimeException exception) {
        log.error("exception --> {}", LogUtils.sanitizeForLog(exception.getMessage()));
        DashboardConfigExceptionMessage dashboardConfigExceptionMessage = new DashboardConfigExceptionMessage();
        if (exception instanceof ResponseStatusException) {
            dashboardConfigExceptionMessage.setCode(String.valueOf(((ResponseStatusException) exception).getStatusCode()));
            dashboardConfigExceptionMessage.setMessage(exception.getMessage());
        } else if (exception instanceof DashboardConfigException) {
            String code = ((DashboardConfigException) exception).getDashboardConfigExceptionMessage().getCode();
            String message = ((DashboardConfigException) exception).getDashboardConfigExceptionMessage().getMessage();
            log.error("Error details in DashboardConfigExceptionHandler is code --> {} message --> {}", LogUtils.sanitizeForLog(code), LogUtils.sanitizeForLog(message));

            dashboardConfigExceptionMessage.setCode(code);
            dashboardConfigExceptionMessage.setMessage(message);
        } else {
            dashboardConfigExceptionMessage.setCode("500");
            dashboardConfigExceptionMessage.setMessage(exception.getMessage());
        }
        return new ResponseEntity<>(dashboardConfigExceptionMessage, HttpStatusCode.valueOf(Integer.parseInt(dashboardConfigExceptionMessage.getCode())));
    }

}
