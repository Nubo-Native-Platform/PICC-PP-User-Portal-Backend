package com.nnp.dashboard.controller;

import com.nnp.dashboard.service.AccountsService;
import com.nnp.dashboard.vo.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * REST Controller exposing account management APIs.
 * Handles fetching/updating account profile details, subscription plans, billing info, communications, and proxy configurations.
 */
@RestController
@RequestMapping("/accounts")
public class AccountsController {

    private final AccountsService accountsService;

    @Autowired
    public AccountsController(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    /**
     * Retrieves account details by account name.
     *
     * @param accountName unique name identifier of the account
     * @return AccountDetailsVO containing profile information
     */
    @GetMapping("details")
    public AccountDetailsVO getAccountDetails(@RequestParam String accountName){
        return accountsService.getAccountDetails(accountName);
    }

    /**
     * Updates account profile details for a given account name.
     *
     * @param accountName unique name identifier of the account
     * @param accountDetailsVO updated account details payload
     * @return map containing success message status
     */
    @PutMapping("details/{account-name}")
    public Map<String, String> updateAccountDetails(
            @PathVariable("account-name") String accountName,
            @RequestBody @Valid AccountDetailsVO accountDetailsVO
    ){
        accountsService.updateAccountDetails(accountName, accountDetailsVO);
        return Map.of(
                "message", "Update Successful"
        );
    }

    /**
     * Retrieves active subscription plan details and subscribed components for an account.
     *
     * @param accountName unique name identifier of the account
     * @return SubscriptionPlanVO detailing plan, price, and components
     */
    @GetMapping("subscribedComponents")
    public SubscriptionPlanVO getSubscribedComponentsForAccount(@RequestParam String accountName){
        return accountsService.getSubscriptionPlanForAccount(accountName);
    }

    /**
     * Retrieves financial billing overview for an account.
     *
     * @param accountName unique name identifier of the account
     * @return BillingDetailsVO containing payment methods and billing status
     */
    @GetMapping("billingDetails")
    public BillingDetailsVO getBillingDetailsForAccount(@RequestParam String accountName){
        return accountsService.getBillingDetailsForAccount(accountName);
    }

    /**
     * Retrieves list of registered communication logs/contacts for an account.
     *
     * @param accountName unique name identifier of the account
     * @return list of AccountCommunicationVO
     */
    @GetMapping("communications")
    public List<AccountCommunicationVO> getAccountCommunications(@RequestParam String accountName){
        return accountsService.getAccountCommunications(accountName);
    }

    /**
     * Fetches current account bill details including invoice lines and collection status.
     *
     * @param accountName unique name identifier of the account
     * @param billingId optional billing ID filter
     * @return CurrentAccountBill object
     */
    @GetMapping("/bill")
    public CurrentAccountBill getCurrentBillForAccount(
            @RequestParam String accountName,
            @RequestParam(required = false) String billingId
    ){
        return accountsService.getCurrentBillForAccount(accountName, billingId);
    }

    /**
     * Fetches proxy configuration records with optional environment and component service filtering.
     *
     * @param env environment ID filter (optional)
     * @param comp component service name filter (optional)
     * @return list of EnvProxyConfigVO matching filters
     */
	@GetMapping({ "/proxyConfig", "/proxyConfig/{env}/{comp}" })
	public List<EnvProxyConfigVO> getAllProxyConfigs(@PathVariable(name = "env", required = false) String env,
			@PathVariable(name = "comp", required = false) String comp) {
		if (Objects.nonNull(env) && Objects.nonNull(comp)) {
			return accountsService.getAllProxyConfigs().stream().filter(
					proxy -> env.equalsIgnoreCase(proxy.getEnvId()) && comp.equalsIgnoreCase(proxy.getCompSrvName()))
					.collect(Collectors.toList());
		} else {
			return accountsService.getAllProxyConfigs();
		}

	}

    /**
     * Creates a new environment proxy configuration record.
     *
     * @param proxyConfigVO payload containing proxy configuration details
     * @return created EnvProxyConfigVO instance
     */
    @PostMapping("/proxyConfig")
    public EnvProxyConfigVO saveProxyConfig(@RequestBody EnvProxyConfigVO proxyConfigVO) {
        return accountsService.saveProxyConfig(proxyConfigVO);
    }

    /**
     * Updates an existing environment proxy configuration record.
     *
     * @param envConfigId configuration ID to update
     * @param proxyConfigVO updated proxy configuration payload
     * @return updated EnvProxyConfigVO instance
     */
    @PutMapping("/proxyConfig/{envConfigId}")
    public EnvProxyConfigVO editProxyConfig(@PathVariable String envConfigId, @RequestBody EnvProxyConfigVO proxyConfigVO) {
        return accountsService.editProxyConfig(envConfigId, proxyConfigVO);
    }

    /**
     * Deletes a proxy configuration record by ID.
     *
     * @param envConfigId configuration ID to delete
     */
    @DeleteMapping("/proxyConfig/{envConfigId}")
    public void deleteProxyConfig(@PathVariable String envConfigId) {
        accountsService.deleteProxyConfig(envConfigId);
    }
}
