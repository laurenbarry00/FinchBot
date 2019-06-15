package com.sicaga.finchbot.event;

import com.sicaga.finchbot.FinchBot;
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

        Guild sicaga = FinchBot.jda.getGuildById(464369469595058176L);

        TextChannel channel = sicaga.getTextChannelById(588556612088627231L); // this is the channel where the role assignment will take place

        HashMap<String, ArrayList<RoleEmotePair>> trackedMessages = FinchBot.config.getTrackedMessages();
        Set<String> keys = trackedMessages.keySet();

        // loop through all the messages that we're tracking
        for (String messageId : keys) {
            Message message = channel.getMessageById(messageId).complete();

            ArrayList<RoleEmotePair> reps = trackedMessages.get(messageId);

            for (RoleEmotePair rep : reps) {
                try { // Emote string is in UTF-8 format
                    message.addReaction(rep.getEmote()).complete(); // Must be .complete() as
                    // that will throw the runtimeexception (that we catch below) if it is a custom emote
                } catch (ErrorResponseException e) { // Custom emote, need to create an Emote object and then pass it to addReaction()
                    Emote emote = sicaga.getEmoteById(rep.getEmote());
                    message.addReaction(emote).queue();
                }
            }
        }
    }
}
