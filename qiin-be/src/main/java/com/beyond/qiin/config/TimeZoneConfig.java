package com.beyond.qiin.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeZoneConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.timeZone(TimeZone.getTimeZone("Asia/Seoul"));

            builder.simpleDateFormat("yyyy-MM-dd HH:mm:ss");

            JavaTimeModule module = new JavaTimeModule();
            module.addSerializer(Instant.class, new JsonSerializer<Instant>() {
                @Override
                public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers)
                        throws IOException {
                    gen.writeString(value.atZone(ZoneId.of("Asia/Seoul"))
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
            });

            builder.modules(module);
        };
    }
}
