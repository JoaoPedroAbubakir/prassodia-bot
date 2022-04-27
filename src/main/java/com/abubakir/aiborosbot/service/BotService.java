package com.abubakir.aiborosbot.service;

import com.abubakir.aiborosbot.app.Command;
import com.abubakir.aiborosbot.app.LavaPlayerAudioProvider;
import com.abubakir.aiborosbot.app.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class BotService {

  private static Logger LOGGER = LoggerFactory.getLogger(BotService.class);

  GatewayDiscordClient gatewayDiscordClient;
  LavaPlayerAudioProvider lavaPlayerAudioProvider;

  TrackScheduler trackScheduler;

  AudioPlayerManager playerManager;

  private static final Map<String, Command> commands = new HashMap<>();

  @Autowired
  public BotService(
      GatewayDiscordClient gatewayDiscordClient,
      LavaPlayerAudioProvider lavaPlayerAudioProvider,
      TrackScheduler trackScheduler,
      AudioPlayerManager playerManager) {
    this.gatewayDiscordClient = gatewayDiscordClient;
    this.lavaPlayerAudioProvider = lavaPlayerAudioProvider;
    this.trackScheduler = trackScheduler;
    LOGGER.info("Created BotService");

    LOGGER.info("Registering Commands");
    commands.put(
        "ping",
        event ->
            event
                .getMessage()
                .getChannel()
                .flatMap(channel -> channel.createMessage("Pong!"))
                .then());
    commands.put(
        "join",
        event ->
            Mono.justOrEmpty(event.getMember())
                .flatMap(Member::getVoiceState)
                .flatMap(VoiceState::getChannel)
                // join returns a VoiceConnection which would be required if we were
                // adding disconnection features, but for now we are just ignoring it.
                .flatMap(VoiceChannel::join)
                .then());
    commands.put(
        "play",
        event ->
            Mono.justOrEmpty(event.getMessage().getContent())
                .map(content -> Arrays.asList(content.split(" ")))
                .doOnNext(command -> playerManager.loadItem(command.get(1), trackScheduler))
                .then());
    LOGGER.info("Registered Commands");

    gatewayDiscordClient
        .getEventDispatcher()
        .on(MessageCreateEvent.class)
        // 3.1 Message.getContent() is a String
        .flatMap(
            event ->
                Mono.just(event.getMessage().getContent())
                    .flatMap(
                        content ->
                            Flux.fromIterable(commands.entrySet())
                                // We will be using ! as our "prefix" to any command in the system.
                                .filter(entry -> content.startsWith('!' + entry.getKey()))
                                .flatMap(entry -> entry.getValue().execute(event))
                                .next()))
        .subscribe();

    this.gatewayDiscordClient.onDisconnect().block(); // kill on disconnect
  }
}
