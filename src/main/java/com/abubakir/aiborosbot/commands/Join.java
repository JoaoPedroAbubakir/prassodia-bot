package com.abubakir.aiborosbot.commands;

import com.abubakir.aiborosbot.audio.LavaPlayerAudioProvider;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Join implements Command{

    String commandName;
    LavaPlayerAudioProvider audioProvider;

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.justOrEmpty(event.getMember())
                .flatMap(Member::getVoiceState)
                .flatMap(VoiceState::getChannel)
                .flatMap(voiceChannel -> voiceChannel.join().withProvider(audioProvider))
                .then();
    }

    @Override
    public String getName() {
        return this.commandName;
    }
}
