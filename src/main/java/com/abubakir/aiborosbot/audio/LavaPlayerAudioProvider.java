package com.abubakir.aiborosbot.audio;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import discord4j.voice.AudioProvider;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LavaPlayerAudioProvider extends AudioProvider {

    AudioPlayer audioPlayer;
    MutableAudioFrame frame = new MutableAudioFrame();
    @Builder
    public LavaPlayerAudioProvider(AudioPlayer audioPlayer) {
        super(
                ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize())
        );
        frame.setBuffer(getBuffer());
        this.audioPlayer = audioPlayer;
    }

    @Override
    public boolean provide() {
        final boolean didProvide = audioPlayer.provide(frame);
        if (didProvide) {
            getBuffer().flip();
        }
        return didProvide;
    }
}
