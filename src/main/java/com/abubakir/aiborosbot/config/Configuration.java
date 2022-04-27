package com.abubakir.aiborosbot.config;

import com.abubakir.aiborosbot.app.LavaPlayerAudioProvider;
import com.abubakir.aiborosbot.app.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {

  @Bean
  GatewayDiscordClient gatewayDiscordClient(@Value("${api.key}") String apiKey) {
    return DiscordClient.create(apiKey).login().block();
  }

  @Bean
  AudioPlayerManager audioPlayerManager() {
    return new DefaultAudioPlayerManager();
  }

  @Bean
  AudioPlayer audioPlayer(AudioPlayerManager audioPlayerManager) {
    audioPlayerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
    AudioSourceManagers.registerRemoteSources(audioPlayerManager);
    return audioPlayerManager.createPlayer();
  }

//  @Bean
//  LavaPlayerAudioProvider lavaPlayerAudioProvider(AudioPlayer audioPlayer) {
//    return new LavaPlayerAudioProvider(audioPlayer);
//  }

//  @Bean
//  TrackScheduler trackScheduler(AudioPlayer audioPlayer) {
//    return new TrackScheduler(audioPlayer);
//  }
}
