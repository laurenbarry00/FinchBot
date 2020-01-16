package com.sicaga.finchbot.event;

import com.sicaga.finchbot.FinchBot;
import com.sicaga.finchbot.util.RoleEmotePair;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.GuildManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MessageReactionAddListener extends ListenerAdapter {
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getReaction().isSelf()) return; // if this event was triggered by finchbot, just return/ignore

        HashMap<String, ArrayList<RoleEmotePair>> trackedMessages = FinchBot.getConfig().getTrackedMessages();
        Set<String> keys = trackedMessages.keySet();
        if (keys.contains(event.getMessageId())) {
            // The message is one that we're tracking
            ArrayList<RoleEmotePair> reps = trackedMessages.get(event.getMessageId());

            // this is the name of the emote that the user clicked
            String emoteName = event.getReactionEmote().getName();

            // member object of the user that triggered the event
            Member user = event.getMember();
            List<Role> usersCurrentRoles = user.getRoles();

            // iterate through the role emote pairs for the message
            for (RoleEmotePair pair : reps) {
                // find the role emote pair that corresponds to the emote the user clicked
                if (emoteName.equalsIgnoreCase(pair.getEmote())) {
                    // this is the role the user wants added
                    Role role = pair.getRole();


                    /*
                    Sicaga needs to be defined inside this scope to prevent this error:
                    https://github.com/DV8FromTheWorld/JDA/wiki/19)-Troubleshooting#cannot-get-reference-as-it-has-already-been-garbage-collected
                    */
                    Guild sicaga = FinchBot.getJda().getGuildById(FinchBot.getConfig().getGuildId());

                    // Check if it's a color role emote pair. If so, we need to remove the emote they just added, and remove all the other color roles from the user
                    if (pair.isShouldRemoveEmoteAferAdding()) {
                        // Remove the reaction that the user put
                        event.getReaction().removeReaction(event.getUser()).queue();

                        // Find all the color roles that aren't the new color the user wanted
                        List<Role> rolesToRemove = new ArrayList<>();
                        for (Role r : usersCurrentRoles) {
                            // This will match for color roles only (ex: "red3" or "violet1")
                            if (r.getName().matches("\\b\\w+\\d\\b")) {
                                FinchBot.getLogger().debug("Added " + r.getName() + " to list of roles to remove from user " + user.getEffectiveName());
                                rolesToRemove.add(r);
                            }
                        }

                        // Guild#modifyMemberRoles requires we specify a Collection of Roles to add.
                        // But we only want to add just the one role.
                        List<Role> rolesToAdd = new ArrayList<>();
                        rolesToAdd.add(role);

                        // simultaneously remove all the color roles that aren't the one the user wants and add the one they want
                        sicaga.modifyMemberRoles(user, rolesToAdd, rolesToRemove).queue();
                    } else { // it's not a color role. Simply add the role to the user
                        FinchBot.getLogger().debug("Role " + role.getName() + " added to member: "+ user.getEffectiveName());
                        sicaga.addRoleToMember(user, role).queue();
                    }
                    return;
                }
            }
        }
        // Not a tracked message, just return
    }
}
