package com.abubakir.aiborosbot.commands;

import com.abubakir.aiborosbot.audio.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Play implements Command{

    String commandName;

    TrackScheduler trackScheduler;
    AudioPlayerManager playerManager;

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.justOrEmpty(event.getMessage().getContent())
                .map(content -> Arrays.asList(content.split(" ")))
                .doOnNext(commmand -> playerManager.loadItem(commmand.get(1), trackScheduler))
                .then();
    }

    @Override
    public String getName() {
        return this.commandName;
    }
}
