package com.sicaga.finchbot.event;

import com.sicaga.finchbot.FinchBot;
import com.sicaga.finchbot.util.RoleEmotePair;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.GuildManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MessageReactionRemoveListener extends ListenerAdapter {
    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if (event.getReaction().isSelf()) {
            return;
        }

        HashMap<String, ArrayList<RoleEmotePair>> trackedMessages = FinchBot.getConfig().getTrackedMessages();
        Set<String> keys = trackedMessages.keySet();

        if (keys.contains(event.getMessageId())) {
            // The message is one that we're tracking
            ArrayList<RoleEmotePair> reps = trackedMessages.get(event.getMessageId());
            for (RoleEmotePair pair : reps) {
                if (pair.isShouldRemoveEmoteAferAdding()) {
                    return;
                }
                // If the reaction matches a role emote pair, remove the role associated
                if (event.getReactionEmote().getName().equalsIgnoreCase(pair.getEmote())) {
                    Role role = pair.getRole();
                    Guild sicaga = FinchBot.getJda().getGuildById(FinchBot.getConfig().getGuildId());
                    assert sicaga != null;
                    /*
                    Sicaga needs to be defined here to prevent this error:
                    https://github.com/DV8FromTheWorld/JDA/wiki/19)-Troubleshooting#cannot-get-reference-as-it-has-already-been-garbage-collected
                     */
                    sicaga.removeRoleFromMember(event.getMember(), role).complete(); // Remove the role
                    FinchBot.getLogger().debug("Role " + role.getName() + " removed from member: "+ event.getMember().getNickname());
                }
            }
        }
        // Not a tracked message, just return
    }
}
