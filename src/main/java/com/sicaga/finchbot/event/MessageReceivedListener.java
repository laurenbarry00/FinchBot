package com.sicaga.finchbot.event;

import com.sicaga.finchbot.FinchBot;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageReceivedListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // In case the websocket get disconnected, otherwise the bot shows as offline
        if (!FinchBot.getJda().getPresence().getStatus().equals(OnlineStatus.ONLINE)) {
            FinchBot.getJda().getPresence().setPresence(OnlineStatus.ONLINE, false);
        }

        // In dev mode, we only respond to users that are listed in the config
        if (FinchBot.getConfig().isDevModeEnabled()) {
            User user = event.getAuthor();
            if (!FinchBot.getConfig().getDevUserIds().contains(user.getId())) {
                return;
            }
        }
    }
}
