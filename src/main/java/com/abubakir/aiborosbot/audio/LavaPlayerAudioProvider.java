package com.abubakir.aiborosbot.audio;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import discord4j.voice.AudioProvider;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

import java.nio.ByteBuffer;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LavaPlayerAudioProvider extends AudioProvider {

    AudioPlayer player;
    MutableAudioFrame frame = new MutableAudioFrame();

    @Builder
    public LavaPlayerAudioProvider(AudioPlayer player){
        super(ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize()));
        frame.setBuffer(getBuffer());
        this.player = player;
    }

    @Override
    public boolean provide() {
        final boolean didProvide = player.provide(frame);
        if (didProvide) {
            getBuffer().flip();
        }
        return didProvide;
    }
}
