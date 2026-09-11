package com.nnp.dashboard.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nnp.dashboard.model.EnvBbComp;
import com.nnp.dashboard.model.EnvProxyConfig;
import com.nnp.dashboard.model.EnvironmentV4;
import com.nnp.dashboard.model.NnpEnvFeaElemDtlSpec;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnvProxyConfigVO {
    private String envConfigId;
    private String envId;
    private String compSrvName;
    private String domainName;
    private String parentFrontend;
    private String subpath;
    private String lineIndex;
    private Integer internalPort;

    // Convert this VO to Entity
    public EnvProxyConfig toEntity() {
        EnvProxyConfig entity = new EnvProxyConfig();
        entity.setEnvConfigId(this.envConfigId);
        if (this.envId != null) {
            EnvironmentV4 env = new EnvironmentV4();
            env.setEnvId(this.envId);
            entity.setEnvironment(env);
        }
        entity.setCompSrvName(this.compSrvName);
        entity.setDomainName(this.domainName);
        entity.setParentFrontend(this.parentFrontend);
        entity.setSubpath(this.subpath);
        entity.setLineIndex(this.lineIndex);
        entity.setInternalPort(this.internalPort);
        return entity;
    }

    // Convert Entity to VO
    public static EnvProxyConfigVO toVo(EnvProxyConfig entity) {
        EnvProxyConfigVO vo = new EnvProxyConfigVO();
        vo.setEnvConfigId(entity.getEnvConfigId());
        vo.setEnvId(entity.getEnvironment() != null ? entity.getEnvironment().getEnvId() : null);
        vo.setCompSrvName(entity.getCompSrvName());
        vo.setDomainName(entity.getDomainName());
        vo.setParentFrontend(entity.getParentFrontend());
        vo.setSubpath(entity.getSubpath());
        vo.setLineIndex(entity.getLineIndex());
        vo.setInternalPort(entity.getInternalPort());
        return vo;
    }
}
