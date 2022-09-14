package com.abubakir.aiborosbot.lpi;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FinalPlayer {
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    Resource resource = resourceLoader.getResource("classpath:audio.mp3");

    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    public FinalPlayer() {
        log.info("instancing audio sourcers");
        this.musicManagers = new HashMap<>();
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public void playPrassodia(VoiceChannel channelToJoin) {
        try {
            loadAndPlay(channelToJoin);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadAndPlay(VoiceChannel channelToJoin) throws IOException {
        GuildMusicManager musicManager = getGuildAudioPlayer(channelToJoin.getGuild());

        playerManager.loadItem(resource.getFile().getPath(), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                play(channelToJoin.getGuild().getAudioManager(), musicManager, track, channelToJoin);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                log.info("No playlist impl available");
            }

            @Override
            public void noMatches() {
                log.info("No matches found for query");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                log.info("Failed to load");
            }
        });
    }

    private GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }
    private void play(AudioManager audioManager, GuildMusicManager musicManager, AudioTrack track, VoiceChannel channelToJoin) {
        if (!audioManager.isConnected()) {
            audioManager.openAudioConnection(channelToJoin);
        }
        musicManager.scheduler.queue(track);
    }
}
