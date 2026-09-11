package com.nnp.dashboard.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import lombok.Data;

@Entity
@Table(name = "env_proxy_config", schema = "portal")
@Data
public class EnvProxyConfig {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "env_config_id", updatable = false, nullable = false)
    private String envConfigId;

    @ManyToOne(fetch = FetchType.LAZY, optional =false)
    @JoinColumn(name = "env_id", referencedColumnName = "env_id")
    private EnvironmentV4 environment;

    @Column(name = "comp_srv_name")
    private String compSrvName;

    @Column(name = "domain_name")
    private String domainName;

    @Column(name = "parent_frontend")
    private String parentFrontend;

    @Column(name = "subpath")
    private String subpath;

    @Column(name = "line_index")
    private String lineIndex;

    @Column(name = "internal_port")
    private Integer internalPort;
}
