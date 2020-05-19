package com.sicaga.finchbot.commands.social;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sicaga.finchbot.FinchBot;
import com.sicaga.finchbot.util.SocialMediaPostSession;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class UpdateCommand extends Command {

    Logger log = LoggerFactory.getLogger(UpdateCommand.class);

    public UpdateCommand() {
        this.name = "update";
        this.help = "Creates a post on the Sicaga social media to promote updates to the user's comic.";
        this.arguments = "";
        this.guildOnly = false;
        this.ownerCommand = false;
        this.category = new Category("Social Media");
    }

    @Override
    protected void execute(CommandEvent event) {
        // going to use their discord id to compare whether they're on the whitelist
        String userId = event.getAuthor().getId();
        JsonObject whitelist = FinchBot.getConfig().getSocialMediaWhitelist();

        // check if the user's on the whitelist
        if (whitelist.has(userId)) {
            log.info("Update command by whitelisted user: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator());

            // contains the user's display name, comic title, and comic url
            JsonObject userComicDetails = whitelist.getAsJsonObject(userId);

            ArrayList<SocialMediaPostSession> activeSessions = FinchBot.getActiveSocialMediaSessions();

            // Search the active sessions to find the user's active session (if it exists)
            for (SocialMediaPostSession session : activeSessions) {
                // if the user has an existing session, we'll work with their existing session
                if (session.getSessionUser().getId().equals(userId)) {
                    log.info("Whitelisted user: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + " already has an existing post session.");
                    session.showPreview(event.getChannel());
                    return;
                }
            }

            try {
                // get the comic data from Json
                String artistName = userComicDetails.get("artistName").getAsString();
                String comicTitle = userComicDetails.get("comicTitle").getAsString();
                String comicUrl = userComicDetails.get("comicUrl").getAsString();

                // the user doesn't have an existing session, so just create a new one
                SocialMediaPostSession newSession = new SocialMediaPostSession(event.getMember(), artistName, comicTitle, comicUrl);
                newSession.showPreview(event.getChannel());
                FinchBot.addSocialMediaSession(newSession);

                log.info(event.getAuthor().getName() +  "#" + event.getAuthor().getDiscriminator() + " created a social media post session.");
            } catch (NullPointerException e) {
                // one of the above fields is missing, error out
                log.error("One or more user fields in social media whitelist is empty!");
                User ironOhki = FinchBot.getJda().getUserById(222047130007764993L);
                event.replyError("You're whitelisted, but your settings weren't configured correctly. Please contact <@" + ironOhki.getId() + ">");
                return;
            }
        } else {
            log.info("Update command by non-whitelisted user: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator());

            // The user isn't whitelisted. Point them to Jer for more info/to see about getting whitelisted.
            User ironOhki = FinchBot.getJda().getUserById(222047130007764993L);
            event.replyError("You are not on the whitelist (list of allowed users) for posting to Sicaga social media. Please contact <@" + ironOhki.getId() + "> for more information.");
        }
    }
}
