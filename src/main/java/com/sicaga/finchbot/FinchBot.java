package com.sicaga.finchbot;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sicaga.finchbot.commands.roleemote.ClearReactionsCommand;
import com.sicaga.finchbot.commands.roleemote.PostEmoteChoicesCommand;
import com.sicaga.finchbot.commands.roleemote.RemoveEmoteChoicesCommand;
import com.sicaga.finchbot.commands.roleemote.RemoveReactionCommand;
import com.sicaga.finchbot.commands.util.*;
import com.sicaga.finchbot.event.MessageReactionAddListener;
import com.sicaga.finchbot.event.MessageReceivedListener;
import com.sicaga.finchbot.event.ReadyListener;
import com.sicaga.finchbot.util.Config;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class FinchBot {
    private static JDA jda;
    private static Config config;
    private static final Logger log = LoggerFactory.getLogger(FinchBot.class);

    public static void main(String[] args) throws LoginException, IllegalArgumentException {
        // config holds token, owner id, and dev options
        config = new Config();
        config.load();

        // setting up an eventwaiter and the command client
        EventWaiter waiter = new EventWaiter();
        CommandClientBuilder client = new CommandClientBuilder();

        // default playing message is "Type <prefix>help"
        if (config.isDevModeEnabled()) {
            client.setActivity(Activity.playing("Running in Dev Mode"));
        } else {
            client.useDefaultGame();
        }

        client.setOwnerId(config.getOwnerId());
        client.setCoOwnerIds(config.getDevUserIds().get(0), config.getDevUserIds().get(1));


        // Set emojis for successes, warnings, and failures
        client.setEmojis("\u2705", "\u26A0", "\u274C");

        client.setPrefix(FinchBot.config.getPrefix());

        // add commands to the command handler here
        client.addCommands(
                new PingCommand(),
                new WhoAmICommand(),
                new SourceCommand(),
                new RoadmapCommand(),
                new ReportBugCommand(),
                new ShutdownCommand(waiter),
                new RemoveReactionCommand(),
                new ClearReactionsCommand(),
                new PostEmoteChoicesCommand(),
                new RemoveEmoteChoicesCommand()
        );

        jda = new JDABuilder(AccountType.BOT)
                .setToken(config.getToken())

                // set temporary status while bot is loading
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.playing("Loading..."))

                // add the command and waiter event
                .addEventListeners(waiter, client.build())

                // add our own event listeners
                .addEventListeners(new ReadyListener(),
                        new MessageReactionAddListener(),
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

    public static Logger getLogger() {
        return log;
    }
}
