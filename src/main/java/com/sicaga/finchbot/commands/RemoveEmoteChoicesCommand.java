package com.sicaga.finchbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sicaga.finchbot.FinchBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

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

        // Return if message ID provided isn't a tracked message
        if (!FinchBot.config.getTrackedMessages().containsKey(args[0])) {
            event.replyWarning("Invalid message ID, is it a tracked message?");
            return;
        }

        TextChannel channel = FinchBot.jda.getTextChannelById(FinchBot.config.getRoleEmoteChannel());
        Message message = channel.getMessageById(args[0]).complete();
        List<MessageReaction> messageReactions = message.getReactions();

        for (MessageReaction messageReaction : messageReactions) {
            messageReaction.removeReaction(FinchBot.jda.getSelfUser()).complete();
        }
    }
}
