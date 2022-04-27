package com.abubakir.aiborosbot.app;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public abstract class MessageListener {
    public Mono<Void> processCommand(Message eventMessage) {
        return Mono.empty();
    }
}
