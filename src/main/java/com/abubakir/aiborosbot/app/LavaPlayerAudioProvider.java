package com.abubakir.aiborosbot.app;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import discord4j.voice.AudioProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

@Service
public class LavaPlayerAudioProvider extends AudioProvider {

  AudioPlayer audioPlayer;
  private final MutableAudioFrame frame = new MutableAudioFrame();

  @Autowired
  public LavaPlayerAudioProvider(AudioPlayer audioPlayer) {
    // for Discord
    super(ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize()));
    // Set LavaPlayer's MutableAudioFrame to use the same buffer as the one we
    // just allocated
    frame.setBuffer(getBuffer());
    this.audioPlayer = audioPlayer;
  }

  @Override
  public boolean provide() {
    // AudioPlayer writes audio data to its AudioFrame
    final boolean didProvide = audioPlayer.provide(frame);
    // If audio was provided, flip from write-mode to read-mode
    if (didProvide) {
      getBuffer().flip();
    }
    return didProvide;
  }
}
