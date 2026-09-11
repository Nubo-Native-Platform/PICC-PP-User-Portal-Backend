package com.nnp.dashboard.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.nnp.dashboard.dto.AllAccountsResponse;
import com.nnp.dashboard.model.*;
import com.nnp.dashboard.vo.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.nnp.dashboard.repo.NnpAccBillColRepo;
import com.nnp.dashboard.repo.NnpAccBillLnRepo;
import com.nnp.dashboard.repo.NnpAccBillRepo;
import com.nnp.dashboard.repo.NnpAccCommRepo;
import com.nnp.dashboard.repo.NnpAccDcntRepo;
import com.nnp.dashboard.repo.NnpAccSupportRepo;
import com.nnp.dashboard.repo.NnpAccountPlanCompRepo;
import com.nnp.dashboard.repo.NnpAccountPlanRepo;
import com.nnp.dashboard.repo.NnpAccountRepository;
import com.nnp.dashboard.repo.NnpCountryRepository;
import com.nnp.dashboard.repo.NnpEnvApigwRepo;
import com.nnp.dashboard.repo.NnpEnvLogRepo;
import com.nnp.dashboard.repo.NnpEnvPipelineRepo;
import com.nnp.dashboard.repo.NnpEnvUsageRepo;
import com.nnp.dashboard.repo.NnpPlanCompRepo;
import com.nnp.dashboard.repo.NnpPlanRepository;
import org.springframework.util.CollectionUtils;


@Service
public class PortalService {

	@Autowired
	private NnpCountryRepository nnpCountryRepository;

	@Autowired
	private NnpPlanRepository nnpPlanRepository;

	@Autowired
	private NnpAccountRepository nnpAccountRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private NnpAccDcntRepo accDcntRepo;

	@Autowired
	private NnpAccBillRepo billRepo;

	@Autowired
	private NnpAccBillColRepo accBillColRepo;

	@Autowired
	private NnpAccBillLnRepo accBillLnRepo;

	@Autowired
	private NnpAccountPlanRepo accountPlanRepo;

	@Autowired
	private NnpPlanCompRepo compRepo;

	@Autowired
	private NnpAccountPlanCompRepo accPlanComp;

	@Autowired
	private NnpAccCommRepo accCommRepo;

	@Autowired
	private NnpAccSupportRepo accSupportRepo;

	@Autowired
	private NnpEnvUsageRepo envUsageRepo;

	@Autowired
	private NnpEnvApigwRepo apigwRepo;

	@Autowired
	private NnpEnvLogRepo envLogRepo;

	@Autowired
	private NnpEnvPipelineRepo envPipelineRepo;

	// 1.NNP_COUNTRY
	
	public List<NnpCountryVO> findAllCountry() {
		List<NnpCountryVO> country = new ArrayList<NnpCountryVO>();
		nnpCountryRepository.findAll().forEach(env -> {
			country.add(modelMapper.map(env, NnpCountryVO.class));
		});
		return country;
	}

	// 2.nnp_plan
	
	public List<NnpPlanVO> getPlansByCountryId(String countryId) {
		List<NnpPlanVO> nnpPlan = new ArrayList<NnpPlanVO>();
		List<NnpPlan> nnpPlanList = nnpPlanRepository.findByNnpCountryCountryId(countryId);
		nnpPlanList.forEach(env -> {
			nnpPlan.add(modelMapper.map(env, NnpPlanVO.class));
		});
		return nnpPlan;
	}

	// 3.NNP_ACCOUNT
	
	public List<NnpAccountVO> getAllAccountsByEnvId(String envId) {
		List<NnpAccountVO> accountVO = new ArrayList<NnpAccountVO>();
		nnpAccountRepository.findByEnvEnvId(envId).forEach(env -> {
			accountVO.add(modelMapper.map(env, NnpAccountVO.class));
		});
		return accountVO;
	}

	// 4.NnpAccDcnt
	
	public List<NnpAccDcntVo> getAllAccDcntByAccId(String accId) {
		List<NnpAccDcntVo> accDcnt = new ArrayList<NnpAccDcntVo>();
		accDcntRepo.findByNnpAccountAccId(accId).forEach(env -> {
			accDcnt.add(modelMapper.map(env, NnpAccDcntVo.class));
		});
		return accDcnt;
	}

	// 5.NNP_ACCOUNT_BILL
	
	public List<NnpAccBillVO> getAllBillAccByAccId(String accId) {
		List<NnpAccBillVO> bill = new ArrayList<NnpAccBillVO>();
		billRepo.findByNnpAccountAccId(accId).forEach(env -> {
			bill.add(modelMapper.map(env, NnpAccBillVO.class));
		});
		return bill;
	}

	// 6.NNP_ACC_BILL_COL
	
	public List<NnpAccBillColVO> getBillCollectionByBillId(String accBillId) {
		// TODO Auto-generated method stub
		List<NnpAccBillColVO> col = new ArrayList<NnpAccBillColVO>();
		accBillColRepo.findByAccBillAccBillId(accBillId).forEach(coldata -> {
			col.add(modelMapper.map(coldata, NnpAccBillColVO.class));
		});
		return col;
	}

	// NNP_ACC_BILL_LN
	
	public List<NnpAccBillLnVO> findByAccBillLnAccBillId(String accBillId) {
		// TODO Auto-generated method stub
		List<NnpAccBillLnVO> billLn = new ArrayList<NnpAccBillLnVO>();
		accBillLnRepo.findByAccBillAccBillId(accBillId).forEach(billLnData -> {
			billLn.add(modelMapper.map(billLnData, NnpAccBillLnVO.class));
		});
		return billLn;
	}

	// NNP_ACCOUNT_PLANS
	
	public List<NnpAccountPlanVO> getPlanAccountByPlanId(String planId) {
		// TODO Auto-generated method stub
		List<NnpAccountPlanVO> accountPlan = new ArrayList<NnpAccountPlanVO>();
		accountPlanRepo.findByNnpPlanPlanId(Long.parseLong(planId)).forEach(accPlan -> {
			accountPlan.add(modelMapper.map(accPlan, NnpAccountPlanVO.class));
		});
		return accountPlan;
	}

	// NNP_ACC_PLAN_COMP
	
	public List<NnpAccountPlanCompVO> getAccountPlanByPlanCompId(String planCompId) {
		// TODO Auto-generated method stub
		List<NnpAccountPlanCompVO> accountPlanComp = new ArrayList<NnpAccountPlanCompVO>();
		accPlanComp.findByNnpAccountPlanAccPlanId(planCompId).forEach(env -> {
			accountPlanComp.add(modelMapper.map(env, NnpAccountPlanCompVO.class));
		});
		return accountPlanComp;
	}

	// NNP_ACC_COMM
	
	public List<NnpAccCommVO> getAccCommByAccId(String accId) {
		// TODO Auto-generated method stub
		List<NnpAccCommVO> accComm = new ArrayList<NnpAccCommVO>();
		accCommRepo.findByNnpAccountAccId(accId).forEach(env -> {
			accComm.add(modelMapper.map(env, NnpAccCommVO.class));
		});
		return accComm;
	}

	// NNP_ACC_SUPPORT
	
	public List<NnpAccSupportVO> getAccSupportByAccId(String accId) {
		// TODO Auto-generated method stub
		List<NnpAccSupportVO> accSupport = new ArrayList<NnpAccSupportVO>();
		accSupportRepo.findByNnpAccountAccId(accId).forEach(acc -> {
			accSupport.add(modelMapper.map(acc, NnpAccSupportVO.class));
		});
		return accSupport;
	}

	// NNP_ENV_USAGE
	
	public List<NnpEnvUsageVO> getEnvUsageByEnvId(String envId) {
		// TODO Auto-generated method stub
		List<NnpEnvUsageVO> envUsage = new ArrayList<NnpEnvUsageVO>();
		envUsageRepo.findByEnvId(envId).forEach(env -> {
			envUsage.add(modelMapper.map(env, NnpEnvUsageVO.class));
		});
		return envUsage;
	}

	// NNP_ENV_APIGW
	
	public List<NnpEnvApigwVO> getEnvApigwByEnvId(String envId) {
		// TODO Auto-generated method stub
		List<NnpEnvApigwVO> envApiGw = new ArrayList<NnpEnvApigwVO>();
		apigwRepo.findByEnvId(envId).forEach(env -> {
			envApiGw.add(modelMapper.map(env, NnpEnvApigwVO.class));
		});
		return envApiGw;
	}

	// NNP_ENV_LOG
	
	public List<NnpEnvLogVO> getEnvLogByEnvId(String envId) {
		// TODO Auto-generated method stub
		List<NnpEnvLogVO> envLog = new ArrayList<NnpEnvLogVO>();
		envLogRepo.findByEnvId(envId).forEach(env -> {
			envLog.add(modelMapper.map(env, NnpEnvLogVO.class));
		});
		return envLog;
	}

	//NNP_ENV_PIPELINE
	
	public List<NnpEnvPipelineVO> getEnvPipelineByEnvId(String envId) {
		// TODO Auto-generated method stub
		List<NnpEnvPipelineVO> piplineVo = new ArrayList<NnpEnvPipelineVO>();
		envPipelineRepo.findByEnvId(envId).forEach(env -> {
			piplineVo.add(modelMapper.map(env, NnpEnvPipelineVO.class));
		});
		return piplineVo;
	}

	// TODO : Slow API , Optimize If Required (Not Sure If This API Used Now)
	public List<SubscriptionPlanVO> getPlanComponentsByCountryCode(String countryCode) {

		List<NnpPlan> nnpPlans = nnpPlanRepository.findByNnpCountry_CountryCode(countryCode);

		if (CollectionUtils.isEmpty(nnpPlans))
			throw new RuntimeException("There Are No Plans With This Country Code");

		return nnpPlans.stream()
				.map(this::getSubscriptionPlanVOFromNnpPlan)
				.toList();

	}

	private SubscriptionPlanVO getSubscriptionPlanVOFromNnpPlan(NnpPlan nnpPlan){

		String currency = nnpPlan.getNnpCountry().getCurrency();

		var groupedComponents = nnpPlan.getPlanComps().stream()
				.collect(Collectors.groupingBy(
						pc -> pc.getPlanCompGroup().getCompGroupTitle(),
						Collectors.mapping(pc -> constructPlanComponent(pc, currency), Collectors.toList())
				));

		return new SubscriptionPlanVO(
				0L,
				nnpPlan.getHostPlName(),
				null,
				null,
				null,
				null,
				groupedComponents
		);
	}

	private SubscriptionPlanVO.PlanComponent constructPlanComponent(NnpPlanComp nnpPlanComp, String currency){
		EnvBbComp envBbComp = nnpPlanComp.getEnvBbComp();
		List<SubscriptionPlanVO.PlanFeature> features = envBbComp.getEnvCompSpecs().stream()
				.map(eb -> new SubscriptionPlanVO.PlanFeature(eb.getEnvSpecName(), eb.getEnvSpecId()))
				.toList();
		return new SubscriptionPlanVO.PlanComponent(
				envBbComp.getEnvCompId(),
				envBbComp.getEnvCompName(),
				new PriceVO(currency, Double.parseDouble(nnpPlanComp.getBasePrice())),
				features
		);
	}

	public AllAccountsResponse getAllAccounts(int pageNumber, int pageSize, String accountStatus) {

		PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

		Page<NnpAccount> nnpAccountsPage;
		if (accountStatus.equalsIgnoreCase("ALL"))
			nnpAccountsPage = nnpAccountRepository.findAll(pageRequest);
		else
			nnpAccountsPage = nnpAccountRepository.findByAccStatusOrderByCreatedOnDesc(accountStatus, pageRequest);

		List<NnpAccountVO> nnpAccounts = nnpAccountsPage.stream()
				.map(a -> modelMapper.map(a, NnpAccountVO.class))
				.toList();

		return new AllAccountsResponse(
			nnpAccounts, nnpAccountsPage.getTotalPages(), nnpAccountsPage.getTotalElements(), pageSize, pageNumber
		);
	}
}
