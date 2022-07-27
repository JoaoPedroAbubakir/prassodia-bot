package com.abubakir.aiborosbot.config;

import discord4j.core.DiscordClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Objects;

@org.springframework.context.annotation.Configuration
@ConfigurationProperties(prefix = "bot")
public class Configuration {

    @Bean
    public DiscordClient discordClient (@Value("${api.key}") String apiKey) {
        return DiscordClient.create(Objects.requireNonNull(apiKey));
    }

}
