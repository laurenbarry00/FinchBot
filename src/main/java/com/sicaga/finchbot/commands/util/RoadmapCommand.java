package com.sicaga.finchbot.commands.util;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sicaga.finchbot.util.CustomEmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoadmapCommand extends Command {

    Logger log = LoggerFactory.getLogger(RoadmapCommand.class);

    public RoadmapCommand() {
        this.name = "roadmap";
        this.help = "Provides a link to FinchBot's roadmap of coming features";
        this.arguments = "";
        this.guildOnly = false;
        this.ownerCommand = false;
        this.category = new Category("General");
    }

    @Override
    protected void execute(CommandEvent event) {
        log.info("Roadmap command by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator());

        CustomEmbedBuilder builder = new CustomEmbedBuilder();
        builder.addField("FinchBot Roadmap", "https://github.com/laurenbarry00/FinchBot#roadmap", false);

        event.reply(builder.build());
    }
}
