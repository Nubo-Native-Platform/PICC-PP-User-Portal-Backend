package com.nnp.dashboard.service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnp.dashboard.dto.*;
import com.nnp.dashboard.dto.ElementDetailVOV2;
import com.nnp.dashboard.dto.EnvFeatureVOV2;
import com.nnp.dashboard.dto.FeatureElementVOV2;
import com.nnp.dashboard.model.*;
import com.nnp.dashboard.repo.apiecosystem.ApiTransactionRepo;
import com.nnp.dashboard.repo.devsecops.IssueCompBuildRepo;
import com.nnp.dashboard.service.environment.EnvironmentDataMappingService;
import com.nnp.dashboard.service.environment.strategy.*;
import com.nnp.dashboard.utils.LogUtils;
import com.nnp.dashboard.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nnp.dashboard.repo.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class NubonsPortalService {
    
    private static final Logger logger = LoggerFactory.getLogger(NubonsPortalService.class);
	private static final String SUPER_ADMIN_ENV_CODE = "MASTER";

	@Autowired
	private KafkaMessageService kafkaMessageService;
	
	@Autowired
	private ModelMapper mapper;

	@Autowired
	private NnpEnvUsageRepo envUsageRepo;

	@Autowired
	private NnpEnvLogRepo envLogRepo;

	@Autowired
	private NnpEnvPipelineRepo envPipelineRepo;

	@Autowired
	private NnpEnvApigwRepo apigwRepo;

	@Autowired
	private NnpAccCommRepo accCommRepo;

	@Autowired
	private EnvFeatureRepoV2 envFeatureRepoV2;

	@Autowired
	private FeatureElementRepoV2 elementRepoV2;

	@Autowired
	private ElementDetailRepoV2 elementDetailRepoV2;

	@Autowired
	private ChElemDetailRepoV2 chElemDetailRepoV2;

	@Autowired
	private NnpUserRepo nnpUserRepo;

	@Autowired
	private NnpUserRegisterRepo nnpUserRegisterRepo;

	@Autowired
	private SignozClientService signozClientService;

	@Autowired
	private EnvironmentDataMappingService environmentDataMappingService;

	@Autowired
	private LogsDataMappingStrategy logsDataMappingStrategy;

	@Autowired
	private UsageDataMappingStrategy usageDataMappingStrategy;

	@Autowired
	private PipelineDataMappingStrategy pipelineDataMappingStrategy;

	@Autowired
	private ApiGatewayDataMappingStrategy apiGatewayDataMappingStrategy;

	@Autowired
	private IssueCompBuildRepo issueCompBuildRepo;

	@Autowired
	private ApiTransactionRepo apiTransactionRepo;

	@Autowired
	private ObjectMapper objectMapper;

	@Value("${kafka.topic.podusage:nnp-pod-usage}")
	private String podUsageKafkaTpic;

	@Value("${kafka.topic.metricprediction:nnp-metric-prediction}")
	private String metricpredictionKafkaTopic;

	private boolean isSuperAdmin(String envId){
		return envId.equals(SUPER_ADMIN_ENV_CODE);
	}


	public List<NnpEnvUsageVO> getUsageStatisticsByEnvId(String envId) {
		List<NnpEnvUsage> result;
		if(isSuperAdmin(envId)){
			result = envUsageRepo.findTop24HourlyUsage();
		} else {
			result = envUsageRepo.findTop24ByEnvIdOrderByUsageDateDescHourDesc(envId);
		}
		return result.stream()
				.map(usage -> mapper.map(usage, NnpEnvUsageVO.class))
				.toList();
	}


	public List<NnpEnvLogVO> getLogsForLast24HoursByEnvId(String envId) {
		List<NnpEnvLog> result;
		if(isSuperAdmin(envId)){
			result = envLogRepo.findTop24HourlyLogs();
		} else {
			result = envLogRepo.findTop24ByEnvIdOrderByLogDateDescHourDesc(envId);
		}
		return result.stream()
				.map(log -> mapper.map(log, NnpEnvLogVO.class))
				.collect(Collectors.toList());
	}


	public List<Map<String, Object>> getPipelineExecutionsStatsForLast24HoursByEnvId(String envId) {
		return isSuperAdmin(envId) ? envPipelineRepo.getLast24PipelineExecutionsStatsForAllEnv() : envPipelineRepo.getLast24PipelineExecutionsStatsByEnvId(envId);
	}


	public List<Map<String, Object>> getApiGatewayMetrics(String envId) {
		return isSuperAdmin(envId) ? apigwRepo.findApiGatewayResponseDetailsForLast24HoursForAllEnv() : apigwRepo.findApiGatewayResponseDetailsForLast24HoursByEnvId(envId);
	}


	public List<NnpAccCommVO> getAccCommByAccId(String accId) {
		List<NnpAccCommVO> accComm = new ArrayList<NnpAccCommVO>();
		accCommRepo.findByNnpAccountAccId(accId).forEach(env -> {
			accComm.add(mapper.map(env, NnpAccCommVO.class));
		});
		return accComm;
	}

	public List<EnvFeatureVOV2> getSubscribedComponents(String envId) {
		// Retrieve and map environment_features
		List<EnvFeatureVOV2> featuresVo = envFeatureRepoV2.findByEnvId(envId).stream()
				.map(envFeatureV2 -> mapper.map(envFeatureV2, EnvFeatureVOV2.class))
				.sorted(Comparator.comparing(EnvFeatureVOV2::getFeaSeq)).toList();

		// Retrieve and map Features_elements
		for (EnvFeatureVOV2 feature : featuresVo) {
			String feaId = feature.getFeaId();
			List<FeatureElementVOV2> feaElements = elementRepoV2.findByFeatureId(feaId).stream()
					.map(featureElementV2 -> mapper.map(featureElementV2, FeatureElementVOV2.class))
					.sorted(Comparator.comparing(FeatureElementVOV2::getFeaSeq)).toList();
			feature.setFeatureElements(feaElements);

		}
		return featuresVo;
	}


	public List<ElementDetailVOV2> getElementDetailsSpecification(String eldtlId) {

		List<ElementDetailVOV2> elementDetails = elementDetailRepoV2.findByElementId(eldtlId).stream()
				.map(elementDetailV2 -> mapper.map(elementDetailV2, ElementDetailVOV2.class)).toList();

		for (ElementDetailVOV2 elmdtlSpecication : elementDetails) {
			String elementDtlId = elmdtlSpecication.getElementDtlId();

			List<CHElementDetailVO> feaElements = chElemDetailRepoV2.findByElementDtlId(elementDtlId).stream()
					.map(featuresSpecification -> mapper.map(featuresSpecification, CHElementDetailVO.class))
					.sorted(Comparator.comparing(CHElementDetailVO::getElementDtlId)).toList();
			elmdtlSpecication.setChildElementDtls(feaElements);
		}

		return elementDetails;
	}


	public List<Map<String, Object>> getUserDetailsByEnvId(String envId) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Object[]> userObj = null;
		try {
			userObj = nnpUserRepo.findByNnpEnvEnvId(envId);
			if (userObj != null) {
				for (int i = 0; i < userObj.size(); i++) {
					Object arr[] = userObj.get(i);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("userId", arr[0]);
					map.put("envId", arr[1]);
					map.put("roleId", arr[2]);
					map.put("firstName", arr[3]);
					map.put("lastName", arr[3]);
					map.put("email", arr[4]);
					map.put("contact", arr[5]);
					map.put("requestDate", arr[6]);
					map.put("userType", arr[7]);
					map.put("userStatus", arr[8]);
					list.add(map);
				}
			} else {
				return list;
			}
		} catch (Exception e) {
			e.fillInStackTrace();
		}
		return list;
	}

	@Transactional
	public Map<String, Object> saveUser(NnpUserRegisterVO nnpUser) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// check user name,email,contact
			List<NnpUserRegister> email = nnpUserRegisterRepo.findByEmail(nnpUser.getEmail());
			if (!email.isEmpty()) {
				map.put("message", "email already exist");
				return map;
			}

			List<NnpUserRegister> contact = nnpUserRegisterRepo.findByContact(nnpUser.getContact());
			if (!contact.isEmpty()) {
				map.put("message", "contact already exist");
				return map;
			}

			Optional<NnpUserRegister> byId = nnpUserRegisterRepo.findById(nnpUser.getUserId());
			if (byId.isEmpty()) {
				NnpUserRegister user = mapper.map(nnpUser, NnpUserRegister.class);
				user = nnpUserRegisterRepo.save(user);
				map.put("message", user);
			} else {
				map.put("message", "user already Exist :" + nnpUser.getUserId());
				return map;
			}
		} catch (Exception e) {
			e.fillInStackTrace();
		}
		return map;
	}
	

	public NnpUserRegisterVO getUserByUserId(String userId) {
		Optional<NnpUserRegister> user = nnpUserRegisterRepo.findById(userId);
		return mapper.map(user, NnpUserRegisterVO.class);
	}
	
	@Transactional
	public String updateUserDetails(NnpUserRegisterVO nnpUser) {
		try {
			NnpUserRegisterVO targetObje = getUserByUserId(nnpUser.getUserId());
			copyNonNullProperties(nnpUser, targetObje);
			NnpUserRegister target = mapper.map(targetObje, NnpUserRegister.class);
			nnpUserRegisterRepo.saveAndFlush(target);
		} catch (Exception e) {
			e.fillInStackTrace();
		}
		return "Updated";
	}
	
	public void copyNonNullProperties(Object src, Object target) {
		BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
	}

	public static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<String>();
		for (java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null)
				emptyNames.add(pd.getName());
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}

	public List<NnpEnvUsage> getLastHourUsageForAllEnv() {
		try {
			CompletableFuture<List<EnvUsageDto>> usageFuture = CompletableFuture
				.supplyAsync(() -> signozClientService.fetchUsageForAll());
			
			CompletableFuture<Map<String, EnvironmentV4>> envMapFuture = CompletableFuture
				.supplyAsync(() -> environmentDataMappingService.buildEnvironmentMap());
			
			List<EnvUsageDto> usageDtos = usageFuture.join();
			Map<String, EnvironmentV4> envMap = envMapFuture.join();

            return environmentDataMappingService.mapWithAllEnvironments(
					usageDtos, envMap, usageDataMappingStrategy);
			
		} catch (Exception e) {
			return List.of();
		}
	}

	@Transactional
	public List<NnpEnvUsage> saveUsageData(List<NnpEnvUsage> usageEntities) {
		try {
			logger.info("Starting to save {} usage entities", LogUtils.sanitizeForLog( usageEntities.size()));
			
			if (usageEntities.isEmpty()) {
				logger.warn("No usage data to save");
				return List.of();
			}
			
			List<NnpEnvUsage> savedEntities = envUsageRepo.saveAll(usageEntities);
			logger.info("Successfully saved {} usage records", LogUtils.sanitizeForLog(savedEntities.size()));
			return savedEntities;
			
		} catch (Exception e) {
			logger.error("Error saving usage data: {}", LogUtils.sanitizeForLog(e));
			return List.of();
		}
	}
	
	@Transactional
	public List<NnpEnvUsage> fetchAndSaveLastHourUsageData() {
		logger.info("Starting combined fetch and save operation");
		List<NnpEnvUsage> fetchedData = getLastHourUsageForAllEnv();
		return saveUsageData(fetchedData);
	}
	
	public List<NnpEnvLog> getLastHourLogsForAllEnv() {
		try {
			logger.info("Starting to fetch logs data for all environments");
			
			CompletableFuture<List<LogsDataDto>> logsFuture = CompletableFuture
				.supplyAsync(() -> signozClientService.fetchLogsForAll());
			
			CompletableFuture<Map<String, EnvironmentV4>> envMapFuture = CompletableFuture
				.supplyAsync(() -> environmentDataMappingService.buildEnvironmentMap());
			
			List<LogsDataDto> rawLogsDtos = logsFuture.join();
			Map<String, EnvironmentV4> envMap = envMapFuture.join();
			
			List<NnpEnvLog> logEntities = environmentDataMappingService.mapWithAllEnvironments(
				rawLogsDtos, envMap, logsDataMappingStrategy);
			
			logger.info("Successfully processed logs data: {} total environments covered",LogUtils.sanitizeForLog( logEntities.size()));
			return logEntities;
			
		} catch (Exception e) {
			logger.error("Error fetching logs data for all environments : {}", LogUtils.sanitizeForLog(e));
			return List.of();
		}
	}
	
	@Transactional
	public List<NnpEnvLog> saveLogsData(List<NnpEnvLog> logEntities) {
		try {
			logger.info("Starting to save {} log entities", LogUtils.sanitizeForLog(logEntities.size()));
			
			if (logEntities.isEmpty()) {
				logger.warn("No logs data to save");
				return List.of();
			}
			
			List<NnpEnvLog> savedEntities = envLogRepo.saveAll(logEntities);
			logger.info("Successfully saved {} log records", LogUtils.sanitizeForLog(savedEntities.size()));
			return savedEntities;
			
		} catch (Exception e) {
			logger.error("Error saving logs data", e);
			return List.of();
		}
	}
	
	@Transactional
	public List<NnpEnvLog> fetchAndSaveLastHourLogsData() {
		logger.info("Starting combined fetch and save operation for logs");
		List<NnpEnvLog> fetchedData = getLastHourLogsForAllEnv();
		return saveLogsData(fetchedData);
	}

	public List<NnpEnvPipeline> getLastHourPipelineStatsForAllEnv() {
		try {
			logger.info("Starting to fetch pipeline stats for all environments");

			CompletableFuture<Map<String, EnvironmentV4>> envMapFuture = CompletableFuture
					.supplyAsync(() -> environmentDataMappingService.buildEnvironmentMap());

			List<PipelineStatsDto> statsDtos = issueCompBuildRepo.getPipelineStatsLastHour();
			Map<String, EnvironmentV4> envMap = envMapFuture.join();
			List<NnpEnvPipeline> pipelineEntities = environmentDataMappingService.mapWithAllEnvironments(
					statsDtos, envMap, pipelineDataMappingStrategy);
			logger.info("Successfully fetched {} pipeline stats records", LogUtils.sanitizeForLog(statsDtos.size()));
			return pipelineEntities;

		} catch (Exception e) {
			logger.error("Error fetching pipeline stats for all environments:{}", LogUtils.sanitizeForLog(e));
			return List.of();
		}
	}

	@Transactional
	public List<NnpEnvPipeline> savePipelineStatsData(List<NnpEnvPipeline> pipelineEntities) {
		try {
			logger.info("Starting to save {} pipeline stats entities",LogUtils.sanitizeForLog( pipelineEntities.size()));
			if (pipelineEntities.isEmpty()) {
				logger.warn("No pipeline stats data to save");
				return List.of();
			}
			List<NnpEnvPipeline> savedEntities = envPipelineRepo.saveAll(pipelineEntities);
			logger.info("Successfully saved {} pipeline stats records", LogUtils.sanitizeForLog( savedEntities.size()));
			return savedEntities;
		} catch (Exception e) {
			logger.error("Error saving pipeline stats data:{}", LogUtils.sanitizeForLog(e));
			return List.of();
		}
	}

	@Transactional
	public List<NnpEnvPipeline> fetchAndSaveLastHourPipelineStatsData() {
		logger.info("Starting combined fetch and save operation for pipeline stats");
		List<NnpEnvPipeline> fetchedData = getLastHourPipelineStatsForAllEnv();
		return savePipelineStatsData(fetchedData);
	}

	public List<NnpEnvApigw> getLastHourApigwStatsForAllEnv() {
		try {
			logger.info("Starting to fetch API gateway stats for all environments");

			CompletableFuture<Map<String, EnvironmentV4>> envMapFuture = CompletableFuture
					.supplyAsync(() -> environmentDataMappingService.buildEnvironmentMap());

			List<ApigatwayStatsDto> statsDtos = apiTransactionRepo.getApigatewayStatsLastHour();
			Map<String, EnvironmentV4> envMap = envMapFuture.join();
			List<NnpEnvApigw> apigwEntities = environmentDataMappingService.mapWithAllEnvironments(
					statsDtos, envMap, apiGatewayDataMappingStrategy);
			logger.info("Successfully fetched {} API gateway stats records", LogUtils.sanitizeForLog( statsDtos.size()));
			return apigwEntities;

		} catch (Exception e) {
			logger.error("Error fetching API gateway stats for all environments:{}", LogUtils.sanitizeForLog(e));
			return List.of();
		}
	}

	@Transactional
	public List<NnpEnvApigw> saveApigwStatsData(List<NnpEnvApigw> apigwEntities) {
		try {
			logger.info("Starting to save {} API gateway stats entities", LogUtils.sanitizeForLog( apigwEntities.size()));
			if (apigwEntities.isEmpty()) {
				logger.warn("No API gateway stats data to save");
				return List.of();
			}
			List<NnpEnvApigw> savedEntities = apigwRepo.saveAll(apigwEntities);
			logger.info("Successfully saved {} API gateway stats records", LogUtils.sanitizeForLog(savedEntities.size()));
			return savedEntities;
		} catch (Exception e) {
			logger.error("Error saving API gateway stats data : {}", LogUtils.sanitizeForLog(e));
			return List.of();
		}
	}

	@Transactional
	public List<NnpEnvApigw> fetchAndSaveLastHourApigwStatsData() {
		logger.info("Starting combined fetch and save operation for API gateway stats");
		List<NnpEnvApigw> fetchedData = getLastHourApigwStatsForAllEnv();
		return saveApigwStatsData(fetchedData);
	}

	public List<PodUsageDto> getPodUsageStats(){
		return signozClientService.fetchPodUsageStats();
	}

	public List<PodUsageDto> getPodUsageStatsForNamespaces(List<String> namespaces){
		return signozClientService.fetchPodUsageStatsForNamespaces(namespaces);
	}

	public List<KafkaMessageDto> fetchKafkaMessagesForTopicInRange(String topic, long startTimestamp, long endTimestamp) {
		return kafkaMessageService.getMessagesForTopicInRange(topic, startTimestamp, endTimestamp).stream().map(rec -> new KafkaMessageDto(
			rec.key() != null ? rec.key() : null,
			rec.value(),
			rec.partition(),
			rec.offset(),
			rec.timestamp()
		)).toList();
	}

	public List<Map<String, Object>> getPodUsageKafkaMessagesInRange(long startTimestamp, long endTimestamp) {
		List<Map<String, Object>> result = new ArrayList<>();
		fetchKafkaMessagesForTopicInRange(podUsageKafkaTpic, startTimestamp, endTimestamp)
				.forEach(msg -> {
					Object value = msg.getValue();
					Long timestamp = msg.getTimestamp();
					if (value instanceof String strValue) {
						try {
							List<PodUsageDto> list = objectMapper.readValue(
									strValue,
									objectMapper.getTypeFactory()
											.constructCollectionType(List.class, PodUsageDto.class));

							if (list != null && !list.isEmpty()) {
								Map<String, Object> data = Map.of(
										"messages", list,
										"timestamp", timestamp,
										"name", "k8s-metrics"
								);
								result.add(data);
							}
						} catch (Exception e) {
							logger.error("Failed to parse Kafka message: {}", LogUtils.sanitizeForLog(e.getMessage()));
						}
					}
				});
		return result;
	}

	public void sendPredctionToTopic(PodPredictionDto podPrediction){
		kafkaMessageService.sendMessage(metricpredictionKafkaTopic, podPrediction);
	}

	public List<Map<String, Object>> getPredictionKafkaMessageInRange(long startTimestamp, long endTimestamp){
		List<Map<String, Object>> result = new ArrayList<>();
		fetchKafkaMessagesForTopicInRange(metricpredictionKafkaTopic, startTimestamp, endTimestamp)
				.forEach(msg -> {
					Object value = msg.getValue();
					Long timestamp = msg.getTimestamp();
					if (value instanceof String strValue) {
						try {
							PodPredictionDto p = objectMapper.readValue(
									strValue,
                                    PodPredictionDto.class);

							if (p != null ) {
								Map<String, Object> data = Map.of(
										"messages", p,
										"timestamp", timestamp,
										"name", "k8s-metrics"
								);
								result.add(data);
							}
						} catch (Exception e) {
							logger.error("Failed to parse Kafka message:{} ",LogUtils.sanitizeForLog(e.getMessage()));
						}
					}
				});
		return result;
	}

}
