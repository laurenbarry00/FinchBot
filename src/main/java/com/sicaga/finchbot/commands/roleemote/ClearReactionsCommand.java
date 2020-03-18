package com.sicaga.finchbot.commands.roleemote;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sicaga.finchbot.FinchBot;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ClearReactionsCommand extends Command {

    Logger log = LoggerFactory.getLogger(ClearReactionsCommand.class);

    public ClearReactionsCommand() {
        this.name = "clearreactions";
        this.help = "Removes all reactions from a message";
        this.arguments = "<message id>";
        this.guildOnly = false;
        this.ownerCommand = true;
        this.category = new Category("Bot Dev");
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

            // get message id from arguments
            String messageId = items[0];

            try {
                // get the message that we're removing the reactions from
                // this must be in the same channel as where the command was executed
                Message message = event.getTextChannel().retrieveMessageById(messageId).complete();

                message.clearReactions().queue();
                log.info("ClearReactions command on message: " + messageId + " by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator());
            } catch (ErrorResponseException e) {
                if (e.getErrorResponse().toString().equalsIgnoreCase("unknown_message")) { // Message not found
                    event.replyError(e.getMeaning() + ", are you in the correct channel?");
                } else {
                    event.replyError(e.getMeaning());
                }
            }
        }
    }
}
