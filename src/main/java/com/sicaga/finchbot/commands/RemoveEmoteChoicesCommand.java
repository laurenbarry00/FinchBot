package com.sicaga.finchbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sicaga.finchbot.FinchBot;
import com.sicaga.finchbot.util.RoleEmotePair;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class RemoveEmoteChoicesCommand extends Command {
    public RemoveEmoteChoicesCommand() {
        this.name = "removeemotechoices";
        this.help = "Removes the emote choices from a tracked message";
        this.arguments = "<message ID>";
        this.guildOnly = true;
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        // Return if they haven't given a message ID
        if (event.getArgs().isEmpty()) {
            event.replyWarning("Please specify the message ID to remove emote choices from.");
            return;
        }
        // Return if there are the wrong number of args
        String[] args = event.getArgs().split("\\s+");
        if (args.length != 1) {
            event.replyWarning("Invalid arguments, please specify the message ID to remove emote choices from.");
            return;
        }

        TextChannel channel = FinchBot.getJda().getTextChannelById(FinchBot.getConfig().getRoleEmoteChannel());

        // Want to remove all of the emote choices
        if (args[0].equalsIgnoreCase("all")) {
            HashMap<String, ArrayList<RoleEmotePair>> trackedMessages = FinchBot.getConfig().getTrackedMessages();
            Set<String> keys = trackedMessages.keySet();
            // Loop through all tracked messages
            for (String messageId : keys) {
                Message message = channel.getMessageById(messageId).complete();
                List<MessageReaction> emotes = message.getReactions();
                // Remove all FinchBot's reactions from the message
                for (MessageReaction mr: emotes) {
                    mr.removeReaction(FinchBot.getJda().getSelfUser()).complete();
                }
            }
            FinchBot.getLogger().info("COMMAND RemoveEmoteChoices by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator());
            return;
        }

        // Return if message ID provided isn't a tracked message
        if (!FinchBot.getConfig().getTrackedMessages().containsKey(args[0])) {
            event.replyWarning("Invalid message ID, is it a tracked message?");
            return;
        }

        // Remove the emote choices from a specific message
        Message message = channel.getMessageById(args[0]).complete();
        List<MessageReaction> messageReactions = message.getReactions();

        for (MessageReaction messageReaction : messageReactions) {
            messageReaction.removeReaction(FinchBot.getJda().getSelfUser()).complete();
        }
        FinchBot.getLogger().info("COMMAND RemoveEmoteChoices by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator());
    }
}
