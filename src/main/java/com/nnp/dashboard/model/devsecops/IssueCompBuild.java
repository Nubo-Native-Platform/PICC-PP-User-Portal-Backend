package com.nnp.dashboard.model.devsecops;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "issue_comp_build", schema = "devopscollector")
@Getter
@Setter
public class IssueCompBuild implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "namespace_path", nullable = false)
    private String namespacePath;

    @Column(name = "started_at")
    private LocalDate startedAt;

    @Column(name = "finished_at")
    private LocalDate finishedAt;

    @Column(name = "status")
    private String status;

}
