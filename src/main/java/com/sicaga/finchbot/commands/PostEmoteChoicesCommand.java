package com.sicaga.finchbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sicaga.finchbot.FinchBot;
import com.sicaga.finchbot.util.RoleEmotePair;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class PostEmoteChoicesCommand extends Command {
    public PostEmoteChoicesCommand() {
        this.name = "postemotechoices";
        this.help = "Re-posts the emote choices on tracked messages";
        this.arguments = "";
        this.guildOnly = true;
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        Guild sicaga = FinchBot.getJda().getGuildById(FinchBot.getConfig().getGuildId());

        TextChannel channel = sicaga.getTextChannelById(FinchBot.getConfig().getRoleEmoteChannel()); // this is the channel where the role assignment will take place

        // Get all the tracked messages
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
        FinchBot.getLogger().info("COMMAND PostEmoteChoices by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator());
    }
}
