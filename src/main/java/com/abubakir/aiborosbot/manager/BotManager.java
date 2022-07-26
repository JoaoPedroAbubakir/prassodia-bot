package com.abubakir.aiborosbot.manager;

import discord4j.core.DiscordClient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BotManager {

    DiscordClient client;

}
