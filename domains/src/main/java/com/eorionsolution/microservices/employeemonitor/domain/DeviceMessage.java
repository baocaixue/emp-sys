package com.eorionsolution.microservices.employeemonitor.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.codec.binary.Hex;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

@Data
@Document(value = "message")
@EqualsAndHashCode(of = {"type", "mac", "bleMac"})
@ToString
public class DeviceMessage {
    @Id
    private String id;

    private byte type;
    private String mac;
    private byte rssi;
    private BeaconMessage beaconMessage;

    private LocalDateTime comingTime;
    private String bleMac;

    public static DeviceMessage getInstance(String base64EncodedMsg, String bleMac) {
        byte[] data = Base64.getDecoder().decode(base64EncodedMsg);
        var result = new DeviceMessage();
        result.id = UUID.randomUUID().toString();
        result.comingTime = LocalDateTime.now();
        result.type = data[0];
        result.mac = Hex.encodeHexString(Arrays.copyOfRange(data, 1, 7)).toUpperCase();
        result.rssi = data[7];
        result.beaconMessage = BeaconMessage.getInstance(Arrays.copyOfRange(data, 8, data.length));
        result.bleMac = bleMac;
        return result;
    }
}
