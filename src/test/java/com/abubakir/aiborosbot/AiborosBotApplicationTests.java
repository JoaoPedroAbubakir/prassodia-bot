package com.abubakir.aiborosbot;

import com.abubakir.aiborosbot.config.Configuration;
import discord4j.core.DiscordClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource("classpath:application.properties")
@EnableConfigurationProperties(value = Configuration.class)
class AiborosBotApplicationTests {

    @Autowired
    DiscordClient discordClient;
    @Test
    void contextLoads() {
        assertThat(discordClient).isNotNull();
    }

}
