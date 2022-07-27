package com.abubakir.aiborosbot.bot;

import com.abubakir.aiborosbot.commands.Command;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.List;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BotCore {

    Mono<GatewayDiscordClient> discordClient;
    @Autowired
    List<Command> commands;

    @Builder
    public BotCore(Mono<GatewayDiscordClient> discordClient, List<Command> commands) {
        this.discordClient = discordClient;
        this.commands = commands;
        discordClient.subscribe(this::handle);
    }

    private void handle(GatewayDiscordClient gatewayDiscordClient) {
        gatewayDiscordClient.getEventDispatcher().on(MessageCreateEvent.class, messageCreateEvent -> {
            commands.stream().filter(command ->
                    command.getName().equals(messageCreateEvent.getMessage().getContent()))
                    .findFirst().ifPresent(command -> command.execute(messageCreateEvent));
            return Mono.empty();
        });
    }

}
