package com.nnp.dashboard.client;

import com.nnp.dashboard.vo.redmine.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

@HttpExchange(value = "/api")
public interface RedmineIntegrationClient {

    @PostExchange(value = "/createIssue")
    IssueCreationResponse createIssue(@RequestBody Issue issue, @RequestParam String apiKey);

    @PutExchange(value = "/updateIssue")
    String updateIssue(@RequestBody Issue issue, @RequestParam String issueId, @RequestParam String apiKey);

    @GetExchange(value = "/issue-categories")
    IssueCategoriesResponse getIssueCategories(@RequestParam String projectId, @RequestParam String apiKey);

    @GetExchange(value = "/trackers")
    TrackersResponse getTrackers(@RequestParam String apiKey);

    @GetExchange(value = "/issue-statuses")
    IssueStatusesResponse getIssueStatuses(@RequestParam String apiKey);

    @GetExchange(value = "/issue-priorities")
    IssuePrioritiesResponse getIssuePriorities(@RequestParam String apiKey);

    @GetExchange(value = "/getIssues")
    IssuesResponse getIssue(@RequestParam String projectId, @RequestParam String authorId, @RequestParam String trackerId ,@RequestParam String apiKey);

}
