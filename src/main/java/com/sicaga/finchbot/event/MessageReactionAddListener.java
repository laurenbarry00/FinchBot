package com.sicaga.finchbot.event;

import com.sicaga.finchbot.FinchBot;
import com.sicaga.finchbot.util.RoleEmotePair;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MessageReactionAddListener extends ListenerAdapter {
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getReaction().isSelf()) return; // if this is from finchbot, just return

        Guild sicaga = FinchBot.getJda().getGuildById(464369469595058176L);

        HashMap<String, ArrayList<RoleEmotePair>> trackedMessages = FinchBot.getConfig().getTrackedMessages();
        Set<String> keys = trackedMessages.keySet();
        if (keys.contains(event.getMessageId())) {
            // The message is one that we're tracking
            ArrayList<RoleEmotePair> reps = trackedMessages.get(event.getMessageId());
            for (RoleEmotePair pair : reps) {
                // id is null if it's a unicode emoji
                if (event.getReactionEmote().getId() == null) {
                    if (event.getReactionEmote().getName().equals(pair.getEmote())) { // it's the emoji we're looking for
                        Role role = pair.getRole();
                        GuildController gc = new GuildController(sicaga);
                        gc.addRolesToMember(event.getMember(), role).complete(); // Add the role to the user

                        // Don't need to test if we should remove the reaction afterwards, that only applies to custom emotes (color roles)
                    }
                } else { // it's a custom emote
                    if (event.getReactionEmote().getId().equals(pair.getEmote())) {
                        Role newRole = pair.getRole();
                        GuildController gc = new GuildController(sicaga); // Used for interacting with roles

                        if (pair.isShouldRemoveEmoteAferAdding()) { // Clear all of the other color roles off of the user

                            for (String message : keys) {
                                ArrayList<RoleEmotePair> pairs = trackedMessages.get(message);

                                for (RoleEmotePair rep : pairs) {
                                    if (rep.isShouldRemoveEmoteAferAdding()) {
                                        gc.removeRolesFromMember(event.getMember(), rep.getRole()).complete(); // Remove the role.
                                        // This is .complete() because we need to ensure the roles are removed before adding the new one
                                    }
                                }
                            }


                            event.getReaction().removeReaction(event.getUser()).queue(); // only remove the reaction if it's not a color role
                        }
                        // Add the new color role
                        gc.addSingleRoleToMember(event.getMember(), newRole).queue(); // Add the new role
                    }
                }
            }
        }
        // Not a tracked message, just return
    }
}
