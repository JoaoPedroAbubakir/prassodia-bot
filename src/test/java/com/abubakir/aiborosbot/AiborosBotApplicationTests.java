package com.abubakir.aiborosbot;

import com.abubakir.aiborosbot.config.Configuration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("classpath:application.properties")
@EnableConfigurationProperties(value = Configuration.class)
class AiborosBotApplicationTests {

    @Test
    void contextLoads() {

    }

}
