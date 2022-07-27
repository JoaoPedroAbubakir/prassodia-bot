package com.abubakir.aiborosbot.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Message;
import discord4j.voice.AudioProvider;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static java.rmi.server.LogStream.log;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BotManager {

    DiscordClient client;
    AudioProvider audioProvider;

    @Builder
    public BotManager(DiscordClient client, AudioProvider audioProvider) {
        this.client = client;
        this.audioProvider = audioProvider;
        start();
    }

    public void start() {
        log.info("start was called");
        client.withGateway(client -> {
                    client.getEventDispatcher().on(ReadyEvent.class)
                            .subscribe(ready -> log.info(String.format("Logged in as %s", ready.getSelf().getUsername())));

                    client.getEventDispatcher().on(MessageCreateEvent.class)
                            .map(MessageCreateEvent::getMessage)
                            .filter(msg -> msg.getContent().equals("!ping"))
                            .flatMap(Message::getChannel)
                            .flatMap(channel -> channel.createMessage("Pong!"))
                            .subscribe();



                    client.getEventDispatcher().on(Event.class)
                            .subscribe(event -> log.info(String.format("Event type: %s", event.getClass().getName())));

                    return client.onDisconnect();
                })
                .block();
    }




}
