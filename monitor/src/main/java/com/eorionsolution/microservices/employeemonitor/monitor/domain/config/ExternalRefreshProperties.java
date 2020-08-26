package com.eorionsolution.microservices.employeemonitor.monitor.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external.refresh")
@Data
public class ExternalRefreshProperties {
    private boolean enable = false;

    private String uri;
    private int channel;
    private int projectType;
    private String uniquenessSign;
    private String[] params;

}
