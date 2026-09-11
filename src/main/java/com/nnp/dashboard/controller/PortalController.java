package com.nnp.dashboard.controller;

import java.util.List;

import com.nnp.dashboard.dto.AllAccountsResponse;
import com.nnp.dashboard.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nnp.dashboard.service.PortalService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class PortalController {
	
	
	@Autowired
	private PortalService portalService;


	@GetMapping("/country")
	public List<NnpCountryVO> findCountry() {
		return portalService.findAllCountry();
	}

	// NNP_PLAN 
	@GetMapping("/plan/{countryId}")
	public List<NnpPlanVO> getPlansByCountryId(@PathVariable String countryId) {
		return portalService.getPlansByCountryId(countryId);
	}

	@GetMapping("/planComponents/{countryCode}")
	public List<SubscriptionPlanVO> getPlanComponentsByCountryCode(@PathVariable String countryCode) {
		return portalService.getPlanComponentsByCountryCode(countryCode);
	}

	// NNP_ACCOUNT
	@GetMapping("/account/{envId}")
	public List<NnpAccountVO> getAllAccountsByEnvId(@PathVariable String envId) {
		  return portalService.getAllAccountsByEnvId(envId);	
	}

	@GetMapping("/accounts")
	public AllAccountsResponse getAllAccounts(
			@RequestParam(defaultValue = "0", required = false) int pageNumber,
			@RequestParam(defaultValue = "9999", required = false) int pageSize,
			@RequestParam(defaultValue = "ALL", required = false) String accountStatus
	){
		return portalService.getAllAccounts(pageNumber, pageSize, accountStatus);



	}
	
	//NnpAccDcntVo
	@GetMapping("/dcnt/{accId}")
	public List<NnpAccDcntVo> getAllAccDcntByAccId(@PathVariable String accId) {
		  return portalService.getAllAccDcntByAccId(accId)	;
	}
	//NnpAccBillVO
	@GetMapping("/bill/{accId}")
	public List<NnpAccBillVO> getAllBillAccByAccId(@PathVariable String accId) {
		  return portalService.getAllBillAccByAccId(accId);
	}
	
	//NNP_ACC_BILL
	@GetMapping("/billCol/{accBillId}")
	public List<NnpAccBillColVO> getBillCollectionByBillId(@PathVariable String accBillId) {
		  return portalService.getBillCollectionByBillId(accBillId);
	}
	
	//NNP_ACC_BILLLN
	@GetMapping("/billLn/{accBillId}")
	public List<NnpAccBillLnVO> findByAccBillLnAccBillId(@PathVariable String accBillId) {
		  return portalService.findByAccBillLnAccBillId(accBillId);
	}
	
	//NNP_ACC_PLAN
	@GetMapping("/accountPlan/{planId}")
	public List<NnpAccountPlanVO> getPlanAccountByPlanId(@PathVariable String planId) {
		  return portalService.getPlanAccountByPlanId(planId);
	}
	
	//NNP_PLAN_COMP
	/*
	 * @GetMapping("/planComp/{envId}") public List<NnpPlanCompVO>
	 * getPlanCompByEnvId(@PathVariable String envId) { return
	 * portalService.getPlanCompByEnvId(envId); }
	 */
	
	//NNP_ACC_PLAN_COMP
	@GetMapping("/accComp/{planCompId}")
	public List<NnpAccountPlanCompVO> getAccountPlanByPlanCompId(@PathVariable String planCompId) {
		  return portalService.getAccountPlanByPlanCompId(planCompId);
	}
	
	//NNP_ACC_COMM
	@GetMapping("/accComm/{accId}")
	public List<NnpAccCommVO> getAccCommByAccId(@PathVariable String accId) {
		  return portalService.getAccCommByAccId(accId);
	}
	
	//NNP_ACC_SUPPORT
	@GetMapping("/accSupport/{accId}")
	public List<NnpAccSupportVO> getAccSupportByAccId(@PathVariable String accId) {
		  return portalService.getAccSupportByAccId(accId);
	}
	
	//NNP_ENV_USAGE
	@GetMapping("/envUsage/{envId}")
	public List<NnpEnvUsageVO> getEnvUsageByEnvId(@PathVariable String envId) {
		  return portalService.getEnvUsageByEnvId(envId);
	}
	
	//NNP_ENV_APIGW	
	@GetMapping("/envApiGw/{envId}")
	public List<NnpEnvApigwVO> getEnvApigwByEnvId(@PathVariable String envId) {
		  return portalService.getEnvApigwByEnvId(envId);
	}
	
	//NNP_ENV_LOG
	@GetMapping("/envLog/{envId}")
	public List<NnpEnvLogVO> getEnvLogByEnvId(@PathVariable String envId) {
		  return portalService.getEnvLogByEnvId(envId);
	}
	
	//NNP_ENV_PIPELINE
	@GetMapping("/envPipeline/{envId}")
	public List<NnpEnvPipelineVO> getEnvPipelineByEnvId(@PathVariable String envId) {
		  return portalService.getEnvPipelineByEnvId(envId);
	}
}
