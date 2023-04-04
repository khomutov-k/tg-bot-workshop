package ru.innopolis.process_automation.tgbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import ru.innopolis.process_automation.tgbot.config.BotConfig;

@SpringBootApplication
public class TgBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(TgBotApplication.class, args);
	}

}
