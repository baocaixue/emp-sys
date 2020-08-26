package com.eorionsolution.microservices.employeemonitor.monitor.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.compress.utils.IOUtils;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Base64;

@Document
@Data
@Accessors(chain = true)
public class EmployeeAvatar {
    @Id
    private String id;

    private Binary avatar;

    @Transient
    private FilePart inputAvatar;

    @Transient
    private String base64EncodedAvatar;

    public Mono<EmployeeAvatar> parseInputAvatar() {
        return inputAvatar.content()
                .<InputStream>reduce(new InputStream() {public int read() { return -1; }},
                        (s, d) -> new SequenceInputStream(s, d.asInputStream())
                )
                .flatMap(inputStream -> {
                    try {
                        return Mono.just(this.setAvatar(
                                new Binary(BsonBinarySubType.BINARY, IOUtils.toByteArray(inputStream))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Mono.just(this);
                });
    }

    public EmployeeAvatar parseBinaryAvatarToBase64Encode() {
        if (this.avatar == null) {
            return this;
        }
        return this.setBase64EncodedAvatar(Base64.getEncoder().encodeToString(this.getAvatar().getData()));
    }
}
