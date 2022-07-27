package com.abubakir.aiborosbot.config;

import com.abubakir.aiborosbot.audio.LavaPlayerAudioProvider;
import com.abubakir.aiborosbot.manager.BotManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.core.DiscordClient;
import discord4j.voice.AudioProvider;
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

    @Bean
    public AudioPlayer audioPlayerManager() {
        DefaultAudioPlayerManager defaultAudioPlayerManager = new DefaultAudioPlayerManager();
        defaultAudioPlayerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(defaultAudioPlayerManager);
        return defaultAudioPlayerManager.createPlayer();
    }

    @Bean
    public AudioProvider audioProvider(AudioPlayer audioPlayer) {
        return new LavaPlayerAudioProvider(audioPlayer);
    }

    @Bean
    public BotManager botManager(DiscordClient discordClient, AudioProvider audioProvider) {
        return new BotManager(discordClient, audioProvider);
    }

}
