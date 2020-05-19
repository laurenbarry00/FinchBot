package com.sicaga.finchbot.event;

import com.sicaga.finchbot.FinchBot;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageReceivedListener extends ListenerAdapter {
    Logger log = LoggerFactory.getLogger(MessageReceivedListener.class);

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // In case the websocket get disconnected, otherwise the bot shows as offline
        if (!FinchBot.getJda().getPresence().getStatus().equals(OnlineStatus.ONLINE)) {
            log.info("Set Finchbot online status to ONLINE.");
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
