package com.eorionsolution.microservices.employeemonitor.monitor.domain;

import com.eorionsolution.microservices.employeemonitor.monitor.domain.config.ExternalRefreshProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalRefreshRequestBody {
    private int channel;
    private int projectType;
    private String uniquenessSign;
    private String[] params;

    public static ExternalRefreshRequestBody fromExternalRefreshProperties(ExternalRefreshProperties properties) {
        return new ExternalRefreshRequestBody(properties.getChannel(), properties.getProjectType(),
                properties.getUniquenessSign(), properties.getParams());
    }
}
