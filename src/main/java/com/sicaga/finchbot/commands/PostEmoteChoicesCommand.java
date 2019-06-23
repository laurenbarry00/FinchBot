package com.sicaga.finchbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sicaga.finchbot.FinchBot;
import com.sicaga.finchbot.util.RoleEmotePair;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;

import java.util.ArrayList;
import java.util.HashMap;
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

            ArrayList<RoleEmotePair> reps = trackedMessages.get(messageId);

            for (RoleEmotePair rep : reps) {
                try { // Emote string is in UTF-8 format
                    message.addReaction(rep.getEmote()).complete(); // Must be .complete() as that will throw the runtimeexception (that we catch below) if it is a custom emote
                } catch (ErrorResponseException e) { // Custom emote, need to create an Emote object and then pass it to addReaction()
                    if (e.getErrorResponse().toString().equalsIgnoreCase("unknown_emoji")) {
                        Emote emote = sicaga.getEmoteById(rep.getEmote());
                        message.addReaction(emote).complete(); // This needs to be not asynchronous to make sure that the colors are added in the right order
                    } else {
                        event.replyError(e.getMeaning());
                    }
                }
            }
        }
        FinchBot.getLogger().info("COMMAND PostEmoteChoices by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator());
    }
}
