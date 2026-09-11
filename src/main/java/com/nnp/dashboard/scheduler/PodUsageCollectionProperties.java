package com.nnp.dashboard.scheduler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "scheduler.pod-usage-data.chunking")
public class PodUsageCollectionProperties {
    /** Enables namespace-scoped, paced collection. Disable only to use the legacy single query. */
    private boolean enabled = true;
    /** Explicit scope avoids an unbounded all-namespace discovery scan. */
    private List<String> namespaces = List.of("nnp-core-components");
    /** Maximum namespaces included in one SigNoz request/Kafka record. */
    private int namespaceBatchSize = 1;
    /** Minimum milliseconds between starts of two chunk queries. */
    private long delayBetweenChunksMs = 1_000;
    /** Window used to spread chunks; normally the one-minute scheduler interval. */
    private long collectionWindowMs = 60_000;
}
