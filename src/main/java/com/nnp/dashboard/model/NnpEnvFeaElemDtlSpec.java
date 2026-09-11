package com.nnp.dashboard.model;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Entity
@Table(name = "nnp_env_fea_elem_dtl_spec", schema = "portal")
@Data
public class NnpEnvFeaElemDtlSpec implements Serializable {
    @Id
    @Column(name = "dtlspec_id", nullable = false)
    private String dtlSpecId;

    // Add other fields as per your schema if needed
}
