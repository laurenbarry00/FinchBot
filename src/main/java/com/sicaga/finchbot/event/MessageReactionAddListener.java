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

        HashMap<String, ArrayList<RoleEmotePair>> trackedMessages = FinchBot.getConfig().getTrackedMessages();
        Set<String> keys = trackedMessages.keySet();
        if (keys.contains(event.getMessageId())) {
            // The message is one that we're tracking
            ArrayList<RoleEmotePair> reps = trackedMessages.get(event.getMessageId());
            for (RoleEmotePair pair : reps) {
                if (event.getReactionEmote().getName().equalsIgnoreCase(pair.getEmote())) {
                    Role role = pair.getRole();

                    // It's a color role emote pair
                    if (pair.isShouldRemoveEmoteAferAdding()) {
                        // Remove the reaction that the user put
                        event.getReaction().removeReaction(event.getUser()).queue();

                        // Remove all the other color roles
                        for (String message : keys) {
                            ArrayList<RoleEmotePair> pairs = trackedMessages.get(message);

                            ArrayList<Role> rolesToRemove = new ArrayList<>();
                            for (RoleEmotePair rep : pairs) {
                                if (rep.isShouldRemoveEmoteAferAdding() && rep.getRole() != role) {
                                    rolesToRemove.add(rep.getRole());
                                }
                            }
                            // Remove all the color roles that aren't the new color
                            /*
                            Sicaga needs to be defined inside this scope to prevent this error:
                            https://github.com/DV8FromTheWorld/JDA/wiki/19)-Troubleshooting#cannot-get-reference-as-it-has-already-been-garbage-collected
                             */
                            Guild sicaga = FinchBot.getJda().getGuildById(FinchBot.getConfig().getGuildId());
                            GuildController gc = new GuildController(sicaga);
                            gc.removeRolesFromMember(event.getMember(), rolesToRemove).complete();
                            // This needs to be .complete() because all the other colors need to be removed before we add the new one
                        }
                    }

                    /*
                    Sicaga is declared redundantly in this scope in order to prevent this error:
                    https://github.com/DV8FromTheWorld/JDA/wiki/19)-Troubleshooting#cannot-get-reference-as-it-has-already-been-garbage-collected
                     */
                    Guild sicaga = FinchBot.getJda().getGuildById(FinchBot.getConfig().getGuildId());
                    GuildController gc = new GuildController(sicaga);
                    gc.addSingleRoleToMember(event.getMember(), role).complete(); // Add the role to the user
                    FinchBot.getLogger().debug("Role " + role.getName() + " added to member: "+ event.getMember().getNickname());
                    return;
                }
            }
        }
        // Not a tracked message, just return
    }
}
