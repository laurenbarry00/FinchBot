package com.sicaga.finchbot;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FinchBot {
    public static void main(String[] args) throws IOException, LoginException, IllegalArgumentException {
        // config.txt has two lines: bot token and owner ID
        List<String> configList = Files.readAllLines(Paths.get("src/config.txt"));
        final String token = configList.get(0);
        final String ownerId = configList.get(1);

        // setting up an eventwaiter and the command client
        EventWaiter waiter = new EventWaiter();
        CommandClientBuilder client = new CommandClientBuilder();

        // default playing message is "Type <prefix>help"
        client.useDefaultGame();

        client.setOwnerId(ownerId);

        // Set emojis for successes, warnings, and failures
        client.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");

        client.setPrefix("?");

        // add commands to the command handler here
        /*
        client.addCommands(

        );
        */

        new JDABuilder(AccountType.BOT)
                .setToken(token)

                // set temporary status while bot is loading
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setGame(Game.playing("Loading..."))

                // add the command and waiter listeners
                .addEventListener(waiter)
                .addEventListener(client.build())

                .build();
    }
}
