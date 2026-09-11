package com.nnp.dashboard.service;

import com.nnp.dashboard.client.RedmineIntegrationClient;
import com.nnp.dashboard.config.redmine.RedmineConfigProps;
import com.nnp.dashboard.exception.DashboardConfigException;
import com.nnp.dashboard.vo.redmine.*;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service for communicating with Redmine REST API for issue management.
 * Pre-loads Redmine metadata (trackers, categories, priorities, statuses, parent issues) during startup.
 */
@Service
@Slf4j
public class RedmineService {

    // TODO : (Support) Put These Values In Config Props
    private static final String PARENT_ISSUE_TRACKER_NAME = "Epic";

    private final RedmineIntegrationClient redmineIntegrationClient;
    private final RedmineConfigProps redmineConfigProps;


    @Getter private List<TrackersResponse.Tracker> trackers = new ArrayList<>();
    @Getter private List<IssueCategoriesResponse.IssueCategory> issueCategories = new ArrayList<>();
    @Getter private List<IssuePrioritiesResponse.IssuePriority> issuePriorities = new ArrayList<>();
    @Getter private List<IssueStatusesResponse.IssueStatus> issueStatuses = new ArrayList<>();
    @Getter private List<IssuesResponse.Issue> parentIssues = new ArrayList<>();

    public RedmineService(RedmineIntegrationClient redmineIntegrationClient, RedmineConfigProps redmineConfigProps) {
        this.redmineIntegrationClient = redmineIntegrationClient;
        this.redmineConfigProps = redmineConfigProps;
    }

    @PostConstruct
    private void initialize() {
        try {
            initializeTrackers();
            initializeIssueCategories();
            initializeIssuePriorities();
            initializeIssueStatuses();
            initializeParentIssues();
        } catch (Exception e) {
            log.warn("Redmine integration metadata initialization skipped or deferred: {}", e.getMessage());
        }
    }

    private String sanitizeString(String txt){
        return txt.trim().toLowerCase().replaceAll(" ", "");
    }

    public TrackersResponse.Tracker getTrackerByName(String trackerName){
        return trackers.stream()
                .filter(t -> sanitizeString(trackerName).equals(sanitizeString(t.name())))
                .findFirst()
                .orElseThrow(() -> new DashboardConfigException("" , trackerName + "Does Not Exist In Redmine"));
    }

    public IssuesResponse.Issue getParentIssueBySubject(String parentIssueSubject){
        return parentIssues.stream()
                .filter(t -> sanitizeString(parentIssueSubject).equals(sanitizeString(t.subject())))
                .findFirst()
                .orElseThrow(() -> new DashboardConfigException("" , parentIssueSubject + "Does Not Exist In Redmine"));
    }

    public IssueCategoriesResponse.IssueCategory getIssueCategoryByName(String issueCategoryName){
        return issueCategories.stream()
                .filter(ic -> sanitizeString(issueCategoryName).equals(sanitizeString(ic.name())))
                .findFirst()
                .orElseThrow(() -> new DashboardConfigException("" , issueCategoryName + "Does Not Exist In Redmine"));
    }

    public IssuePrioritiesResponse.IssuePriority getIssuePriorityByName(String issuePriorityName){
        return issuePriorities.stream()
                .filter(ip -> sanitizeString(issuePriorityName).equals(sanitizeString(ip.name())))
                .findFirst()
                .orElseThrow(() -> new DashboardConfigException("" , issuePriorityName + "Does Not Exist In Redmine"));
    }

    public IssueStatusesResponse.IssueStatus getIssueStatusByName(String issueStatusName){
        return issueStatuses.stream()
                .filter(t -> sanitizeString(issueStatusName).equals(sanitizeString(t.name())))
                .findFirst()
                .orElseThrow(() -> new DashboardConfigException("" , issueStatusName + "Does Not Exist In Redmine"));
    }

    private void initializeParentIssues() {
//        log.info("Starting To Initialize Redmine Parent Issues");

        TrackersResponse.Tracker parentIssueTracker = getTrackerByName(PARENT_ISSUE_TRACKER_NAME);
        IssuesResponse issuesResponse =
                redmineIntegrationClient.getIssue(
                        redmineConfigProps.getBaseProjectId(),
                        "",
                        String.valueOf(parentIssueTracker.id()),
                        redmineConfigProps.getApiKey()
                );

        if (Objects.isNull(issuesResponse))
            throw new DashboardConfigException("Cound Not Get Parent Issue Response From Redmine", "");
        this.parentIssues = issuesResponse.issues();

//        log.info("Initialize Redmine Parent Issue");
    }

    private void initializeIssueStatuses() {
//        log.info("Starting To Initialize Redmine Issue Statuses");

        IssueStatusesResponse issueStatusesResponse =
                redmineIntegrationClient.getIssueStatuses(redmineConfigProps.getApiKey());
        if (Objects.isNull(issueStatusesResponse))
            throw new DashboardConfigException("Cound Not Get Issue Status From Redmine", "");
        this.issueStatuses = issueStatusesResponse.issue_statuses();

//        log.info("Initialize Redmine Issue Statuses");
    }

    private void initializeIssuePriorities() {
//        log.info("Starting To Initialize Redmine Issue Priorities");

        IssuePrioritiesResponse issuePrioritiesResponse =
                redmineIntegrationClient.getIssuePriorities(redmineConfigProps.getApiKey());
        if (Objects.isNull(issuePrioritiesResponse))
            throw new DashboardConfigException("Cound Not Get Issue Priorities From Redmine", "");
        this.issuePriorities = issuePrioritiesResponse.issue_priorities();

//        log.info("Initialize Redmine Issue Priorities");
    }

    private void initializeIssueCategories() {
//        log.info("Starting To Initialize Issue Categories");

        IssueCategoriesResponse issueCategoriesResponse =
                redmineIntegrationClient.getIssueCategories(redmineConfigProps.getBaseProjectId(), redmineConfigProps.getApiKey());
        if (Objects.isNull(issueCategoriesResponse))
            throw new DashboardConfigException("Cound Not Get Issue Categories From Redmine", "");
        this.issueCategories = issueCategoriesResponse.issue_categories();

//        log.info("Initialize Redmine Issue Categories");

    }

    private void initializeTrackers() {
//        log.info("Starting To Initialize Redmine Trackers");

        TrackersResponse trackersResponse = redmineIntegrationClient.getTrackers(redmineConfigProps.getApiKey());
        if (Objects.isNull(trackersResponse))
            throw new DashboardConfigException("Cound Not Get Trackers From Redmine", "");
        this.trackers = trackersResponse.trackers();

//        log.info("Initialize Redmine Trackers");
    }

    public IssueCreationResponse createIssueInRedmine(Issue issue) {
        return redmineIntegrationClient.createIssue(issue, redmineConfigProps.getApiKey());
    }

    public String updateIssueInRedmine(Issue issue, String issueId) {
        return redmineIntegrationClient.updateIssue(issue, issueId, redmineConfigProps.getApiKey());
    }
}
