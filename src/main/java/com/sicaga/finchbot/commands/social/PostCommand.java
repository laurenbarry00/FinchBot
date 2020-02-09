package com.sicaga.finchbot.commands.social;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sicaga.finchbot.FinchBot;
import com.sicaga.finchbot.util.CustomEmbedBuilder;
import com.sicaga.finchbot.util.SocialMediaPostSession;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PostCommand extends Command {
    EventWaiter waiter;
    public PostCommand(EventWaiter waiter) {
        this.name = "post";
        this.help = "Posts to Sicaga social media. Use `!update` to select a post template first.";
        this.arguments = "";
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = 5; // TODO: 10 minutes cooldown
        this.cooldownScope = CooldownScope.GLOBAL;
        this.category = new Category("Social Media");

        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent event) {
        String userId = event.getAuthor().getId(); // going to use their discord id
        ArrayList<SocialMediaPostSession> activeSessions = FinchBot.getActiveSocialMediaSessions();

        for (SocialMediaPostSession session : activeSessions) {
            // find if the user has a currently active session
            if (session.getSessionUser().getId().equals(userId)) {
                // the user has an active session, but they haven't selected a template yet
                if (session.getSelectedTemplate().isEmpty()) {
                    event.replyWarning("You haven't confirmed your template selection. Did you forget to click :stop_button:?");
                    return;
                }

                // send a confirmation of what they want to send
                CustomEmbedBuilder builder = new CustomEmbedBuilder();
                builder.setAuthor("Post this message social media?");
                builder.addField("React with :white_check_mark: to confirm your choice and post.", session.getSelectedTemplate(), false);

                // before sending, wait for 2 minutes until the user reacts with a check mark
                Message success = event.getChannel().sendMessage(builder.build()).complete();
                success.addReaction("\u2705").queue();

                waiter.waitForEvent(MessageReactionAddEvent.class,
                        e -> {
                            // check that it's the person who ran the command
                            if (!e.getMember().equals(event.getMember())) {
                                return false;
                            } else return e.getReactionEmote().getName().equalsIgnoreCase("\u2705"); // confirmed
                        },
                        e -> postMessage(session, event.getChannel()), 2, TimeUnit.MINUTES, () -> {
                            event.reply("Sorry, you took too long to confirm. Please run `!post` again.");
                            success.delete().queue();
                        });
                return;
            }
        }

        // will only reach here if the user doesn't have an active session
        event.replyError("You haven't set up your post template yet! Please run `!update` first to do this.");
    }

    private void postMessage(SocialMediaPostSession session, MessageChannel returnChannel) {
        returnChannel.sendTyping().queue();

        // remove the session from the list of active sessions
        // the session is still preserved in case the tweet doesn't go through, in which case we'll add it back to the active list
        FinchBot.removeSocialMediaSession(session);

        FinchBot.getLogger().info(session.getArtistName() + " initiated post to social media. Message content: " + session.getSelectedTemplate());

        try {
            // send the tweet
            Twitter twitter = TwitterFactory.getSingleton();
            Status status = twitter.updateStatus(session.getSelectedTemplate());

            // assemble the link of the newly posted status (via the status ID)
            String linkBuilder = "https://twitter.com/" +
                    status.getUser().getScreenName() +
                    "/status/" +
                    status.getId();

            // Send the link of the posted status to the user
            CustomEmbedBuilder embedBuilder = new CustomEmbedBuilder();
            embedBuilder.addField("Tweet sent successfully. See it here:", linkBuilder, false);
            returnChannel.sendMessage(embedBuilder.build()).queue();

            FinchBot.getLogger().info("Tweet sent by " + session.getArtistName() + " with content: " + session.getSelectedTemplate());
        } catch (TwitterException e) {
            // add the session back to the list of active sessions, since the tweet didn't go through.
            FinchBot.addSocialMediaSession(session);

            if (e.getMessage().contains("duplicate")) {
                returnChannel.sendMessage("The tweet you are attempting to send is a duplicate. Check Sicaga's twitter to see if it already went through!").queue();
            } else {
                returnChannel.sendMessage("There was a problem sending " + session.getSessionUser().getAsMention() +
                        "'s post to Twitter. Please wait and try again later.").queue();

                FinchBot.getLogger().error(e.getMessage());
            }
        }
    }
}
