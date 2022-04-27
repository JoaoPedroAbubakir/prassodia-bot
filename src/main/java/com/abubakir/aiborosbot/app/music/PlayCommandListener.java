package com.abubakir.aiborosbot.app.music;

import com.abubakir.aiborosbot.app.EventListener;
import com.abubakir.aiborosbot.app.MessageListener;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlayCommandListener extends MessageListener
    implements EventListener<MessageCreateEvent> {

  private Logger log = LoggerFactory.getLogger(PlayCommandListener.class);

  private final AudioPlayerManager playerManager;
  private final Map<Long, GuildMusicManager> musicManagers;

  public PlayCommandListener() {
    this.musicManagers = new ConcurrentHashMap<>();

    this.playerManager = new DefaultAudioPlayerManager();
    AudioSourceManagers.registerRemoteSources(playerManager);
    AudioSourceManagers.registerLocalSource(playerManager);
  }

  private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
    long guildId = guild.getId().asLong();
    GuildMusicManager musicManager = musicManagers.get(guildId);

    if (musicManager == null) {
      musicManager = new GuildMusicManager(playerManager);
      musicManagers.put(guildId, musicManager);
    }

    return musicManager;
  }

  @Override
  public Class<MessageCreateEvent> getEventType() {
    return null;
  }

  @Override
  public Mono<Void> execute(MessageCreateEvent event) {
    Message message = event.getMessage();

    Mono.just(message.getContent())
        .map(
            it -> {
              MessageChannel channel = message.getChannel().block();

              if (channel instanceof TextChannel) {
                String[] command = it.split(" ", 2);

                if ("~play".equals(command[0]) && command.length == 2) {
                  loadAndPlay((TextChannel) channel, command[1]);
                } else if ("~skip".equals(command[0])) {
                  skipTrack((TextChannel) channel);
                }
              }
              return Mono.empty();
            });
    return Mono.empty();
  }

  private void loadAndPlay(TextChannel channel, final String trackUrl) {
    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild().block());

    playerManager.loadItemOrdered(
        musicManager,
        trackUrl,
        new AudioLoadResultHandler() {
          @Override
          public void trackLoaded(AudioTrack track) {
            sendMessageToChannel(channel, "Adding to queue " + track.getInfo().title);

            play(channel.getGuild().block(), musicManager, track);
          }

          @Override
          public void playlistLoaded(AudioPlaylist playlist) {
            AudioTrack firstTrack = playlist.getSelectedTrack();

            if (firstTrack == null) {
              firstTrack = playlist.getTracks().get(0);
            }

            sendMessageToChannel(
                channel,
                "Adding to queue "
                    + firstTrack.getInfo().title
                    + " (first track of playlist "
                    + playlist.getName()
                    + ")");

            play(channel.getGuild().block(), musicManager, firstTrack);
          }

          @Override
          public void noMatches() {
            sendMessageToChannel(channel, "Nothing found by " + trackUrl);
          }

          @Override
          public void loadFailed(FriendlyException exception) {
            sendMessageToChannel(channel, "Could not play: " + exception.getMessage());
          }
        });
  }

  private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track) {
    GuildMusicManager manager = getGuildAudioPlayer(guild);
    attachToFirstVoiceChannel(guild);
    musicManager.scheduler.queue(track);
  }

  private void skipTrack(TextChannel channel) {
    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild().block());
    musicManager.scheduler.nextTrack();

    sendMessageToChannel(channel, "Skipped to next track.");
  }

  private void sendMessageToChannel(TextChannel channel, String message) {
    try {
      channel.createMessage(message).block();
    } catch (Exception e) {
      log.warn("Failed to send message {} to {}", message, channel.getName(), e);
    }
  }

  private static void attachToFirstVoiceChannel(Guild guild) {
    VoiceChannel voiceChannel = guild.getChannels().ofType(VoiceChannel.class).blockFirst();
    // Check if any VoiceState for this guild relates to bot
    guild
        .getVoiceStates() // Check if any VoiceState for this guild relates to bot
        .any(
            voiceState ->
                Boolean.TRUE.equals(
                    Mono.just(guild.getClient().getSelfId())
                        .map(voiceState.getUserId()::equals)
                        .block()));
    boolean inVoiceChannel = false;

    if (!inVoiceChannel) {
      voiceChannel.join().block();
    }
  }
}
