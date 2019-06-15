package com.sicaga.finchbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sicaga.finchbot.FinchBot;
import net.dv8tion.jda.core.entities.*;

import java.util.List;

public class RemoveReactionCommand extends Command {
    public RemoveReactionCommand() {
        this.name = "removereaction";
        this.help = "Removes a reaction from a message";
        this.arguments = "<message id> <emote name> <user id>";
        this.guildOnly = false;
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.replyWarning("Missing arguments, please include message ID, emote name/ID, and user ID.");
        }  else {
            String[] items = event.getArgs().split("\\s+");
            if (items.length != 3) {
                event.replyWarning("Invalid arguments, please include message ID, emote name/ID, and user ID.");
                return;
            }

            /*
             * messageId: the message that has the reaction we're looking to delete
             * emoteName: either the ID (in the case of a custom emote) or the emoji that we want to delete
             * userId: the id of the user whose reaction we're trying to delete
             */
            String messageId = items[0];
            String emoteName = items[1];
            String userId = items[2];

            User user = FinchBot.jda.getUserById(userId);

            try {
                Emote emote = event.getGuild().getEmoteById(emoteName); // This will throw an exception (caught below) if it is unicode emote

                event.getTextChannel().removeReactionById(messageId, emote, user).queue();
            } catch (NumberFormatException e) { // It is a unicode emoji
                Message message = event.getTextChannel().getMessageById(messageId).complete();
                List<MessageReaction> reactions = message.getReactions();

                for (MessageReaction reaction : reactions) { // Get all the reaction from the message, compare to the emote we're looking to delete until we find it
                    if (reaction.getReactionEmote().getId() == null) {
                        if (reaction.getReactionEmote().getName().equals(emoteName)) {
                            reaction.removeReaction(user).queue();
                        }
                    } else {
                        if (reaction.getReactionEmote().getId().equals(emoteName)) {
                            reaction.removeReaction(user).queue();
                        }
                    }
                }
            }
        }
    }
}
