package ru.innopolis.process_automation.tgbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "bot")
@Data
@Primary
@PropertySource("classpath:local.yaml")
public class BotConfig {
    private String name;
    private String token;

    @Bean
    public RestTemplate getRestTemplate() {
        RestTemplateBuilder builder = new RestTemplateBuilder();

        return builder.build();
    }
}
