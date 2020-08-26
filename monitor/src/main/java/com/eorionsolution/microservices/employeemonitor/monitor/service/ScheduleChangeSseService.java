package com.eorionsolution.microservices.employeemonitor.monitor.service;

import com.eorionsolution.microservices.employeemonitor.monitor.domain.ExternalRefreshRequestBody;
import com.eorionsolution.microservices.employeemonitor.monitor.domain.config.ExternalRefreshProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.*;

@Service
@Log4j2
public class ScheduleChangeSseService {
    private final FluxProcessor<String, String> processor;
    private final FluxSink<String> sink;

    private final ExternalRefreshProperties properties;

    public ScheduleChangeSseService(final ExternalRefreshProperties properties) {
        this.processor = DirectProcessor.<String>create().serialize();
        this.sink = processor.sink();
        this.properties = properties;
    }

    private WebClient generateWebClient() {
        return WebClient.builder()
                .baseUrl(properties.getUri())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<Void> sendEvent(String msg) {
       if (!properties.isEnable())
           return Mono.just(sink.next(msg)).then();
       else {
            return generateWebClient().post()
                    .body(BodyInserters.fromValue(ExternalRefreshRequestBody.fromExternalRefreshProperties(properties)))
                    .exchange()
                    .doOnNext(clientResponse -> log.info("status:{}", clientResponse.statusCode()))
                    .then();
       }
    }

    public Flux<ServerSentEvent<String>> getEvent() {
        return processor.map(e -> ServerSentEvent.builder(e).build());
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void sendNewDateEvent() {
        this.sendEvent("new-date");
    }
}
