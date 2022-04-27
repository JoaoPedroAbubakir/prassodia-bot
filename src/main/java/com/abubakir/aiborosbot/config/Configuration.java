package com.abubakir.aiborosbot.config;

import com.abubakir.aiborosbot.app.EventListener;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.List;

@org.springframework.context.annotation.Configuration
public class Configuration {
  @Bean
  public <T extends Event> GatewayDiscordClient gatewayDiscordClient(
      @Value("${api.key}") String apiKey, List<EventListener<T>> eventListeners) {
    GatewayDiscordClient client = DiscordClientBuilder.create(apiKey).build().login().block();
    for (EventListener<T> listener : eventListeners) {
      client
          .on(listener.getEventType())
          .flatMap(listener::execute)
          .onErrorResume(listener::handleError)
          .subscribe();
    }
    return client;
  }
}
