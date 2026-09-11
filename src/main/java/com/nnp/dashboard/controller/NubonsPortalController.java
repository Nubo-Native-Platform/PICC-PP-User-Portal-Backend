package com.nnp.dashboard.controller;

import java.util.*;

import com.nnp.dashboard.dto.PodPredictionDto;
import com.nnp.dashboard.dto.PodUsageDto;
import com.nnp.dashboard.model.NnpEnvApigw;
import com.nnp.dashboard.model.NnpEnvLog;
import com.nnp.dashboard.model.NnpEnvPipeline;
import com.nnp.dashboard.model.NnpEnvUsage;
import com.nnp.dashboard.vo.NnpEnvUsageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nnp.dashboard.dto.ElementDetailVOV2;
import com.nnp.dashboard.dto.EnvFeatureVOV2;
import com.nnp.dashboard.service.NubonsPortalService;
import com.nnp.dashboard.vo.NnpAccCommVO;
import com.nnp.dashboard.vo.NnpEnvLogVO;
import com.nnp.dashboard.vo.NnpUserRegisterVO;

@RestController
@RequestMapping("/portal")
public class NubonsPortalController {

	@Autowired
	private NubonsPortalService nubonsPortalService;

	@GetMapping("/lastHourUsage")
	public List<NnpEnvUsage> getLastHourUsage() {
		return nubonsPortalService.getLastHourUsageForAllEnv();
	}

	@GetMapping("/lastHourLogs")
	public List<NnpEnvLog> getLastHourLogs() {
		return nubonsPortalService.getLastHourLogsForAllEnv();
	}

	@GetMapping("/lastHourPipelineStats")
	public List<NnpEnvPipeline> getLastHourPipelineStats() {
		return nubonsPortalService.getLastHourPipelineStatsForAllEnv();
	}

	@GetMapping ("/lastMinutePodUsage")
	public List<PodUsageDto> getLastMinutePodUsage() { return  nubonsPortalService.getPodUsageStats(); }

	@GetMapping("/lastHourApiGatewayStats")
	public List<NnpEnvApigw> getLastHourApiGatewayStats() {
		return nubonsPortalService.getLastHourApigwStatsForAllEnv();
	}

	@GetMapping("/last24UsageMemory/{envId}")
	public List<NnpEnvUsageVO> getUsageStatistics(@PathVariable String envId) {
		return nubonsPortalService.getUsageStatisticsByEnvId(envId);
	}

	@GetMapping("last24Log/{envId}")
	public List<NnpEnvLogVO> getLogsForLast24Hours(@PathVariable String envId) {
		return nubonsPortalService.getLogsForLast24HoursByEnvId(envId);
	}

	@GetMapping("last24PipelineUsage/{envId}")
	public  List<Map<String, Object>> getPipelineExecutions(@PathVariable String envId) {
		return nubonsPortalService.getPipelineExecutionsStatsForLast24HoursByEnvId(envId);
	}

	@GetMapping("last24ApiGatewayUsage/{envId}")
	public List<Map<String, Object>> getApiGatewayMetrics(@PathVariable String envId) {
		return nubonsPortalService.getApiGatewayMetrics(envId);
	}

	@GetMapping("/accComm/{accId}")
	public List<NnpAccCommVO> getAccCommByAccId(@PathVariable String accId) {
		return nubonsPortalService.getAccCommByAccId(accId);
	}
	
	@GetMapping("/subscribeComponents/{envId}")
	public List<EnvFeatureVOV2> getSubscribedComponents(@PathVariable String envId){
		return nubonsPortalService.getSubscribedComponents(envId);
	}
	
	@GetMapping("/elmdtlSpecication/{eldtlId}")
	public List<ElementDetailVOV2> getElementDetailsSpecification(@PathVariable String eldtlId){
		return nubonsPortalService.getElementDetailsSpecification(eldtlId);
	}
	
	@GetMapping("userDetails/{envId}")
	public List<Map<String, Object>> getUserDetailsByEnvId(@PathVariable String envId) {
		return nubonsPortalService.getUserDetailsByEnvId(envId);
	}
	
	@PostMapping("user")
	public Map<String, Object> createUser(@RequestBody NnpUserRegisterVO nnpUser) {
		return nubonsPortalService.saveUser(nnpUser);
	}
	
	@GetMapping("user/{userId}")
	public NnpUserRegisterVO getUserByUserId(@PathVariable String userId) {
		return nubonsPortalService.getUserByUserId(userId);
	}
	
	@PutMapping("user")
	public String updateUserDetails(@RequestBody NnpUserRegisterVO user) {
		return nubonsPortalService.updateUserDetails(user);
	}
	
	

    /**
     * Get Kafka messages for a topic within a datetime range (epoch millis).
     * Example: /portal/kafka/messages?start=1699000000000&end=1699003600000
     */
	@GetMapping("/podusage/messages")
	public List<Map<String, Object>> getPodUsageKafkaMessagesInRange(@RequestParam long start, @RequestParam long end) {
		return nubonsPortalService.getPodUsageKafkaMessagesInRange(start, end);
	}

	@GetMapping("/metricprediction/messages")
	public List<Map<String, Object>> getPredictionKafkaMessagesInRange(@RequestParam long start, @RequestParam long end) {
		return nubonsPortalService.getPredictionKafkaMessageInRange(start, end);
	}

	@PostMapping("/metricprediction/send")
	public ResponseEntity sendPredctionToTopic(@RequestBody PodPredictionDto podPrediction){
		nubonsPortalService.sendPredctionToTopic(podPrediction);
		return new ResponseEntity<>(podPrediction, HttpStatus.OK);
	}
}
