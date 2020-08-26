package com.eorionsolution.microservices.employeemonitor.monitor.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RefreshDuration {
    @Id
    private String id = "1";

    private int duration;

    private String macAddr1;

    private String macAddr2;

    private int pwdDuration;
}
