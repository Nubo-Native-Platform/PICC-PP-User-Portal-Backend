package com.nnp.dashboard.model.apiecosystem;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "api_transaction", schema = "apiecosystem")
@Getter
@Setter
public class ApiTransaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "api_transid", nullable = false)
    private Long id;

    @Column(name = "apiid", nullable = false)
    private String apiId;

    @Column(name = "env_code", nullable = false)
    private String envCode;

    @Column(name = "call_receive_ts")
    private LocalDate callReceiveTs;

    @Column(name = "call_return_ts")
    private LocalDate callReturnTs;

    @Column(name = "req_time")
    private Long reqTime;

    @Column(name = "resp_time")
    private Long respTime;

    @Column(name = "status")
    private Boolean status;

}
