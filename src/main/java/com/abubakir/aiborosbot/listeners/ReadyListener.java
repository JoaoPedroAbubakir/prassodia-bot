package com.abubakir.aiborosbot.listeners;

import com.abubakir.aiborosbot.lpi.FinalPlayer;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class ReadyListener implements EventListener {

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (genericEvent instanceof GuildVoiceMoveEvent guildVoiceMoveEvent) {
            VoiceChannel channelJoined = (VoiceChannel) guildVoiceMoveEvent.getChannelJoined();
            if (channelJoined.getId().equals("845065150720770048")) {
                new FinalPlayer().playPrassodia(channelJoined);
            }
        }
    }



}
