package com.abubakir.aiborosbot;

import com.abubakir.aiborosbot.config.BeansConfiguration;
import net.dv8tion.jda.api.JDA;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource("classpath:application.properties")
class AiborosBotApplicationTests {

    @Autowired
    JDA bot;

    @Test
    void contextLoads() {
        assertThat(bot).isNotNull();
    }

}
