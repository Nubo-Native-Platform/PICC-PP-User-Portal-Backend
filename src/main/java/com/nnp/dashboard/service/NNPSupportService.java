package com.nnp.dashboard.service;

import com.nnp.dashboard.config.redmine.RedmineConfigProps;
import com.nnp.dashboard.exception.DashboardConfigException;
import com.nnp.dashboard.model.NnpAccSupport;
import com.nnp.dashboard.model.NnpAccount;
import com.nnp.dashboard.repo.NnpAccSupportRepo;
import com.nnp.dashboard.repo.NnpAccountRepository;
import com.nnp.dashboard.utils.JsonUtils;
import com.nnp.dashboard.vo.AccSupportVO;
import com.nnp.dashboard.vo.GetIssuesResponse;
import com.nnp.dashboard.vo.IssueTrendResponse;
import com.nnp.dashboard.vo.SupportIssueVO;
import com.nnp.dashboard.vo.redmine.Issue;
import com.nnp.dashboard.vo.redmine.IssueCreationResponse;
import com.nnp.dashboard.vo.redmine.IssueStatusesResponse;
import com.nnp.dashboard.vo.redmine.Issue__1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NNPSupportService {

    // TODO : (Support) Put These Values In Config Props
    private static final String TRACKER_NAME = "Story";
    private static final String STATUS_RESOLVED = "Resolved";
    private static final String STATUS_CLOSED = "Closed";


    private final NnpAccSupportRepo nnpAccSupportRepo;
    private final NnpAccountRepository nnpAccountRepo;
    private final RedmineService redmineService;

    private final RedmineConfigProps redmineConfigProps;

    @Autowired
    public NNPSupportService(NnpAccSupportRepo nnpAccSupportRepo, NnpAccountRepository nnpAccountRepo, RedmineService redmineService, RedmineConfigProps redmineConfigProps) {
        this.nnpAccSupportRepo = nnpAccSupportRepo;
        this.nnpAccountRepo = nnpAccountRepo;
        this.redmineService = redmineService;
        this.redmineConfigProps = redmineConfigProps;
    }

    public enum IssueStatus {
        OPEN,
        CLOSED,
        ALL
    }

    public Issue updateSupportIssue(SupportIssueVO supportIssueVO, String issueId) {
        NnpAccSupport nnpAccSupport = nnpAccSupportRepo.findById(issueId)
                .orElseThrow(() -> new DashboardConfigException("", "No Account Support Ticket With Issue Id : " + issueId));

        Issue issue = new Issue(constructIssueForRedmine(supportIssueVO), new HashMap<>());
        String updateResponse = redmineService.updateIssueInRedmine(issue, nnpAccSupport.getRedmineIssueId());
//        log.info("Updated Successfully With Response Body : {}", updateResponse);

        if (StringUtils.hasText(supportIssueVO.subject()))
            nnpAccSupport.setTktTitle(supportIssueVO.subject());
        if (StringUtils.hasText(supportIssueVO.category()))
            nnpAccSupport.setTktCategory(supportIssueVO.category());
        if (StringUtils.hasText(supportIssueVO.priority()))
            nnpAccSupport.setTktPriority(supportIssueVO.priority());
        if (StringUtils.hasText(supportIssueVO.description()))
            nnpAccSupport.setDescription(supportIssueVO.description());
        if (Objects.nonNull(supportIssueVO.ticketResolutionDate()))
            nnpAccSupport.setTktResDt(supportIssueVO.ticketResolutionDate());
        if (StringUtils.hasText(supportIssueVO.resolution()))
            nnpAccSupport.setResolution(supportIssueVO.resolution());
        if (StringUtils.hasText(supportIssueVO.status()))
            nnpAccSupport.setStatus(supportIssueVO.status());
        if (StringUtils.hasText(supportIssueVO.categoryType()))
            nnpAccSupport.setCategoryType(supportIssueVO.categoryType());

        nnpAccSupportRepo.save(nnpAccSupport);

        return issue;
    }

    public NnpAccSupport createSupportIssue(SupportIssueVO issueCreationVO) {

        NnpAccount nnpAccount = nnpAccountRepo.findByAccName(issueCreationVO.accountName())
                .orElseThrow(() -> new DashboardConfigException(
                        "Account With Name : " + issueCreationVO.accountName() + " Does Not Exist",
                        "400"
                ));

        Issue issue = new Issue(constructIssueForRedmine(issueCreationVO), new HashMap<>());
//        log.info("Creating Redmine Issue Using Obj : {}", JsonUtils.serializeObject(issue));

        IssueCreationResponse issueCreationResponse = redmineService.createIssueInRedmine(issue);
        int redmineIssueId = issueCreationResponse.issue().id();
//        log.info("Successfully Created Issue In Redmine With Id : {}", redmineIssueId);

        NnpAccSupport nnpAccSupport = new NnpAccSupport(
                "ACC_SP_TKT-" + UUID.randomUUID(),
                nnpAccount,
                issueCreationVO.ticketDate(),
                issueCreationVO.ticketResolutionDate(),
                issueCreationVO.category(),
                issueCreationVO.priority(),
                issueCreationVO.ticketDate(),
                issueCreationVO.subject(),
                issueCreationVO.description(),
                issueCreationVO.resolution(),
                String.valueOf(redmineIssueId),
                issueCreationVO.status(),
                issueCreationVO.categoryType()
        );
        nnpAccSupportRepo.save(nnpAccSupport);

        nnpAccSupport.setNnpAccount(null);
        return nnpAccSupport;
    }

    private Issue__1 constructIssueForRedmine(SupportIssueVO issueCreationVO) {
//        log.info("Category Type = {}", issueCreationVO.categoryType());
        Issue__1 issue__1 = new Issue__1();
        if (StringUtils.hasText(issueCreationVO.categoryType())){
            int parentIssueId = redmineService.getParentIssueBySubject(issueCreationVO.category()).id();
            issue__1.setParentIssueId(parentIssueId);
        }

        issue__1.setProjectId(Integer.parseInt(redmineConfigProps.getBaseProjectId()));

        int trackerId = redmineService.getTrackerByName(TRACKER_NAME).id();
        issue__1.setTrackerId(trackerId);

        int statusId = redmineService.getIssueStatusByName(issueCreationVO.status()).id();
        issue__1.setStatusId(statusId);

        issue__1.setAuthorId("");
        issue__1.setAssignedToId("");

        if (StringUtils.hasText(issueCreationVO.subject()))
            issue__1.setSubject(issueCreationVO.subject());

        if (StringUtils.hasText(issueCreationVO.priority())){
            int issuePriorityId = redmineService.getIssuePriorityByName(issueCreationVO.priority()).id();
            issue__1.setPriorityId(issuePriorityId);
        }

        if (StringUtils.hasText(issueCreationVO.description()))
            issue__1.setDescription(issueCreationVO.description());

        if (StringUtils.hasText(issueCreationVO.categoryType())){
            int categoryTypeId = redmineService.getIssueCategoryByName(issueCreationVO.categoryType()).id();
            issue__1.setCategoryId(String.valueOf(categoryTypeId));
        }

        return issue__1;

    }

    public GetIssuesResponse getIssues(int pageNumber, int pageSize, String accName, IssueStatus issueStatus) {

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("tktDt").descending());

        Page<NnpAccSupport> issuePage = switch (issueStatus){
            case OPEN -> nnpAccSupportRepo.findByAccIdAndIssueStatus(getAllIssueStatusesNamesExceptClosed() , accName, pageRequest);
            case CLOSED -> nnpAccSupportRepo.findByAccIdAndIssueStatus(List.of(STATUS_CLOSED) , accName, pageRequest);
            case ALL -> nnpAccSupportRepo.findByAccIdAndIssueStatus(getAllIssueStatusesNames(), accName, pageRequest);
            default -> throw new DashboardConfigException("500", issueStatus + ": Issue Status Is Not Handled In Code");
        };

        List<AccSupportVO> issues = issuePage.map(this::createAccSupportVOFromNnpAccSupport).toList();
        return new GetIssuesResponse(issuePage.getTotalPages(), issues.size(), issues);
    }

    private List<String> getAllIssueStatusesNamesExceptClosed() {
        return redmineService.getIssueStatuses().stream()
                .map(IssueStatusesResponse.IssueStatus::name)
                .filter(issueName -> !issueName.equals(STATUS_CLOSED))
                .toList();
    }

    private List<String> getAllIssueStatusesNames() {
        return redmineService.getIssueStatuses().stream()
                .map(IssueStatusesResponse.IssueStatus::name)
                .toList();
    }

    private AccSupportVO createAccSupportVOFromNnpAccSupport(NnpAccSupport nnpAccSupport) {

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String issueStartDate = Objects.isNull(nnpAccSupport.getTktStart()) ?
                "" : nnpAccSupport.getTktStart().format(dateFormat);

        String issueEndDate = Objects.isNull(nnpAccSupport.getTktResDt()) ?
                "" : nnpAccSupport.getTktResDt().format(dateFormat);

        Long resolvedHrs = null;
        if (StringUtils.hasText(issueStartDate) && StringUtils.hasText(issueEndDate))
            resolvedHrs = Duration.between(nnpAccSupport.getTktStart(), nnpAccSupport.getTktResDt()).toHours();

        return new AccSupportVO(
                nnpAccSupport.getSupTktId(),
                issueStartDate,
                issueEndDate,
                resolvedHrs,
                nnpAccSupport.getTktCategory(),
                nnpAccSupport.getTktTitle(),
                nnpAccSupport.getTktPriority(),
                nnpAccSupport.getStatus(),
                nnpAccSupport.getCategoryType(),
                nnpAccSupport.getDescription(),
                nnpAccSupport.getNnpAccount().getAccName(),
                nnpAccSupport.getResolution(),
                nnpAccSupport.getRedmineIssueId()
        );
    }

    public List<IssueTrendResponse> getIssuesTrends(String accName, int months) {

        Map<String, List<NnpAccSupport>> categoryIssueMap =
                nnpAccSupportRepo.findByTktStartAfterAndNnpAccount_AccName(ZonedDateTime.now().minusMonths(months), accName)
                        .stream()
                        .filter(s -> Objects.nonNull(s.getTktCategory()))
                        .collect(Collectors.groupingBy(NnpAccSupport::getTktCategory));

        List<IssueTrendResponse> issueTrendResponses = new ArrayList<>();

        for (Map.Entry<String, List<NnpAccSupport>> supportEntry : categoryIssueMap.entrySet()) {
            String issueCategory = supportEntry.getKey();
            List<NnpAccSupport> issuesByCategory = supportEntry.getValue();

            Map<String, IssueTrendResponse.MonthlyIssueData> monthlyIssueDataMap = issuesByCategory.stream()
                    .collect(Collectors.groupingBy(
                            i -> Objects.isNull(i.getTktStart()) ? "No Date" : i.getTktStart().getMonth().name(),
                            Collectors.collectingAndThen(Collectors.toList(), list -> {
                                int reportedIssues = list.size();
                                List<NnpAccSupport> resolvedIssues = list.stream()
                                        .filter(this::isIssueCompleted)
                                        .toList();
                                NnpAccSupport nnpAccSupport = list.stream().findFirst()
                                        .orElseThrow(RuntimeException::new);
                                return new IssueTrendResponse.MonthlyIssueData(
                                        nnpAccSupport.getTktStart().getMonth().name(),
                                        nnpAccSupport.getTktStart().getYear(),
                                        resolvedIssues.size(),
                                        reportedIssues,
                                        avgResolvedIssueTimeHours(resolvedIssues)
                                );
                            })
                    ));

            IssueTrendResponse issueTrendResponse = new IssueTrendResponse(
                    issueCategory,
                    issuesByCategory.size(),
                    monthlyIssueDataMap.values().stream().toList()
            );
            issueTrendResponses.add(issueTrendResponse);
        }
        return issueTrendResponses;
    }

    private Double avgResolvedIssueTimeHours(List<NnpAccSupport> resolvedIssues){
        if (CollectionUtils.isEmpty(resolvedIssues))
            return null;
        return resolvedIssues.stream()
                .mapToLong(this::getIssueResolveTimeInHours)
                .average()
                .orElseThrow(() -> new DashboardConfigException("500", "Some Problem Calculating Avg For Resolved/Closed Issues"));
    }

    private long getIssueResolveTimeInHours(NnpAccSupport nnpAccSupport) {
        if (Objects.isNull(nnpAccSupport.getTktStart()))
            throw new DashboardConfigException("500","No TktStart (Start Date) For Ticket With Id : " + nnpAccSupport.getSupTktId());

        if (Objects.isNull(nnpAccSupport.getTktResDt()))
            throw new DashboardConfigException("500","No TktResDt (Resolve/Closed Date) For Ticket With Id : " + nnpAccSupport.getSupTktId());

        return Duration.between(nnpAccSupport.getTktStart(), nnpAccSupport.getTktResDt()).toHours();
    }

    private boolean isIssueCompleted(NnpAccSupport nnpAccSupport){
        return Objects.nonNull(nnpAccSupport.getStatus()) &&
                (nnpAccSupport.getStatus().equals(STATUS_CLOSED) || nnpAccSupport.getStatus().equals(STATUS_RESOLVED));
    }

}
