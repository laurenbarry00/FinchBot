package com.sicaga.finchbot;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sicaga.finchbot.commands.ClearReactionsCommand;
import com.sicaga.finchbot.commands.RemoveReactionCommand;
import com.sicaga.finchbot.event.MessageReactionAddListener;
import com.sicaga.finchbot.event.MessageReactionRemoveListener;
import com.sicaga.finchbot.event.ReadyListener;
import com.sicaga.finchbot.util.Config;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;

public class FinchBot {
    public static JDA jda;
    public static Config config;

    public static void main(String[] args) throws LoginException, IllegalArgumentException {
        // config holds token, owner id, and dev options
        config = new Config();
        config.load();

        // setting up an eventwaiter and the command client
        EventWaiter waiter = new EventWaiter();
        CommandClientBuilder client = new CommandClientBuilder();

        // default playing message is "Type <prefix>help"
        if (config.isDevModeEnabled()) {
            client.setGame(Game.playing("Running in Dev Mode"));
        } else {
            client.useDefaultGame();
        }

        client.setOwnerId(config.getOwnerId());

        // Set emojis for successes, warnings, and failures
        client.setEmojis("\u2705", "\u26A0", "\u274C");

        client.setPrefix("?");

        // add commands to the command handler here
        client.addCommands(
                new RemoveReactionCommand(),
                new ClearReactionsCommand()
        );


        jda = new JDABuilder(AccountType.BOT)
                .setToken(config.getToken())

                // set temporary status while bot is loading
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setGame(Game.playing("Loading..."))

                // add the command and waiter event
                .addEventListener(waiter)
                .addEventListener(client.build())

                // add our own event listeners
                .addEventListener(new ReadyListener())
                .addEventListener(new MessageReactionAddListener())
                .addEventListener(new MessageReactionRemoveListener())

                // start it up!
                .build();

    }
}
