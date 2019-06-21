package com.sicaga.finchbot.event;

import com.sicaga.finchbot.FinchBot;
import com.sicaga.finchbot.commands.PostEmoteChoicesCommand;
import com.sicaga.finchbot.util.RoleEmotePair;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class ReadyListener extends ListenerAdapter {
    @Override
    public void onReady(ReadyEvent event) {
        FinchBot.config.loadRoleEmotePairs();

        Guild sicaga = FinchBot.jda.getGuildById(FinchBot.config.getGuildId());

        if (FinchBot.config.isCollectEmotesModeEnabled()) {
            try {
                System.out.println("EMOTE COLLECTION MODE");
                System.out.println("\nEmotes:\n" + sicaga.getEmotes() + "\n");
                System.out.println("SHUTTING DOWN...");

                FinchBot.jda.shutdown();
            } catch (Exception e) { /* Don't need to do anything, just shut it down */ }
        }

        TextChannel channel = sicaga.getTextChannelById(FinchBot.config.getRoleEmoteChannel()); // this is the channel where the role assignment will take place

        // Get all of the tracked messages
        HashMap<String, ArrayList<RoleEmotePair>> trackedMessages = FinchBot.config.getTrackedMessages();
        Set<String> keys = trackedMessages.keySet();

        // loop through all the messages that we're tracking
        for (String messageId : keys) {
            Message message = channel.getMessageById(messageId).complete();

            ArrayList<RoleEmotePair> reps = trackedMessages.get(messageId);

            for (RoleEmotePair rep : reps) {
                try { // Emote string is in UTF-8 format
                    message.addReaction(rep.getEmote()).complete(); // Must be .complete() as that will throw the runtimeexception (that we catch below) if it is a custom emote
                } catch (ErrorResponseException e) { // Custom emote, need to create an Emote object and then pass it to addReaction()
                    Emote emote = sicaga.getEmoteById(rep.getEmote());
                    message.addReaction(emote).complete(); // This needs to be not asynchronous to make sure that the colors are added in the right order
                }
            }
        }
    }
}
