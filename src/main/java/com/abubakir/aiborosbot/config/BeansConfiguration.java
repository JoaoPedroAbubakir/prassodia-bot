package com.abubakir.aiborosbot.config;

import com.abubakir.aiborosbot.listeners.ReadyListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;

@Configuration
public class BeansConfiguration {

    @Value("${api.key}")
    private String botToken;

    @Bean
    public JDA bot() throws LoginException, InterruptedException {
        return JDABuilder.createDefault(botToken)
                .addEventListeners(readyListener())
                .build().awaitReady();
    }

    @Bean
    public ReadyListener readyListener() {
        return new ReadyListener();
    }






}
