package com.nnp.dashboard.controller;

import com.nnp.dashboard.client.RedmineIntegrationClient;
import com.nnp.dashboard.model.NnpAccSupport;
import com.nnp.dashboard.service.NNPSupportService;
import com.nnp.dashboard.vo.GetIssuesResponse;
import com.nnp.dashboard.vo.IssueTrendResponse;
import com.nnp.dashboard.vo.SupportIssueVO;
import com.nnp.dashboard.vo.redmine.Issue;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing support tickets and Redmine issue integration.
 * Handles ticket creation, status updates, paginated issue retrieval, and historical trend analysis.
 */
@RestController
@RequestMapping("support")
public class NNPSupportController {

    private final NNPSupportService nnpSupportService;

    @Autowired
    public NNPSupportController(NNPSupportService nnpSupportService, RedmineIntegrationClient redmineIntegrationClient) {
        this.nnpSupportService = nnpSupportService;
    }

    /**
     * Creates a new support issue ticket in Redmine and persists local reference.
     *
     * @param supportIssueVO ticket creation payload
     * @return created NnpAccSupport entity record
     */
    @PostMapping("create")
    public NnpAccSupport createNnpSupportIssue(@RequestBody @Valid SupportIssueVO supportIssueVO){
        return nnpSupportService.createSupportIssue(supportIssueVO);
    }

    /**
     * Updates an existing support issue ticket by Redmine issue ID.
     *
     * @param issueId Redmine issue ticket ID
     * @param supportIssueVO updated issue payload
     * @return updated Redmine Issue instance
     */
    @PutMapping("update/{issue-id}")
    public Issue updateNnpSupportIssue(@PathVariable("issue-id") String issueId, @RequestBody @Valid SupportIssueVO supportIssueVO){
        return nnpSupportService.updateSupportIssue(supportIssueVO, issueId);
    }

    /**
     * Retrieves paginated support issues filtered by account name and status.
     *
     * @param pageNumber 1-based page index (default: 1)
     * @param pageSize page size limit (default: 10)
     * @param accName account name filter (optional)
     * @param issueStatus issue status filter enum (default: ALL)
     * @return GetIssuesResponse wrapper containing paginated issue list
     */
    @GetMapping("issues")
    public GetIssuesResponse getIssues(
            @RequestParam(required = false, defaultValue = "1") int pageNumber,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false) String accName,
            @RequestParam(required = false, defaultValue = "ALL") NNPSupportService.IssueStatus issueStatus
    ){
        return nnpSupportService.getIssues(pageNumber, pageSize, accName, issueStatus);
    }

    /**
     * Computes monthly support issue trends over a specified month range for an account.
     *
     * @param accName account name
     * @param months number of historical months to analyze (default: 12)
     * @return list of IssueTrendResponse data points
     */
    @GetMapping("issues/trends")
    public List<IssueTrendResponse> getIssueTrends(
            @RequestParam String accName,
            @RequestParam(required = false, defaultValue = "12") int months
    ){
        return nnpSupportService.getIssuesTrends(accName, months);
    }
}
