package com.sicaga.finchbot.commands.util;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sicaga.finchbot.util.CustomEmbedBuilder;

public class SourceCommand extends Command {
    public SourceCommand() {
        this.name = "source";
        this.help = "Provides a link to FinchBot's source code on GitHub";
        this.arguments = "";
        this.guildOnly = false;
        this.ownerCommand = false;
        this.category = new Category("General");
    }

    @Override
    protected void execute(CommandEvent event) {
        CustomEmbedBuilder builder = new CustomEmbedBuilder();
        builder.addField("GitHub", "https://github.com/laurenbarry00/FinchBot", false);

        event.reply(builder.build());
    }
}