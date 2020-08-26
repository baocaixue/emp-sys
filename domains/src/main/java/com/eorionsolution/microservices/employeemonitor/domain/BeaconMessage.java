package com.eorionsolution.microservices.employeemonitor.domain;

import lombok.Data;
import org.apache.commons.codec.binary.Hex;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

@Data
public class BeaconMessage {
    private String uuid;
    private short major;
    private short minor;
    private byte rssi;

    public static BeaconMessage getInstance(byte[] data) {
        var result = new BeaconMessage();
        result.uuid = Hex.encodeHexString(Arrays.copyOfRange(data, 9, 25));
        result.major = fromTwoBytesToShort(data[25], data[26]);
        result.minor = fromTwoBytesToShort(data[27], data[28]);
        result.rssi = data[29];
        return result;
    }

    private static short fromTwoBytesToShort(byte b1, byte b2) {
        var buf = ByteBuffer.allocate(2);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.put(b1);
        buf.put(b2);
        return buf.getShort(0);
    }
}
