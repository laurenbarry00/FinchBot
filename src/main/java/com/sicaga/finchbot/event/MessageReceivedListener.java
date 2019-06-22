package com.sicaga.finchbot.event;

import com.sicaga.finchbot.FinchBot;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageReceivedListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        FinchBot.getJda().getPresence().setPresence(OnlineStatus.ONLINE, false); // In case the websocket get disconnected, otherwise the bot shows as offline

        if (FinchBot.getConfig().isDevModeEnabled()) { // In dev mode, we only respond to users that are listed in the config
            User user = event.getAuthor();
            if (!FinchBot.getConfig().getDevUserIds().contains(user.getId())) {
                return;
            }
        }
    }
}
