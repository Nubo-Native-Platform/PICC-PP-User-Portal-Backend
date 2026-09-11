package com.nnp.dashboard.config.redmine;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "redmine")
public class RedmineConfigProps {

    private String baseUrl;
    private String apiKey;
    private String baseProjectId;

}
