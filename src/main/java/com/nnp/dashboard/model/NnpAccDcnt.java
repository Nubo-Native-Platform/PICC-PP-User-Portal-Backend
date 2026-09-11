package com.nnp.dashboard.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nnp_acc_dcnt", schema = "portal")
@Getter
@Setter
public class NnpAccDcnt implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "acc_dcnt_id", nullable = false)
    private String accDcntId;

    @Column(name = "acc_dcnt_description")
    private String accDcntDescription;

    @Column(name = "acc_dcnt_end_ts")
    private LocalDateTime accDcntEndTs;

    @Column(name = "acc_dcnt_percent")
    private String accDcntPercent;

    @Column(name = "acc_dcnt_start_ts")
    private LocalDateTime accDcntStartTs;

    @Column(name = "api_id")
    private String apiId;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "created_on")
    private OffsetDateTime createdOn;

    @Column(name = "modified_on")
    private OffsetDateTime modifiedOn;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL,optional = false)
    @JoinColumn(name = "acc_id")
    private NnpAccount nnpAccount;  

   
}
