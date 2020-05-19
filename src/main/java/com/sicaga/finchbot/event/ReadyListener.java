package com.sicaga.finchbot.event;

import com.sicaga.finchbot.FinchBot;
import com.sicaga.finchbot.commands.roleemote.PostEmoteChoicesCommand;
import com.sicaga.finchbot.util.RoleEmotePair;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class ReadyListener extends ListenerAdapter {

    Logger log = LoggerFactory.getLogger(ReadyListener.class);

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        // get the role-emote pairs from the config file
        // this needs to be executed onReady as opposed to otherwise because we need to retrieve guild-specific information as we do this
        FinchBot.getConfig().loadRoleEmotePairs();

        // load the social media post templates from file
        // the whitelist will be requested on each instance of !update
        FinchBot.getConfig().loadSocialMediaTemplates();

        Guild sicaga = FinchBot.getJda().getGuildById(FinchBot.getConfig().getGuildId());

        if (FinchBot.getConfig().isCollectEmotesModeEnabled()) {
            try {
                log.debug("EMOTE COLLECTION MODE");
                log.debug("\nEmotes:\n" + sicaga.getEmotes() + "\n");
                log.debug("SHUTTING DOWN...");

                FinchBot.getJda().shutdown();
                return;
            } catch (Exception e) { /* Don't need to do anything, just shut it down */ }
        } else if (FinchBot.getConfig().shouldSkipRoleEmoteInit()) {
            log.info("Skipping role emote pair initialization (not adding emotes to tracked messages)...");
            return;
        }

        TextChannel channel = sicaga.getTextChannelById(FinchBot.getConfig().getRoleEmoteChannel()); // this is the channel where the role assignment will take place

        // Get all of the tracked messages
        HashMap<String, ArrayList<RoleEmotePair>> trackedMessages = FinchBot.getConfig().getTrackedMessages();
        Set<String> keys = trackedMessages.keySet();

        // loop through all the messages that we're tracking
        for (String messageId : keys) {
            Message message = channel.retrieveMessageById(messageId).complete();

            List<MessageReaction> emotes = message.getReactions();

            ArrayList<RoleEmotePair> reps = trackedMessages.get(messageId);
            for (RoleEmotePair rep : reps) {
                // Color, just add it (so it's in order)
                if (rep.isShouldRemoveEmoteAferAdding()) {
                    try {
                        List<Emote> emoteList = sicaga.getEmotesByName(rep.getEmote(), true);
                        message.addReaction(emoteList.get(0)).queue((success) -> {
                            log.debug("Added RoleEmote emote " + rep.getEmote() + " to message " + messageId);
                        });
                    } catch (IndexOutOfBoundsException e) {
                        log.debug("Color emote " + rep.getEmote() + " already added to message " + messageId);
                    }
                } else {
                    // Compare the reactions already on the message to our role emote pairs and add
                    // This is necessary because otherwise the bot will double-up on emotes
                    PostEmoteChoicesCommand.addRoleEmotesToMessage(message, emotes, rep);
                }
            }
        }
    }
}
