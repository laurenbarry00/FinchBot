package com.sicaga.finchbot.commands.roleemote;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sicaga.finchbot.FinchBot;
import net.dv8tion.jda.api.entities.*;

import java.util.List;

public class RemoveReactionCommand extends Command {
    public RemoveReactionCommand() {
        this.name = "removereaction";
        this.help = "Removes a reaction from a message";
        this.arguments = "<message id> <emote name> <user id>";
        this.guildOnly = false;
        this.ownerCommand = true;
        this.category = new Category("Bot Dev");
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.replyWarning("Missing arguments, please include message ID, emote name, and user ID.");
        }  else {
            String[] items = event.getArgs().split("\\s+");
            if (items.length != 3) {
                event.replyWarning("Invalid arguments, please include message ID, emote name, and user ID.");
                return;
            }

            /*
             * messageId: the message that has the reaction we're looking to delete
             * emoteName: the emoji that we want to delete
             * userId: the id of the user whose reaction we're trying to delete
             */
            String messageId = items[0];
            String emoteName = items[1];
            String userId = items[2];

            User user = FinchBot.getJda().getUserById(userId);

            TextChannel channel = event.getTextChannel();
            Message message = channel.retrieveMessageById(messageId).complete();
            List<MessageReaction> mrList = message.getReactions();

            for (MessageReaction mr : mrList) {
                if (mr.getReactionEmote().getName().equalsIgnoreCase(emoteName)) {
                    mr.removeReaction(user).queue();
                    FinchBot.getLogger().info("COMMAND RemoveReaction (reaction: " + mr.getReactionEmote().getName() + ") on message: " + messageId + " by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator());
                    return;
                }
            }
        }
    }
}
