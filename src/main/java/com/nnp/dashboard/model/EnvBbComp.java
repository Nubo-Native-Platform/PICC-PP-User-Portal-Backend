package com.nnp.dashboard.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "env_bbcomp", schema = "portal")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnvBbComp implements Serializable {

    @Id
    @Column(name = "env_compid", nullable = false)
    private String envCompId;

    @Column(name = "env_compname", nullable = false)
    private String envCompName;

    @Column(name = "env_compdesc", nullable = false, columnDefinition = "TEXT")
    private String envCompDesc;

    @Column(name = "env_compstatus", nullable = false, columnDefinition = "TEXT")
    private String envCompStatus;

    @Column(name = "env_comp_type")
    private String envCompType;

    @Column(name = "item_seq")
    private Integer itemSeq;

    @Column(name = "env_platform")
    private String envPlatform = "Gen";

    @Column(name = "env_git_path")
    private String envGitPath;

    @Column(name = "env_git_accesstoken")
    private String envGitAccessToken;

    @Column(name = "shared_comp_serv_url", columnDefinition = "TEXT")
    private String sharedCompServUrl;

    @OneToMany(mappedBy = "envBbComp", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EnvCompSpec> envCompSpecs = new ArrayList<>();

}