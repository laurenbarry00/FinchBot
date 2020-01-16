package com.sicaga.finchbot.commands.roleemote;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sicaga.finchbot.FinchBot;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.util.List;

public class ClearReactionsCommand extends Command {
    public ClearReactionsCommand() {
        this.name = "clearreactions";
        this.help = "Removes all reactions from a message";
        this.arguments = "<message id>";
        this.guildOnly = false;
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.replyWarning("Missing arguments, please include message ID");
        }  else {
            String[] items = event.getArgs().split("\\s+");
            if (items.length != 1) {
                event.replyWarning("Invalid arguments, please include message ID");
                return;
            }

            // our arguments
            String messageId = items[0];

            try {
                Message message = event.getTextChannel().retrieveMessageById(messageId).complete();
                message.clearReactions().queue();
                FinchBot.getLogger().info("COMMAND ClearReactions on message: " + messageId + " by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator());
            } catch (ErrorResponseException e) { // Message not found
                if (e.getErrorResponse().toString().equalsIgnoreCase("unknown_message")) {
                    event.replyError(e.getMeaning() + ", are you in the correct channel?");
                } else {
                    event.replyError(e.getMeaning());
                }
            }
        }
    }
}