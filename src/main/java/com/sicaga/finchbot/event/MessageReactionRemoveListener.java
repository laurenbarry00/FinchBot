package com.sicaga.finchbot.event;

import com.sicaga.finchbot.FinchBot;
import com.sicaga.finchbot.util.RoleEmotePair;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MessageReactionRemoveListener extends ListenerAdapter {
    Logger log = LoggerFactory.getLogger(MessageReactionAddListener.class);

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if (event.getReaction().isSelf()) return; // ignore reactions removed by FinchBot

        HashMap<String, ArrayList<RoleEmotePair>> trackedMessages = FinchBot.getConfig().getTrackedMessages();
        Set<String> keys = trackedMessages.keySet();

        // If the message is one that we're tracking
        if (keys.contains(event.getMessageId())) {
            ArrayList<RoleEmotePair> reps = trackedMessages.get(event.getMessageId());

            for (RoleEmotePair pair : reps) {
                // ignore if it's a color role
                if (pair.isShouldRemoveEmoteAferAdding()) {
                    return;
                }

                // this is the name of the emote that the user clicked
                String emoteName = event.getReactionEmote().getName();

                // this is the user that we're removing the role from
                Member user = event.getMember();

                // If the reaction matches a role emote pair, remove the role associated
                if (emoteName.equalsIgnoreCase(pair.getEmote())) {
                    Role role = pair.getRole();
                    Guild sicaga = FinchBot.getJda().getGuildById(FinchBot.getConfig().getGuildId());
                    /*
                    Sicaga needs to be defined here to prevent this error:
                    https://github.com/DV8FromTheWorld/JDA/wiki/19)-Troubleshooting#cannot-get-reference-as-it-has-already-been-garbage-collected
                     */

                    // Remove the role
                    sicaga.removeRoleFromMember(user, role).queue((success) -> {
                        log.info("Role " + role.getName() + " removed from member: "+ user.getEffectiveName());
                    });
                }
            }
        }
        // Not a tracked message, just return
    }
}
