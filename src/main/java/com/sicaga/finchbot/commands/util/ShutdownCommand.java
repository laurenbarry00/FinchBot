package com.sicaga.finchbot.commands.util;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sicaga.finchbot.FinchBot;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class ShutdownCommand extends Command {
    private EventWaiter waiter;

    public ShutdownCommand() {
        this.name = "shutdown";
        this.help = "Terminates FinchBot";
        this.arguments = "";
        this.guildOnly = false;
        this.ownerCommand = true;
        this.category = new Category("Bot Dev");

        this.waiter = FinchBot.getWaiter();
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("Are you sure you would like to shut down FinchBot? Type `" + FinchBot.getConfig().getPrefix() + "confirm`.");

        // wait for 1 minutes, check for user to confirm shutdown
        waiter.waitForEvent(MessageReceivedEvent.class,
                // Check if same author and if they typed !confirm
                e -> e.getAuthor().equals(event.getAuthor()) && e.getMessage().getContentRaw().equalsIgnoreCase(FinchBot.getConfig().getPrefix().concat("confirm")),
                // Shutdown
                e -> shutdown(event),
                // Waiter times out after one minute
                1, TimeUnit.MINUTES, () -> event.reply("Sorry, you took too long."));
    }

    private void shutdown(CommandEvent event) {
        try {
            event.reactWarning();
            FinchBot.getJda().getPresence().setPresence(OnlineStatus.OFFLINE, true);
            Thread.sleep(50);

            FinchBot.getLogger().info("COMMAND Shutdown by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator());

            // disconnect from websocket, exit program
            FinchBot.getJda().shutdown();
            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
            event.replyError("An error occurred while shutting down. Please wait a few moments and try again.");
        }
    }
}
