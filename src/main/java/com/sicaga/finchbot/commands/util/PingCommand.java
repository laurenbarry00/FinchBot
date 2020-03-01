package com.sicaga.finchbot.commands.util;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sicaga.finchbot.FinchBot;
import com.sicaga.finchbot.util.CustomEmbedBuilder;

public class PingCommand extends Command {
    public PingCommand() {
        this.name = "ping";
        this.help = "Prints the ping (latency) for FinchBot";
        this.arguments = "";
        this.guildOnly = false;
        this.ownerCommand = false;
        this.category = new Category("General");
    }

    @Override
    protected void execute(CommandEvent event) {
        CustomEmbedBuilder builder = new CustomEmbedBuilder();
        builder.setTitle("Pong!");
        builder.addField("Ping", FinchBot.getJda().getGatewayPing() + " ms.", false);

        event.reply(builder.build());
    }
}
