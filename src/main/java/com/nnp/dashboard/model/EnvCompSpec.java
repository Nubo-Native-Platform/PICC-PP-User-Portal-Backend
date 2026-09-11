package com.nnp.dashboard.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "env_compspec", schema = "portal")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnvCompSpec implements Serializable {

    @Id
    @Column(name = "env_specid", nullable = false)
    private String envSpecId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "env_compid", nullable = false)
    private EnvBbComp envBbComp;

    @Column(name = "env_specname", nullable = false)
    private String envSpecName;

    @Column(name = "env_spectype", nullable = false)
    private String envSpecType;

    @Column(name = "env_specvalidation", nullable = false, columnDefinition = "TEXT")
    private String envSpecValidation;

    @Column(name = "env_specdesc", nullable = false, columnDefinition = "TEXT")
    private String envSpecDesc;

    @Column(name = "env_specvalues", columnDefinition = "TEXT")
    private String envSpecValues;

    @Column(name = "env_compstatus", nullable = false)
    private String envCompStatus;

    @Column(name = "env_spec_var_name")
    private String envSpecVarName;

    @Column(name = "is_editable", nullable = false)
    private Boolean isEditable = true;
}

