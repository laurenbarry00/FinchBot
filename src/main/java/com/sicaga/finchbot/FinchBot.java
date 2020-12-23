package com.sicaga.finchbot;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sicaga.finchbot.commands.roleemote.ClearReactionsCommand;
import com.sicaga.finchbot.commands.roleemote.PostEmoteChoicesCommand;
import com.sicaga.finchbot.commands.roleemote.RemoveEmoteChoicesCommand;
import com.sicaga.finchbot.commands.roleemote.RemoveReactionCommand;
import com.sicaga.finchbot.commands.social.PostCommand;
import com.sicaga.finchbot.commands.social.UpdateCommand;
import com.sicaga.finchbot.commands.util.*;
import com.sicaga.finchbot.event.MessageReactionAddListener;
import com.sicaga.finchbot.event.MessageReactionRemoveListener;
import com.sicaga.finchbot.event.MessageReceivedListener;
import com.sicaga.finchbot.event.ReadyListener;
import com.sicaga.finchbot.util.Config;
import com.sicaga.finchbot.util.SocialMediaPostSession;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;

public class FinchBot {
    private static JDA jda;
    private static Config config;

    private static final Logger log = LoggerFactory.getLogger(FinchBot.class);

    private static final EventWaiter waiter = new EventWaiter();

    private static ArrayList<SocialMediaPostSession> activeSocialMediaSessions = new ArrayList<>();

    public static void main(String[] args) throws LoginException, IllegalArgumentException {
        log.info("==================================================");
        log.info("=                 Sicaga FinchBot                =");
        log.info("==================================================");


        // config holds token, owner id, and dev options
        config = new Config();
        config.load();

        CommandClientBuilder client = new CommandClientBuilder();

        // default playing message is "Type <prefix>help"
        if (config.isDevModeEnabled()) {
            log.info("FinchBot initialized in development mode.");
            client.setActivity(Activity.playing("Running in Dev Mode"));
        } else {
            client.useDefaultGame();
        }

        client.setOwnerId(config.getOwnerId());
        log.info("Owner ID set to: " + config.getOwnerId());

        client.setCoOwnerIds(config.getDevUserIds().get(0), config.getDevUserIds().get(1));
        log.info("Co-Owner IDs set.");


        // Set emojis for successes, warnings, and failures
        client.setEmojis("\u2705", "\u26A0", "\u274C");

        client.setPrefix(FinchBot.config.getPrefix());
        log.info("Command Prefix set to: " + config.getPrefix());

        // Add commands to the command handler
        // General Commands
        client.addCommands(
                new PingCommand(),
                new ReportBugCommand(),
                new RoadmapCommand(),
                new SourceCommand(),
                new WhoAmICommand()
        );
        // Bot Dev
        client.addCommands(
                new ShutdownCommand(),
                new ClearReactionsCommand(),
                new PostEmoteChoicesCommand(),
                new RemoveEmoteChoicesCommand(),
                new RemoveReactionCommand()
        );
        // Social Media
        client.addCommands(
                new PostCommand(waiter),
                new UpdateCommand()
        );

        log.info("Initializing JDA bot client.");

        jda = JDABuilder.createDefault(config.getToken())
                // set temporary status while bot is loading
                // this is set to online upon ReadyEvent
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.playing("Loading..."))

                // add the command and waiter event
                .addEventListeners(waiter, client.build())

                // add our own event listeners
                .addEventListeners(new ReadyListener(),
                        new MessageReactionAddListener(),
                        new MessageReactionRemoveListener(),
                        new MessageReceivedListener())

                // start it up!
                .build();
    }

    public static JDA getJda() {
        return jda;
    }

    public static Config getConfig() {
        return config;
    }

    public static EventWaiter getWaiter() {
        return waiter;
    }

    public static ArrayList<SocialMediaPostSession> getActiveSocialMediaSessions() {
        return activeSocialMediaSessions;
    }

    public static void removeSocialMediaSession(SocialMediaPostSession session) {
        activeSocialMediaSessions.remove(session);
    }

    public static void addSocialMediaSession(SocialMediaPostSession session) {
        if (!activeSocialMediaSessions.contains(session)) {
            activeSocialMediaSessions.add(session);
        }
    }
}
