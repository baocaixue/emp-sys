package com.eorionsolution.microservices.employeemonitor.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
public class BleMessage {

    private String v;
    private int mid;
    private int time;
    private String ip;
    private String mac;

    private String[] devices;

    private Set<DeviceMessage> deviceMessages = new HashSet<>();
    private LocalDateTime comingTime;

    public BleMessage putDeviceMsg(DeviceMessage deviceMessage) {
        this.deviceMessages.add(deviceMessage);
        return this;
    }
}
