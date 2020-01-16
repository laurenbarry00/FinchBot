package com.sicaga.finchbot.event;

import com.sicaga.finchbot.FinchBot;
import com.sicaga.finchbot.util.RoleEmotePair;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class ReadyListener extends ListenerAdapter {
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        FinchBot.getConfig().loadRoleEmotePairs();

        Guild sicaga = FinchBot.getJda().getGuildById(FinchBot.getConfig().getGuildId());
        assert sicaga != null;

        if (FinchBot.getConfig().isCollectEmotesModeEnabled()) {
            try {
                FinchBot.getLogger().info("EMOTE COLLECTION MODE");
                FinchBot.getLogger().info("\nEmotes:\n" + sicaga.getEmotes() + "\n");
                FinchBot.getLogger().info("SHUTTING DOWN...");

                FinchBot.getJda().shutdown();
                return;
            } catch (Exception e) { /* Don't need to do anything, just shut it down */ }
        } else if (FinchBot.getConfig().shouldSkipRoleEmoteInit()) {
            FinchBot.getLogger().info("Skipping role emote pair initialization (adding emotes to tracked messages)...");
            return;
        }

        TextChannel channel = sicaga.getTextChannelById(FinchBot.getConfig().getRoleEmoteChannel()); // this is the channel where the role assignment will take place

        // Get all of the tracked messages
        HashMap<String, ArrayList<RoleEmotePair>> trackedMessages = FinchBot.getConfig().getTrackedMessages();
        Set<String> keys = trackedMessages.keySet();

        // loop through all the messages that we're tracking
        for (String messageId : keys) {
            assert channel != null;
            Message message = channel.retrieveMessageById(messageId).complete();

            List<MessageReaction> emotes = message.getReactions();

            ArrayList<RoleEmotePair> reps = trackedMessages.get(messageId);
            for (RoleEmotePair rep : reps) {
                // Color, just add it (so it's in order)
                if (rep.isShouldRemoveEmoteAferAdding()) {
                    try {
                        List<Emote> emoteList = sicaga.getEmotesByName(rep.getEmote(), true);
                        message.addReaction(emoteList.get(0)).queue();
                    } catch (IndexOutOfBoundsException e) {
                        FinchBot.getLogger().debug("Color emote " + rep.getEmote() + " already added to message " + messageId);
                    }
                } else {
                    // Compare the reactions already on the message to our role emote pairs and add
                    // This is necessary because otherwise the bot will double-up on emotes
                    addRoleEmotesToMessage(message, emotes, rep);
                }
            }
        }
    }

    public static void addRoleEmotesToMessage(Message message, List<MessageReaction> emotes, RoleEmotePair rep) {
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
