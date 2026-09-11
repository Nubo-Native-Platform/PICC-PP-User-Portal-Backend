package com.nnp.dashboard.scheduler;

import com.nnp.dashboard.exception.DashboardConfigException;
import com.nnp.dashboard.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public abstract class BaseScheduler {
    
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    protected void logSchedulerStart(String schedulerName) {
        if (logger.isInfoEnabled()) {
            logger.info("=== {} SCHEDULER STARTED at {} ===",
                    LogUtils.sanitizeForLog( schedulerName.toUpperCase()),
                    LogUtils.sanitizeForLog( LocalDateTime.now(ZoneId.systemDefault()).format(TIMESTAMP_FORMAT))
            );
        }
    }
    
    protected void logSchedulerSuccess(String schedulerName, Object result) {
        if (logger.isInfoEnabled()) {
            logger.info("=== {} SCHEDULER COMPLETED SUCCESSFULLY at {} ===",
                    LogUtils.sanitizeForLog(schedulerName.toUpperCase()),
                    LogUtils.sanitizeForLog( LocalDateTime.now(ZoneId.systemDefault()).format(TIMESTAMP_FORMAT)));

            if (result != null) {
                logger.info("Scheduler result: {}", LogUtils.sanitizeForLog(result));
            }
        }

    }
    
    protected void logSchedulerError(String schedulerName, Exception e) {
        if (logger.isErrorEnabled()) {
            logger.error("=== {} SCHEDULER FAILED at {} === \n Error {}" ,
                    LogUtils.sanitizeForLog(schedulerName.toUpperCase()),
                    LogUtils.sanitizeForLog( LocalDateTime.now(ZoneId.systemDefault()).format(TIMESTAMP_FORMAT)),
                    LogUtils.sanitizeForLog(e.getMessage())
            );
        }
    }
    
    protected void executeWithErrorHandling(String schedulerName, SchedulerTask task) {
        try {
            logSchedulerStart(schedulerName);
            Object result = task.execute();
            logSchedulerSuccess(schedulerName, result);
        } catch (Exception e) {
            logSchedulerError(schedulerName, e);
        }
    }
    
    @FunctionalInterface
    protected interface SchedulerTask {
        Object execute() throws DashboardConfigException;
    }
}