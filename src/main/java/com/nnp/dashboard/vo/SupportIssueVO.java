package com.nnp.dashboard.vo;

import java.time.ZonedDateTime;

// TODO : (Support) Implement A Custom Validator or Use Validation Groups For Updation And Creation Case
public record SupportIssueVO(

        String subject,

        String category,

        String priority,

        String description,

        String categoryType,

        String accountName,

        ZonedDateTime ticketDate,

        ZonedDateTime ticketResolutionDate,

        String resolution,

        String status
) {
}
