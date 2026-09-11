package com.nnp.dashboard.service;

import com.nnp.dashboard.exception.DashboardConfigException;
import com.nnp.dashboard.model.*;
import com.nnp.dashboard.repo.*;
import com.nnp.dashboard.utils.ContentValues;
import com.nnp.dashboard.utils.LogUtils;
import com.nnp.dashboard.vo.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountsService {

        private static final String NNP_BASE_CURRENCY = "usd";

        private final NnpAccountRepository nnpAccountRepository;
        private final NnpAccCommRepo nnpAccCommRepo;
        private final NnpAccBillRepo nnpAccBillRepo;

        private final EnvProxyConfigRepo envProxyConfigRepo;
        private final EnvironmentRepoV3 environmentRepov3;
        private final NnpUserRepo nnpUserRepo;

        @Autowired
        public AccountsService(NnpAccountRepository nnpAccountRepository, NnpAccCommRepo nnpAccCommRepo,
                        NnpAccBillRepo nnpAccBillRepo, EnvProxyConfigRepo envProxyConfigRepo,
                        EnvBbCompRepo envBbCompRepo, NnpEnvFeaElemDtlSpecRepo nnpEnvFeaElemDtlSpecRepo,
                        EnvironmentRepoV3 environmentRepo, NnpUserRepo nnpUserRepo) {
                this.nnpAccountRepository = nnpAccountRepository;
                this.nnpAccCommRepo = nnpAccCommRepo;
                this.nnpAccBillRepo = nnpAccBillRepo;
                this.envProxyConfigRepo = envProxyConfigRepo;
                this.environmentRepov3 = environmentRepo;
                this.nnpUserRepo = nnpUserRepo;
        }

        private BigDecimal calculateMonthlyBillUsingPlanComponents(
                List<NnpPlanComp> planComponents,
                String planDiscount,
                String planBasePrice
        ) {
                double dailyCharge = planComponents.stream()
                                .mapToDouble(pc -> Double.parseDouble(pc.getBasePrice()))
                                .sum();

                BigDecimal monthlyComponentCharge = BigDecimal.valueOf(dailyCharge).multiply(BigDecimal.valueOf(30));
                BigDecimal monthlyCharge = monthlyComponentCharge.add(BigDecimal.valueOf(Double.parseDouble(planBasePrice)));
                return monthlyCharge.multiply(
                        BigDecimal.valueOf(100 - Double.parseDouble(planDiscount))
                                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_DOWN)
                );
        }

        public CurrentAccountBill getCurrentBillForAccount(String accountName, String billingId) {

                NnpAccount nnpAccount = nnpAccountRepository.findByAccName(accountName)
                                .orElseThrow(() -> new DashboardConfigException("400", ContentValues.THIS_ACCOUNT_DOES_NOT_EXIST));

                NnpPlan activePlan = getNnpActiveAccountPlan(nnpAccount).getNnpPlan();

                NnpAccBill accountBill = null;
                if (StringUtils.hasText(billingId)) {
                        accountBill = nnpAccBillRepo.findById(billingId)
                                        .orElseThrow(() -> new DashboardConfigException("400",
                                                        "Account Bill With This Id Does Not Exist"));

                        if (Objects.nonNull(accountBill.getAccBillStatus())
                                        && accountBill.getAccBillStatus().equalsIgnoreCase("paid"))
                                throw new DashboardConfigException("400", "This Bill Has Already Been Paid");

                } else
                        accountBill = getLatestNotPaidAccountBill(nnpAccount);

                String formattedBillDate = Objects.isNull(accountBill.getAccBillDt()) ? ""
                                : accountBill.getAccBillDt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                return new CurrentAccountBill(
                                activePlan.getHostPlName(),
                                calculateAmountToBePaidForABill(accountBill),
                                accountBill.getAccBillId(),
                                formattedBillDate,
                                NNP_BASE_CURRENCY);

        }

        private Double calculateAmountToBePaidForABill(NnpAccBill accBill) {
                Float accBillAdjAmount = Optional.ofNullable(accBill.getAccBillAdjAmount()).orElse(0.0f);
                Float accBillOpenBal = Optional.ofNullable(accBill.getAccBillOpenBal()).orElse(0.0f);
                float accBillTaxPct = Optional.ofNullable(accBill.getAccBillTaxPct()).orElse(0.0f);

                float netBill = accBill.getAccBillAmount() - accBillAdjAmount - accBillOpenBal;
                return (double) netBill + netBill * accBillTaxPct / 100;
        }

        public SubscriptionPlanVO getSubscriptionPlanForAccount(String accountName) {

                NnpAccount nnpAccount = nnpAccountRepository.findByAccName(accountName)
                                .orElseThrow(() -> new DashboardConfigException("400", ContentValues.THIS_ACCOUNT_DOES_NOT_EXIST));

                if (CollectionUtils.isEmpty(nnpAccount.getAccountPlans()))
                        return null;

                NnpAccountPlan activeAccountPlan = getNnpActiveAccountPlan(nnpAccount);

                List<NnpPlanComp> activePlanComps = getNnpActivePlanComps(activeAccountPlan);

                String currency = nnpAccount.getNnpCountry().getCurrency();

                var groupedComponents = activePlanComps.stream()
                                .collect(Collectors.groupingBy(
                                                pc -> pc.getPlanCompGroup().getCompGroupTitle(),
                                                Collectors.mapping(apc -> constructPlanComponent(apc, currency), Collectors.toList())));

                String planBasePrice = activeAccountPlan.getNnpPlan().getHostPlBasePr();
                BigDecimal monthlyCharge = calculateMonthlyBillUsingPlanComponents(
                        activePlanComps,
                        activeAccountPlan.getBaseDcnt(),
                        planBasePrice
                );

                return new SubscriptionPlanVO(
                                activeAccountPlan.getNnpPlan().getPlanId(),
                                activeAccountPlan.getNnpPlan().getHostPlName(),
                                monthlyCharge.setScale(3, RoundingMode.HALF_DOWN).toString(),
                                currency,
                                activeAccountPlan.getBaseDcnt(),
                                planBasePrice,
                                groupedComponents
                );
        }

        private SubscriptionPlanVO.PlanComponent constructPlanComponent(NnpPlanComp nnpPlanComp, String currency) {
                EnvBbComp envBbComp = nnpPlanComp.getEnvBbComp();
                return new SubscriptionPlanVO.PlanComponent(
                                envBbComp.getEnvCompId(),
                                envBbComp.getEnvCompName(),
                                new PriceVO(currency, Double.parseDouble(nnpPlanComp.getBasePrice())),
                                new ArrayList<>()
                );
        }

        private List<NnpPlanComp> getNnpActivePlanComps(NnpAccountPlan activeAccountPlan) {
                return activeAccountPlan.getAccountPlanComps().stream()
                                .filter(NnpAccountPlanComp::getActive)
                                .map(NnpAccountPlanComp::getNnpPlanComp)
                                .toList();
        }

        private NnpAccountPlan getNnpActiveAccountPlan(NnpAccount nnpAccount) {
                return nnpAccount.getAccountPlans().stream()
                                .filter(NnpAccountPlan::getActive)
                                .findFirst()
                                .orElseThrow(() -> new DashboardConfigException("400",
                                                "No Active Plans For This Account"));
        }

        private Float getTotalPaidAmount(NnpAccBill accBill) {
                double totalPaidAmount = accBill.getAccBillCols().stream()
                                .mapToDouble(NnpAccBillCol::getPayAmount)
                                .sum();

                return (float) totalPaidAmount;
        }

        private NnpAccBill getLatestNotPaidAccountBill(NnpAccount nnpAccount) {
                List<NnpAccBill> sortedBillsForAccount = nnpAccount.getAccBills().stream()
                                .filter(nab -> Objects.isNull(nab.getAccBillStatus())
                                                || !nab.getAccBillStatus().equalsIgnoreCase("Paid"))
                                .sorted(Comparator.comparing(NnpAccBill::getAccBillDt))
                                .toList();

                return sortedBillsForAccount.stream().findFirst()
                                .orElseGet(() -> {
                                        log.warn("Billing For Account Id : {} Is Empty", LogUtils.sanitizeForLog(nnpAccount.getAccId()));
                                        return null;
                                });
        }

        private BillingDetailsVO.CurrentBillingDetails getCurrentBillingDetails(NnpAccount nnpAccount) {

                NnpAccBill nnpAccBill = getLatestNotPaidAccountBill(nnpAccount);

                if (Objects.isNull(nnpAccBill))
                        return null;

                return new BillingDetailsVO.CurrentBillingDetails(
                                nnpAccBill.getAccBillId(),
                                nnpAccBill.getAccBillAmount(),
                                nnpAccBill.getAccBillComment(),
                                nnpAccBill.getAccBillComment(),
                                nnpAccBill.getAccBillDt().toString(),
                                nnpAccBill.getAccBillOpenBal(),
                                nnpAccBill.getAccBillStatus(),
                                nnpAccBill.getAccBillTaxPct(),
                                nnpAccBill.getAccBillAdjAmount());
        }

        private List<BillingDetailsVO.DailyBillDetails> mapAccBillLnToDailyBillingDetails(NnpAccount nnpAccount) {
                NnpAccBill nnpAccBill = getLatestNotPaidAccountBill(nnpAccount);

                if (Objects.isNull(nnpAccBill))
                        return null;

                return nnpAccBill.getAccBillLns().stream()
                                .map(acl -> new BillingDetailsVO.DailyBillDetails(
                                                acl.getAccBillLnId(),
                                                acl.getAccBillLnAmount(),
                                                acl.getAccPod(),
                                                acl.getAccAIUsage()))
                                .toList();

        }

        public BillingDetailsVO getBillingDetailsForAccount(String accountName) {

                NnpAccount nnpAccount = nnpAccountRepository.findByAccName(accountName)
                                .orElseThrow(() -> new DashboardConfigException("400", ContentValues.THIS_ACCOUNT_DOES_NOT_EXIST));

                if (CollectionUtils.isEmpty(nnpAccount.getAccountPlans()))
                        return null;

                NnpAccountPlan activeAccountPlan = getNnpActiveAccountPlan(nnpAccount);

                List<NnpPlanComp> activePlanComps = getNnpActivePlanComps(activeAccountPlan);

                var billingData = nnpAccount.getAccBills().stream()
                                .map(ab -> new BillingDetailsVO.BillingData(
                                                ab.getAccBillId(),
                                                ab.getAccBillDt().getMonth().name(),
                                                ab.getAccBillDt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                                0,
                                                ab.getAccBillAmount(),
                                                getTotalPaidAmount(ab),
                                                ab.getInvoiceUrl()))
                                .toList();

                String currency = nnpAccount.getNnpCountry().getCurrency();
                String planBasePrice = activeAccountPlan.getNnpPlan().getHostPlBasePr();
                BigDecimal monthlyCharge = calculateMonthlyBillUsingPlanComponents(
                        activePlanComps,
                        activeAccountPlan.getBaseDcnt(),
                        planBasePrice
                );

                return new BillingDetailsVO(
                                activeAccountPlan.getNnpPlan().getHostPlName(),
                                String.format("%s %s / Month", monthlyCharge.setScale(3, RoundingMode.HALF_DOWN), currency),
                                getCurrentBillingDetails(nnpAccount),
                                mapAccBillLnToDailyBillingDetails(nnpAccount),
                                billingData
                );
        }

        public List<AccountCommunicationVO> getAccountCommunications(String accountName) {

                NnpAccount nnpAccount = nnpAccountRepository.findByAccName(accountName)
                                .orElseThrow(() -> new DashboardConfigException("400",ContentValues.THIS_ACCOUNT_DOES_NOT_EXIST));

                return nnpAccCommRepo.findByNnpAccountAccId(nnpAccount.getAccId()).stream()
                                .map(ac -> new AccountCommunicationVO(
                                                ac.getCommDate().format(
                                                                DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a")),
                                                ac.getCommType(), ac.getCommCategory(),
                                                ac.getCommComments(),
                                                ac.getCommTitle(), ac.getCommDescription(), ac.getCommFileLinks()))
                                .toList();
        }

        public List<EnvProxyConfigVO> getAllProxyConfigs() {
                return envProxyConfigRepo.findAll().stream().map(EnvProxyConfigVO::toVo).toList();
        }

        public EnvProxyConfigVO saveProxyConfig(EnvProxyConfigVO proxyConfigVO) {
                try {
                        if (proxyConfigVO.getEnvId() == null || proxyConfigVO.getEnvId().isEmpty()) {
                                throw new IllegalArgumentException(
                                                "Environment (envId) is required and cannot be null or empty");
                        }
                        if (!environmentRepov3.existsByEnvCode(proxyConfigVO.getEnvId())) {
                                throw new IllegalArgumentException(
                                                "Environment with id " + proxyConfigVO.getEnvId() + " does not exist");
                        }

                        EnvProxyConfig config = proxyConfigVO.toEntity();
                        EnvProxyConfig resp = envProxyConfigRepo.save(config);
                        return EnvProxyConfigVO.toVo(resp);
                } catch (Exception ex) {
                        log.error("Error saving EnvProxyConfig: {} , ERROR:{}", LogUtils.sanitizeForLog(ex.getMessage()), LogUtils.sanitizeForLog(ex));
                        throw new DashboardConfigException("500","Failed to save proxy config", ex);
                }

        }

        public EnvProxyConfigVO editProxyConfig(String envConfigId, EnvProxyConfigVO proxyConfigVO) {
                try {
                        if (proxyConfigVO.getEnvId() == null || proxyConfigVO.getEnvId().isEmpty()) {
                                throw new IllegalArgumentException(
                                                "Environment (envId) is required and cannot be null or empty");
                        }
                        if (!environmentRepov3.existsByEnvCode(proxyConfigVO.getEnvId())) {
                                throw new IllegalArgumentException(
                                                "Environment with id " + proxyConfigVO.getEnvId() + " does not exist");
                        }
                        EnvProxyConfig config = envProxyConfigRepo.findById(envConfigId)
                                        .orElseThrow(() -> new RuntimeException("Proxy config not found"));
                        // Set referenced Environment
                        EnvironmentV4 env = new EnvironmentV4();
                        env.setEnvId(proxyConfigVO.getEnvId());
                        config.setEnvironment(env);

                        config.setCompSrvName(proxyConfigVO.getCompSrvName());
                        config.setDomainName(proxyConfigVO.getDomainName());
                        config.setParentFrontend(proxyConfigVO.getParentFrontend());
                        config.setSubpath(proxyConfigVO.getSubpath());
                        config.setLineIndex(proxyConfigVO.getLineIndex());
                        config.setInternalPort(proxyConfigVO.getInternalPort());

                        EnvProxyConfig saved = envProxyConfigRepo.save(config);
                        return EnvProxyConfigVO.toVo(saved);
                } catch (Exception ex) {
                        log.error("Error editing EnvProxyConfig: {} , Error: {}", LogUtils.sanitizeForLog(ex.getMessage()), LogUtils.sanitizeForLog(ex));
                        throw new DashboardConfigException("500", "Failed to edit proxy config", ex);
                }
        }

        public void deleteProxyConfig(String envConfigId) {
                envProxyConfigRepo.deleteById(envConfigId);
        }

        public AccountDetailsVO getAccountDetails(String accountName) {
                NnpAccount nnpAccount = nnpAccountRepository.findByAccName(accountName)
                                .orElseThrow(() -> new DashboardConfigException("400", "This Account Does Not Exist"));

                EnvironmentV4 env = nnpAccount.getEnv();
                String envMasterUserId = env.getEnvCustId();

                if (!StringUtils.hasText(envMasterUserId))
                        throw new DashboardConfigException("500",
                                        "There Is No Master User (EnvCustId) In Env : " + env.getEnvCode());

                NnpUser nnpUser = nnpUserRepo.findById(envMasterUserId)
                                .orElseThrow(() -> new DashboardConfigException("500",
                                                ContentValues.NO_USER_WITH_ID + envMasterUserId));

                return new AccountDetailsVO(
                                nnpUser.getFullName(),
                                nnpUser.getEmail(),
                                nnpUser.getContact(),
                                nnpUser.getAddress(),
                                nnpAccount.getOrganization(),
                                env.getEnvDesc(),
                                env.getAdminComm(),
                                env.getUserComm()
                );
        }

        public void updateAccountDetails(String accountName, @Valid AccountDetailsVO accountDetailsVO) {
                NnpAccount nnpAccount = nnpAccountRepository.findByAccName(accountName)
                        .orElseThrow(() -> new DashboardConfigException("400", "This Account Does Not Exist"));

                EnvironmentV4 env = nnpAccount.getEnv();
                String envMasterUserId = env.getEnvCustId();

                if (!StringUtils.hasText(envMasterUserId))
                        throw new DashboardConfigException("500",
                                "There Is No Master User (EnvCustId) In Env : " + env.getEnvCode());

                NnpUser accountMasterUser = nnpUserRepo.findById(envMasterUserId)
                        .orElseThrow(() -> new DashboardConfigException("500",
                                ContentValues.NO_USER_WITH_ID + envMasterUserId));

                String[] nameArray = accountDetailsVO.contactName().split(" ", 2);
                String firstName = nameArray[0];
                String lastName = nameArray[1];

                accountMasterUser.setFirstName(firstName);
                accountMasterUser.setLastName(lastName);
                accountMasterUser.setEmail(accountDetailsVO.email());
                accountMasterUser.setContact(accountDetailsVO.phone());
                accountMasterUser.setAddress(accountDetailsVO.address());

                nnpAccount.setOrganization(accountDetailsVO.organization());
                env.setAdminComm(accountDetailsVO.administrativeAccess());
                env.setUserComm(accountDetailsVO.userAccess());
                env.setEnvDesc(accountDetailsVO.platformUsePurpose());


                nnpUserRepo.save(accountMasterUser);
                nnpAccountRepository.save(nnpAccount);
        }

}
