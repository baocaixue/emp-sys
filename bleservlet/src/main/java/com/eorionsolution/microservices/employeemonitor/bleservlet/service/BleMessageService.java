package com.eorionsolution.microservices.employeemonitor.bleservlet.service;

import com.eorionsolution.microservices.employeemonitor.bleservlet.repository.DeviceMessageRepository;
import com.eorionsolution.microservices.employeemonitor.domain.BleMessage;
import com.eorionsolution.microservices.employeemonitor.domain.DeviceMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@Log4j2
@AllArgsConstructor
public class BleMessageService {
    private final ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
    private final DeviceMessageRepository deviceMessageRepository;

    public void saveBleMessageToMongo(InputStream inputStream) throws IOException {
        BleMessage message = objectMapper.readValue(inputStream, BleMessage.class);
        var bleMac = message.getMac();
        var set = Arrays.stream(message.getDevices())
                .map(device -> DeviceMessage.getInstance(device, bleMac))
                .collect(Collectors.toSet());
        log.debug(message);
        deviceMessageRepository.saveAll(set);
    }
}
