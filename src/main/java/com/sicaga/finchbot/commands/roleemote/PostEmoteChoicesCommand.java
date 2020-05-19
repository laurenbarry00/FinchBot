package com.sicaga.finchbot.commands.roleemote;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sicaga.finchbot.FinchBot;
import com.sicaga.finchbot.event.ReadyListener;
import com.sicaga.finchbot.util.RoleEmotePair;
import net.dv8tion.jda.api.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class PostEmoteChoicesCommand extends Command {

    static Logger log = LoggerFactory.getLogger(PostEmoteChoicesCommand.class);

    public PostEmoteChoicesCommand() {
        this.name = "postemotechoices";
        this.help = "Re-posts the emote choices on tracked messages";
        this.arguments = "";
        this.guildOnly = true;
        this.ownerCommand = true;
        this.category = new Category("Bot Dev");
    }

    @Override
    protected void execute(CommandEvent event) {
        log.info("PostEmoteChoices command by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator());

        Guild sicaga = FinchBot.getJda().getGuildById(FinchBot.getConfig().getGuildId());

        // this is the channel where the role assignment will take place
        TextChannel channel = sicaga.getTextChannelById(FinchBot.getConfig().getRoleEmoteChannel());

        // Get all the tracked messages
        HashMap<String, ArrayList<RoleEmotePair>> trackedMessages = FinchBot.getConfig().getTrackedMessages();
        Set<String> keys = trackedMessages.keySet();

        // loop through all the messages that we're tracking
        for (String messageId : keys) {
            Message message = channel.retrieveMessageById(messageId).complete();

            List<MessageReaction> emotes = message.getReactions();

            ArrayList<RoleEmotePair> reps = trackedMessages.get(messageId);
            for (RoleEmotePair rep : reps) {
                // it's a color emote, just add it (so it's in order)
                if (rep.isShouldRemoveEmoteAferAdding()) {
                    List<Emote> emoteList = sicaga.getEmotesByName(rep.getEmote(), true);
                    message.addReaction(emoteList.get(0)).queue();

                    log.debug("Added reaction " + emoteList.get(0) + " to message " + messageId);
                } else {
                    // Compare the reactions already on the message to our role emote pairs and add
                    addRoleEmotesToMessage(message, emotes, rep);
                }
            }
        }
    }

    public static void addRoleEmotesToMessage(Message message, List<MessageReaction> emotes, RoleEmotePair rep) {
        for (MessageReaction mr : emotes) {
            MessageReaction.ReactionEmote e = mr.getReactionEmote();
            if (e.getName().equalsIgnoreCase(rep.getEmote())) {
                if (e.isEmote()) {
                    message.addReaction(e.getEmote()).complete();
                    log.debug("Added reaction " + e.getEmote().getName() + " to message " + message.getId());
                } else {
                    message.addReaction(e.getName()).complete();
                    log.debug("Added reaction " + e.getName() + " to message " + message.getId());
                }
            }
        }
    }
}
