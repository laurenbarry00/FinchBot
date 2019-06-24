package com.sicaga.finchbot.event;

import com.sicaga.finchbot.FinchBot;
import com.sicaga.finchbot.util.RoleEmotePair;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class ReadyListener extends ListenerAdapter {
    @Override
    public void onReady(ReadyEvent event) {
        FinchBot.getConfig().loadRoleEmotePairs();

        Guild sicaga = FinchBot.getJda().getGuildById(FinchBot.getConfig().getGuildId());

        if (FinchBot.getConfig().isCollectEmotesModeEnabled()) {
            try {
                FinchBot.getLogger().info("EMOTE COLLECTION MODE");
                FinchBot.getLogger().info("\nEmotes:\n" + sicaga.getEmotes() + "\n");
                FinchBot.getLogger().info("SHUTTING DOWN...");

                FinchBot.getJda().shutdown();
                return;
            } catch (Exception e) { /* Don't need to do anything, just shut it down */ }
        }

        TextChannel channel = sicaga.getTextChannelById(FinchBot.getConfig().getRoleEmoteChannel()); // this is the channel where the role assignment will take place

        // Get all of the tracked messages
        HashMap<String, ArrayList<RoleEmotePair>> trackedMessages = FinchBot.getConfig().getTrackedMessages();
        Set<String> keys = trackedMessages.keySet();

        // loop through all the messages that we're tracking
        for (String messageId : keys) {
            Message message = channel.getMessageById(messageId).complete();

            List<MessageReaction> emotes = message.getReactions();

            ArrayList<RoleEmotePair> reps = trackedMessages.get(messageId);
            for (RoleEmotePair rep : reps) {
                // Color, just add it (so it's in order)
                if (rep.isShouldRemoveEmoteAferAdding()) {
                    List<Emote> emoteList = sicaga.getEmotesByName(rep.getEmote(), true);
                    message.addReaction(emoteList.get(0)).queue();
                } else {
                    // Compare the reactions already on the message to our role emote pairs and add
                    // This is necessary because otherwise the bot will double-up on emotes
                    for (MessageReaction mr : emotes) {
                        MessageReaction.ReactionEmote e = mr.getReactionEmote();
                        if (e.getName().equalsIgnoreCase(rep.getEmote())) {
                            if (e.isEmote()) {
                                message.addReaction(e.getEmote()).complete();
                            } else {
                                message.addReaction(e.getName()).complete();
                            }
                        }
                    }
                }
            }
        }
    }
}
