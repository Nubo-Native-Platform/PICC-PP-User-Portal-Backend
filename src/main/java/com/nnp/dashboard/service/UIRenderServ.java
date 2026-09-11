package com.nnp.dashboard.service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.nnp.dashboard.exception.DashboardConfigExceptionMessage;
import com.nnp.dashboard.utils.LogUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.nnp.dashboard.exception.DashboardConfigException;
import com.nnp.dashboard.exception.DashboardException;
import com.nnp.dashboard.model.CHElementDetail;
import com.nnp.dashboard.model.CHElementDetailV2;
import com.nnp.dashboard.model.ElementDetail;
import com.nnp.dashboard.model.EnvFeature;
import com.nnp.dashboard.model.EnvUserAccess;
import com.nnp.dashboard.model.Environment;
import com.nnp.dashboard.model.EnvironmentV2;
import com.nnp.dashboard.model.EnvironmentV3;
import com.nnp.dashboard.model.FeatureElement;
import com.nnp.dashboard.model.NnpUser;
import com.nnp.dashboard.model.UserConfigV2;
import com.nnp.dashboard.repo.ChElemDetailRepo;
import com.nnp.dashboard.repo.ChElemDetailRepoV2;
import com.nnp.dashboard.repo.ElementDetailRepo;
import com.nnp.dashboard.repo.ElementDetailRepoV2;
import com.nnp.dashboard.repo.EnvFeatureRepo;
import com.nnp.dashboard.repo.EnvFeatureRepoV2;
import com.nnp.dashboard.repo.EnvUserRepo;
import com.nnp.dashboard.repo.EnvironmentRepo;
import com.nnp.dashboard.repo.EnvironmentRepoV2;
import com.nnp.dashboard.repo.EnvironmentRepoV3;
import com.nnp.dashboard.repo.FeatureElementRepo;
import com.nnp.dashboard.repo.FeatureElementRepoV2;
import com.nnp.dashboard.repo.NnpUserRepo;
import com.nnp.dashboard.repo.UserConfigRepoV2;
import com.nnp.dashboard.vo.CHElementDetailVOV2;
import com.nnp.dashboard.vo.ElementDetailVOV2;
import com.nnp.dashboard.vo.EnvFeatureVOV2;
import com.nnp.dashboard.vo.EnvironmentVO;
import com.nnp.dashboard.vo.EnvironmentVOV3;
import com.nnp.dashboard.vo.FeatureElementVOV2;
import com.nnp.dashboard.vo.UserAccessibilitySearch;
import com.nnp.dashboard.vo.UserAccessibilitySearchV2;

import lombok.extern.slf4j.Slf4j;


/**
 * @author AC
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UIRenderServ {
    private final EnvUserRepo userRepo;
    private final NnpUserRepo nnpUserRepo;
    private final EnvironmentRepo envRepository;
    private final EnvFeatureRepo envFeaRepository;
    private final FeatureElementRepo featureElementRepo;
    private final ElementDetailRepo elementDetailRepo;
    private final ChElemDetailRepo chElementDetailRepo;
    private final ModelMapper modelMapper;
    private final EnvironmentRepoV3 envRepositoryV3;
    private final EnvironmentRepoV2 environmentRepoV2;
    private final EnvFeatureRepoV2 envFeatureRepoV2;
    private final FeatureElementRepoV2 featureElementRepoV2;
    private final ElementDetailRepoV2 elementDetailRepoV2;
    private final ChElemDetailRepoV2 chElemDetailRepoV2;
    private final UserConfigRepoV2 userConfigRepoV2;
    private final UrlListCacheService urlCacheService;



    public Environment getEnvByCode(String envCode) {
        return envRepository.findByEnvCode(envCode);
    }

    public EnvFeature getFeatureById(String id) {
        return envFeaRepository.findById(id).get();
    }

    public List<EnvFeature> getAllFeatureByEnvAndUser(String envCode, String userId) {
        Environment env = envRepository.findByEnvCode(envCode);
        EnvUserAccess usrAcc = userRepo.findByUserId(userId).getFirst();
        return envFeaRepository.findByEnvAndUserList_UserIdOrderByFeaSeqAsc(env, usrAcc.getUserId());

    }

    public List<FeatureElement> getAllFeaElmByFeaAndUser(String feaId, String userId) {
        EnvFeature feature = envFeaRepository.findById(feaId).get();
        EnvUserAccess usrAcc = userRepo.findByUserId(userId).get(0);
        return featureElementRepo.findByFeatureAndUserList_UserId(feature, usrAcc.getUserId());
    }

    public List<ElementDetail> getAllElemDtlByFeaDtlAndUser(String elemId, String userId) {
        FeatureElement feaElement = featureElementRepo.findById(elemId).get();
        EnvUserAccess usrAcc = userRepo.findByUserId(userId).getFirst();
        return elementDetailRepo.findByFeaElementAndUserList_UserId(feaElement, usrAcc.getUserId());

    }

    public List<CHElementDetail> getAllChElemDtlByElmDtlAndUser(String elementDtlId, String userId) {
        ElementDetail elementDtl = elementDetailRepo.findById(elementDtlId).get();
        EnvUserAccess usrAcc = userRepo.findByUserId(userId).getFirst();
        return chElementDetailRepo.findByPrElemDtlAndUserList_UserId(elementDtl, usrAcc.getUserId());
    }

    public List<EnvironmentVO> getAllEnvironments() {
        List<EnvironmentVO> envs = new ArrayList<EnvironmentVO>();
        envRepository.findAll().forEach(env ->envs.add(modelMapper.map(env, EnvironmentVO.class)));
        return envs;
    }

    public FeatureElement getFeaElemByID(String id) {
        return featureElementRepo.findById(id).get();
    }

    public ElementDetail getElemDetailByID(String id) {
        return elementDetailRepo.findById(id).get();
    }

    public List<CHElementDetail> getHomeByEnvCode(String envCode, String userId) {
        List<CHElementDetail> chElemDtlList = new ArrayList<CHElementDetail>();
        getEnvByCode(envCode).getEnvFeatures().forEach(
                envFeature -> envFeature.getFeatureElements()
                        .forEach(feaElem -> feaElem.getElementDetails()
                                .forEach(elemDtl -> this.getAllChElemDtlByElmDtlAndUser(elemDtl.getElementDtlId(), userId)
                                        .forEach(chElemDtl -> {
                                            if ("true".equalsIgnoreCase(chElemDtl.getElementDtlHome())) {
                                                chElemDtl.setAssigned(true);
                                                chElemDtlList.add(chElemDtl);
                                            }
                                        }))));

        return chElemDtlList;
    }

    /**
     * @author ebasusa
     */
    public List<CHElementDetail> getHomeByEnvCodeUserId(String envCode, String userId) {
        Environment env = envRepository.findByEnvCode(envCode);
        List<CHElementDetail> list = new ArrayList<CHElementDetail>();
        if (env != null) {
            List<EnvUserAccess> userConfig = userRepo.findByUserIdAndEnvIdAndChElmDetailIdIsNotNull(userId, env.getEnvId());
            list = userConfig.stream()
                    .map(config -> chElementDetailRepo.findByChElementDtlIdAndElementDtlHome(config.getChElmDetailId(), "true"))
                    .filter(Objects::nonNull).toList();
        }
        return list;
    }

    public boolean isUserValidForUrl(UserAccessibilitySearch sc) {
    	
    	boolean isUserValid = false;
    	
    	try {
    		
			String scrUrlHost = sc.getUrl();
			//get urls from cache using userid and envcode
			List<String> urls = urlCacheService.getUrls(sc.getUserId(), sc.getEnvCode());
			if(urls.contains(scrUrlHost)) {
            isUserValid = true;
			}else {
				log.error("url - {} NOT FOUND in the redis cache for user {} and environment {}",LogUtils.sanitizeForLog(scrUrlHost),LogUtils.sanitizeForLog(sc.getUserId()),LogUtils.sanitizeForLog(sc.getEnvCode()));
				
				//retrieve url from db and store in cache
				//get env id from env code
				EnvironmentV3 env = envRepositoryV3.findByEnvCode(sc.getEnvCode());
				//get child element id by user id from user access
				List<UserConfigV2> userConfig = userConfigRepoV2.findByUserIdAndEnvIdAndChElmDetail_ChElementDtlIdIsNotNull(sc.getUserId(),env.getEnvId());
				for (UserConfigV2 config : userConfig) {
					//for ech child element spec get the url 
					URL targetUrl = extractURL(config.getChElmDetail().getElementDtlURL());
					//store url host in cache
					if(Objects.nonNull(targetUrl) && scrUrlHost.equalsIgnoreCase(targetUrl.getHost())) {
							urlCacheService.addUrl(sc.getUserId(),	env.getEnvId(), targetUrl.getHost());
							isUserValid = true;
						}


                }
			}
			
			
		} catch (MalformedURLException e) {
            throw new DashboardConfigException(new DashboardConfigExceptionMessage("500",e.getMessage()));
		}
    	return isUserValid;
    }
    private URL extractURL(String url) throws MalformedURLException {
        if (Objects.isNull(url) || url.isBlank()) {
            return null;
        }

        try {
            URI uri = URI.create(url.trim());

            String scheme = uri.getScheme();
            String host = uri.getHost();

            if (host == null
                    || scheme == null
                    || !("http".equalsIgnoreCase(scheme)
                    || "https".equalsIgnoreCase(scheme))) {
                return null;
            }

            URL targetUrl = uri.toURL();

            log.info(
                    "Extracted URL host: {}",
                    LogUtils.sanitizeForLog(targetUrl.getHost())
            );

            return targetUrl;

        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    public List<EnvironmentVOV3> getAllEnvironmentsV3() {
        List<EnvironmentVOV3> envs = new ArrayList<>();
        envRepositoryV3.findAll().forEach(env ->
            envs.add(modelMapper.map(env, EnvironmentVOV3.class))
        );
        return envs;
    }

    public EnvironmentVOV3 getEnvironmentByEnvIdV3(String envId) {
        Optional<EnvironmentV3> environmentV3 = envRepositoryV3.findById(envId);
        EnvironmentVOV3 environmentVOV3 = null;
        if (environmentV3.isPresent())
            environmentVOV3 = modelMapper.map(environmentV3.get(), EnvironmentVOV3.class);
        else
            throw new DashboardException("env not found");
        return environmentVOV3;
    }

    public List<EnvFeatureVOV2> getFeaturesByEnvIdV2(String envId) {

        return envFeatureRepoV2.findByEnvId(envId).stream()
                .map(envFeatureV2 -> modelMapper.map(envFeatureV2, EnvFeatureVOV2.class))
                .toList();
    }

    public List<FeatureElementVOV2> getFeaElemByFeatureIdV2(String feaId) {

        return featureElementRepoV2.findByFeatureId(feaId).stream()
                .map(featureElementV2 -> modelMapper.map(featureElementV2, FeatureElementVOV2.class))
                .toList();
    }

    public List<ElementDetailVOV2> getElemDtlsByFeatureElemIdV2(String feaElemId) {
        return elementDetailRepoV2.findByElementId(feaElemId).stream()
                .map(elementDetailV2 -> modelMapper.map(elementDetailV2, ElementDetailVOV2.class))
                .toList();
    }

    public List<CHElementDetailVOV2> getChildElemDtlByElemDetailIdAndUserIdV2(String elemDtlId, String userId) {

        NnpUser user = nnpUserRepo.findById(userId)
                .orElseThrow(() -> new DashboardConfigException("400", "There Is No User With The Given User Id"));

        List<CHElementDetailV2> childElements;
        if (user.getUserType().equalsIgnoreCase("superAdmin"))
            childElements = chElemDetailRepoV2.findByElementDtlId(elemDtlId);
        else
            childElements = userConfigRepoV2.findByChElmDetail_ElementDtlIdAndUserId(elemDtlId, userId)
                    .stream()
                    .map(UserConfigV2::getChElmDetail)
                    .toList();

        return childElements.stream()
                .map(chElementDetailV2 -> modelMapper.map(chElementDetailV2, CHElementDetailVOV2.class))
                .toList();
    }

    public List<CHElementDetailVOV2> getHomelinkByEnvIdAndUserIdV2(String envId, String userId) {
        Optional<EnvironmentV2> environmentV2 = environmentRepoV2.findById(envId);
        List<CHElementDetailVOV2> chElementDetailVOV2List = new ArrayList<>();
        if (environmentV2.isPresent()) {
            List<CHElementDetailVOV2> list = new ArrayList<>();
            for (UserConfigV2 userConfigV2 : userConfigRepoV2.findByUserIdAndEnvIdAndChElmDetail_ChElementDtlIdIsNotNull(userId, envId)) {
                CHElementDetailV2 chElementDetailV2 = chElemDetailRepoV2.findByChElementDtlIdAndElementDtlHome(userConfigV2.getChElmDetail().getChElementDtlId(), "true");
                if (chElementDetailV2 != null) {
                    chElementDetailV2.setAssigned(true);
                    CHElementDetailVOV2 map = modelMapper.map(chElementDetailV2, CHElementDetailVOV2.class);
                    list.add(map);
                }
            }
            chElementDetailVOV2List = list;
        } else {
            log.error("Environment not found with envId -- {}", LogUtils.sanitizeForLog(envId));
        }
        return chElementDetailVOV2List;
    }

    //NEED TO DELETE
    public boolean isUserValidForUrlV2(UserAccessibilitySearchV2 userAccessibilitySearchV2) {
        boolean retVal = false;
        try {
            URL scrUrl = extractURL(userAccessibilitySearchV2.getUrl());
            if (scrUrl != null) {
                Optional<EnvironmentV2> environmentV2 = environmentRepoV2.findById(userAccessibilitySearchV2.getEnvId());
                if (environmentV2.isPresent()) {
                    retVal = !(userConfigRepoV2.findByUserIdAndEnvIdAndChElmDetail_ChElementDtlIdIsNotNull(userAccessibilitySearchV2.getUserId(), userAccessibilitySearchV2.getEnvId())
                            .stream()
                            .parallel()
                            .map(userConfigV2 -> chElemDetailRepoV2.findByChElementDtlIdAndElementDtlURLOrDemoUrlIsNotNull(userConfigV2.getChElmDetail().getChElementDtlId()))
                            .flatMap(Optional::stream)
                            .filter(chElementDetailV2 -> {
                                try {
                                    URI scrUri = scrUrl.toURI();
                                    URL elementDtlURL = (chElementDetailV2.getElementDtlURL() != null && !chElementDetailV2.getElementDtlURL().isBlank()) ? extractURL(chElementDetailV2.getElementDtlURL()) : null;
                                    URL demoUrl = (chElementDetailV2.getDemoUrl() != null && !chElementDetailV2.getDemoUrl().isBlank()) ? extractURL(chElementDetailV2.getDemoUrl()) : null;
                                    URI elementDtlUri = Objects.requireNonNull(elementDtlURL).toURI();
                                    URI demoUri = Objects.requireNonNull(demoUrl).toURI();
                                    return scrUri.equals(elementDtlUri)
                                            || scrUri.equals(demoUri);
                                } catch (MalformedURLException e) {
                                    log.error("Exception converting URL --> {}", LogUtils.sanitizeForLog(e.getMessage()));
                                    throw new DashboardException("Exception converting URL", e);
                                } catch (URISyntaxException e) {
                                    throw new DashboardConfigException(new DashboardConfigExceptionMessage("500", e.getMessage()));
                                }
                            })
                            .toList()
                            .isEmpty());
                } else {
                    log.error("Environment not found with envId -- {}", LogUtils.sanitizeForLog(userAccessibilitySearchV2.getEnvId()));
                }
            }
        } catch (MalformedURLException e) {
            log.error("Exception converting scrUrl URL --> {}",LogUtils.sanitizeForLog( e.getMessage()));
            throw new DashboardException("Exception converting scrUrl URL", e);
        }
        return retVal;
    }
}

