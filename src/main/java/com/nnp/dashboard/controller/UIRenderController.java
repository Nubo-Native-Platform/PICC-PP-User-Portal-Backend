package com.nnp.dashboard.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.nnp.dashboard.vo.*;
import com.nnp.dashboard.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nnp.dashboard.model.CHElementDetail;
import com.nnp.dashboard.model.ElementDetail;
import com.nnp.dashboard.model.EnvFeature;
import com.nnp.dashboard.model.Environment;
import com.nnp.dashboard.model.FeatureElement;
import com.nnp.dashboard.service.UIRenderServ;

import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller responsible for rendering UI navigation hierarchies and validating user URL accessibility.
 * Exposes endpoints to retrieve environment trees, features, elements, child element specs, and home links (V1 & V2/V3 APIs).
 */
@RestController
@Slf4j
public class UIRenderController {

    @Autowired
    private UIRenderServ uiRenderServ;

    /**
     * Retrieves environment model structure by environment code.
     *
     * @param envCode unique code identifier of the environment
     * @return Environment entity
     */
    @GetMapping(path = "/env/{envCode}")
    public Environment getEnvironment(@PathVariable String envCode) {
//        log.info("UIRenderController getEnvironment() - /env/{envCode} " + envCode);
        return uiRenderServ.getEnvByCode(envCode);
    }

    /**
     * Retrieves Environment V3 VO structure by environment ID.
     *
     * @param envId unique environment ID
     * @return EnvironmentVOV3 object
     */
    @GetMapping(path = "/env/read/v3/{envId}")
    public EnvironmentVOV3 retriveEnvironmentV3ByEnvId(@PathVariable("envId") String envId) {
//        log.info("UIRenderController ->retriveEnvironmentV3ByEnvId()");
        return uiRenderServ.getEnvironmentByEnvIdV3(envId);
    }

    /**
     * Retrieves all available environments (V1 VO).
     *
     * @return list of EnvironmentVO objects
     */
    @GetMapping(path = "/env/read")
    public List<EnvironmentVO> retriveEnvironment() {
//        log.info("UIRenderController retriveEnvironment() - /env/read ");
        return uiRenderServ.getAllEnvironments();
    }

    /**
     * Retrieves all available environments in V3 format (used in Kubernetes Integration).
     *
     * @return list of EnvironmentVOV3 objects
     */
    @GetMapping(path = "/env/read/v3")
    public List<EnvironmentVOV3> retriveEnvironmentV3() {
//        log.info("UIRenderController ->retriveEnvironmentV3()");
        return uiRenderServ.getAllEnvironmentsV3();
    }

    /**
     * Retrieves features for an environment code and marks assignment status for a user.
     *
     * @param envCode environment code
     * @param userId user ID
     * @return list of EnvFeature objects sorted by sequence
     */
    @GetMapping(path = "/feature/{envCode}/{userId}")
    public List<EnvFeature> getFeatureByEnvAndUser(@PathVariable String envCode, @PathVariable String userId) {
//        log.info("UIRenderController getFeatureByEnvAndUser() - /feature/{envCode}/{userId} " + envCode + " " + "userId");
        Environment env = uiRenderServ.getEnvByCode(envCode);
        env.getEnvFeatures().forEach(envFea -> {
            uiRenderServ.getAllFeatureByEnvAndUser(envCode, userId).forEach(ufea -> {
                if (envFea.getFeaId().equalsIgnoreCase(ufea.getFeaId())) {
                    envFea.setAssigned(true);
                }
            });


        });
        env.getEnvFeatures().sort((EnvFeature f1, EnvFeature f2) -> f1.getFeaSeq().compareTo(f2.getFeaSeq()));
        return env.getEnvFeatures();
    }

    /**
     * Retrieves features for an environment ID in V2 format sorted by sequence.
     *
     * @param envId environment ID
     * @return list of EnvFeatureVOV2 objects
     */
    @GetMapping(path = "/feature/v2/{envId}")
    public List<EnvFeatureVOV2> getFeaturesByEnvV2(@PathVariable("envId") String envId) {

        ArrayList<EnvFeatureVOV2> featuresByEnvV2 = new ArrayList<>(uiRenderServ.getFeaturesByEnvIdV2(envId));
        featuresByEnvV2.sort(Comparator.comparing(EnvFeatureVOV2::getFeaSeq));
        return featuresByEnvV2;
    }

    /**
     * Retrieves feature elements for a given feature ID and user ID with assignment status.
     *
     * @param feaId feature ID
     * @param userId user ID
     * @return list of FeatureElement objects
     */
    @GetMapping(path = "/feaelem/{feaId}/{userId}")
    public List<FeatureElement> getFeaElemByFeatureAndUser(@PathVariable String feaId, @PathVariable String userId) {
//        log.info("UIRenderController getFeaElemByFeatureAndUser() - /feaelem/{feaId}/{userId} " + feaId + " " + "userId");
        EnvFeature fea = uiRenderServ.getFeatureById(feaId);
        fea.getFeatureElements().forEach(feaElem -> {
            uiRenderServ.getAllFeaElmByFeaAndUser(feaId, userId).forEach(ufeaElem -> {
                if (feaElem.getElementId().equalsIgnoreCase(ufeaElem.getElementId())) {
                    feaElem.setAssigned(true);
                }
            });


        });
        fea.getFeatureElements().sort((FeatureElement f1, FeatureElement f2) -> f1.getFeaSeq().compareTo(f2.getFeaSeq()));
        return fea.getFeatureElements();
    }

    /**
     * Retrieves feature elements by feature ID in V2 format sorted by sequence.
     *
     * @param feaId feature ID
     * @return list of FeatureElementVOV2 objects
     */
    @GetMapping(path = "/feaelem/v2/{feaId}")
    public List<FeatureElementVOV2> getFeaElemByFeatureIdV2(@PathVariable("feaId") String feaId) {

        ArrayList<FeatureElementVOV2> featureElementVOV2s = new ArrayList<>(uiRenderServ.getFeaElemByFeatureIdV2(feaId));
        featureElementVOV2s.sort(Comparator.comparing(FeatureElementVOV2::getFeaSeq));
        return featureElementVOV2s;
    }

    /**
     * Retrieves element details for a feature element and user with assignment status.
     *
     * @param feaElemId feature element ID
     * @param userId user ID
     * @return list of ElementDetail objects
     */
    @GetMapping(path = "/element/{feaElemId}/{userId}")
    public List<ElementDetail> getElemDtlByFeatureElemAndUser(@PathVariable String feaElemId, @PathVariable String userId) {
//        log.info("UIRenderController getElemDtlByFeatureElemAndUser() - /element/{feaElemId}/{userId} " + feaElemId + " " + "userId");
        FeatureElement feaElem = uiRenderServ.getFeaElemByID(feaElemId);
        feaElem.getElementDetails().forEach(elemDtl -> {
            uiRenderServ.getAllElemDtlByFeaDtlAndUser(feaElemId, userId).forEach(uElemDtl -> {
                if (elemDtl.getElementDtlId().equalsIgnoreCase(uElemDtl.getElementDtlId())) {
                    elemDtl.setAssigned(true);
                }
            });


        });

        return feaElem.getElementDetails();
    }

    /**
     * Retrieves element details by feature element ID in V2 format.
     *
     * @param feaElemId feature element ID
     * @return list of ElementDetailVOV2 objects
     */
    @GetMapping(path = "/elementdtl/v2/{feaElemId}")
    public List<ElementDetailVOV2> getElemDtlsByFeatureElemIdV2(@PathVariable("feaElemId") String feaElemId) {
        return uiRenderServ.getElemDtlsByFeatureElemIdV2(feaElemId);
    }

    /**
     * Retrieves child element specifications for an element detail ID and user ID with assignment status.
     *
     * @param elemId element detail ID
     * @param userId user ID
     * @return list of CHElementDetail objects
     */
    @GetMapping(path = "/chelement/{elemId}/{userId}")
    public List<CHElementDetail> getChildElemByElemDetailAndUser(@PathVariable String elemId, @PathVariable String userId) {
//        log.info("UIRenderController getChildElemByElemDetailAndUser() - /chelement/{elemId}/{userId} " + elemId + " " + "userId");
        ElementDetail elemDtl = uiRenderServ.getElemDetailByID(elemId);
        elemDtl.getChildElementDtls().forEach(chElem -> {
            uiRenderServ.getAllChElemDtlByElmDtlAndUser(elemId, userId).forEach(uChElemDtl -> {
                if (chElem.getChElementDtlId().equalsIgnoreCase(uChElemDtl.getChElementDtlId())) {
                    chElem.setAssigned(true);
                }
            });


        });

        return elemDtl.getChildElementDtls();
    }

    /**
     * Retrieves child element specifications by element detail ID and user ID in V2 format.
     *
     * @param elemDtlId element detail ID
     * @param userId user ID
     * @return list of CHElementDetailVOV2 objects
     */
    @GetMapping(path = "/chelementdtl/v2/{elemDtlId}/{userId}")
    public List<CHElementDetailVOV2> getChildElemDtlByElemDetailIdAndUserIdV2(
            @PathVariable("elemDtlId") String elemDtlId,
            @PathVariable("userId") String userId
    ) {
        return uiRenderServ.getChildElemDtlByElemDetailIdAndUserIdV2(elemDtlId, userId);
    }

    /**
     * Retrieves home navigation links for an environment code and user ID.
     *
     * @param envCode environment code
     * @param userId user ID
     * @return list of CHElementDetail objects flagged for home display
     */
    @GetMapping(path = "/home/{envCode}/{userId}")
    public List<CHElementDetail> getHomeByEnvCode(@PathVariable String envCode, @PathVariable String userId) {
//        log.info("UIRenderController getChildElemByElemDetailAndUser() - /home/{envCode}/{userId} " + envCode + " " + "userId");
        return uiRenderServ.getHomeByEnvCode(envCode, userId);
    }

    /**
     * Retrieves home navigation links for an environment code and user ID.
     *
     * @param envCode environment code
     * @param userId user ID
     * @return list of CHElementDetail objects
     */
    @GetMapping(path = "/homelink/{envCode}/{userId}")
    public List<CHElementDetail> getHomeByEnvCodeUserId(@PathVariable String envCode, @PathVariable String userId) {
//        log.info("UIRenderController getHomeByEnvCodeUserId() - /homelink/{envCode}/{userId} " + envCode + " " + "userId");
        return uiRenderServ.getHomeByEnvCodeUserId(envCode, userId);
    }

    /**
     * Retrieves home navigation links for environment ID and user ID in V2 format.
     *
     * @param envId environment ID
     * @param userId user ID
     * @return list of CHElementDetailVOV2 objects
     */
    @GetMapping(path = "/homelink/v2/{envId}/{userId}")
    public List<CHElementDetailVOV2> getHomelinkByEnvIdAndUserIdV2(@PathVariable("envId") String envId, @PathVariable("userId") String userId) {
//        log.info("UIRenderController getHomelinkByEnvIdAndUserIdV2() - /homelink/v2/{envId}/{userId} " + envId + " " + userId);
        return uiRenderServ.getHomelinkByEnvIdAndUserIdV2(envId, userId);
    }

    /**
     * Validates if a user is authorized to access a given URL in an environment, utilizing Redis caching.
     *
     * @param sc UserAccessibilitySearch payload containing userId, envCode, and URL
     * @return map with "allowed" boolean status
     */
    @PostMapping(path = "/urlAccessibility")
    public Map<String, Boolean> isUserValidForUrl(@RequestBody UserAccessibilitySearch sc) {
//        log.info("UIRenderController isUserValidForUrl() - input:: " + sc);
        if(Objects.isNull(sc)||(Objects.isNull(sc.getEnvCode())||sc.getEnvCode().isBlank())||(Objects.isNull(sc.getUserId())||sc.getUserId().isBlank())||(Objects.isNull(sc.getUrl())||sc.getUrl().isBlank())){
        	return Collections.singletonMap("allowed", false);
        }
        return Collections.singletonMap("allowed", uiRenderServ.isUserValidForUrl(sc));
    }

    /**
     * Validates if a user is authorized to access a given URL in V2 model format.
     *
     * @param userAccessibilitySearchV2 search criteria payload
     * @return true if authorized, false otherwise
     */
    @GetMapping(path = "/urlAccessibility/v2")
    public boolean isUserValidForUrlV2(@RequestBody UserAccessibilitySearchV2 userAccessibilitySearchV2) {
//        log.info("UIRenderController isUserValidForUrlV2() - input --> {}", userAccessibilitySearchV2);
        return uiRenderServ.isUserValidForUrlV2(userAccessibilitySearchV2);
    }

}
