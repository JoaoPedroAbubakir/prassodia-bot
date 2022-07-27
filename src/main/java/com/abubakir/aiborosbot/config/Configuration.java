package com.abubakir.aiborosbot.config;

import com.abubakir.aiborosbot.audio.LavaPlayerAudioProvider;
import com.abubakir.aiborosbot.audio.TrackScheduler;
import com.abubakir.aiborosbot.bot.BotCore;
import com.abubakir.aiborosbot.commands.Command;
import com.abubakir.aiborosbot.commands.Join;
import com.abubakir.aiborosbot.commands.Play;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.voice.AudioProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@org.springframework.context.annotation.Configuration
@ConfigurationProperties(prefix = "bot")
public class Configuration {

    @Bean
    @DependsOn("discordClient")
    public BotCore botCore(Mono<GatewayDiscordClient> discordClient, List<Command> commands) {
        return new BotCore(discordClient, commands);
    }

    @Bean("commands")
    public List<Command> commands(LavaPlayerAudioProvider lavaPlayerAudioProvider, TrackScheduler trackScheduler, AudioPlayerManager audioPlayerManager) {
        List<Command> commands = new ArrayList<>();
        commands.add(new Join("!join", lavaPlayerAudioProvider));
        commands.add(new Play("!play",trackScheduler, audioPlayerManager));
        return commands;
    }

    @Bean("discordClient")
    @DependsOn("lavaPlayerAudioProvider")
    public Mono<GatewayDiscordClient> discordClient (@Value("${api.key}") String apiKey) {
        return DiscordClient.create(Objects.requireNonNull(apiKey)).login();
    }
    @Bean("audioPlayerManager")
    public AudioPlayerManager audioPlayerManager() {
        AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
        audioPlayerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        return audioPlayerManager;
    }

    @Bean("audioPlayer")
    @DependsOn("audioPlayerManager")
    public AudioPlayer audioPlayer(AudioPlayerManager audioPlayerManager) {
        return audioPlayerManager.createPlayer();
    }

    @Bean("lavaPlayerAudioProvider")
    @DependsOn("audioPlayer")
    public LavaPlayerAudioProvider lavaPlayerAudioProvider(AudioPlayer audioPlayer) {
        return new LavaPlayerAudioProvider(audioPlayer);
    }

    @Bean("trackScheduler")
    @DependsOn("audioPlayer")
    public TrackScheduler trackScheduler(AudioPlayer audioPlayer) {
        return new TrackScheduler(audioPlayer);
    }







}
