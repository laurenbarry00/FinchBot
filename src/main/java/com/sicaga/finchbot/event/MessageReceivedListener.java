package com.sicaga.finchbot.event;

import com.sicaga.finchbot.FinchBot;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageReceivedListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (FinchBot.config.isDevModeEnabled()) { // In dev mode, we only respond to users that are listed in the config
            User user = event.getAuthor();
            for (String devUserId : FinchBot.config.getDevUserIds()) { // loop through all of the dev user IDS
                if (user != FinchBot.jda.getUserById(devUserId)) {
                    return; // ignore the user's message if it's not a dev user
                }
            }
        }
    }
}
