package com.sicaga.finchbot.commands.util;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sicaga.finchbot.FinchBot;
import com.sicaga.finchbot.util.CustomEmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportBugCommand extends Command {

    Logger log = LoggerFactory.getLogger(ReportBugCommand.class);

    public ReportBugCommand() {
        this.name = "reportbug";
        this.help = "Used to report a bug with FinchBot to developers";
        this.arguments = "<description of bug here>";
        this.guildOnly = false;
        this.ownerCommand = false;
        this.category = new Category("General");
    }

    @Override
    protected void execute(CommandEvent event) {
        // user must provide a brief description of the issue
        if (event.getArgs().isEmpty()) {
            event.replyWarning("Please add a description of the bug you've found.");
            return;
        }

        log.info("ReportBug command by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator());
        log.info("ReportBug Report Contents: " + event.getArgs());

        // create and format the embed with the report contents and the info of who submitted the report
        CustomEmbedBuilder builder = new CustomEmbedBuilder();
        builder.setTitle("FinchBot Bug Report");
        builder.addField("Submitter", "<@" + event.getAuthor().getId() + ">", false);
        builder.addField("Report Contents", event.getArgs(), false);

        // DM lauren and jer with the bug report
        User fermataPlays = FinchBot.getJda().getUserById(108992296896196608L);
        User ironOhki = FinchBot.getJda().getUserById(222047130007764993L);
        fermataPlays.openPrivateChannel().queue((channel) -> {
            channel.sendMessage(builder.build()).queue((success) -> {
                log.info("ReportBug report sent successfully to Lauren.");
            });
        });
        ironOhki.openPrivateChannel().queue((channel) -> {
            channel.sendMessage(builder.build()).queue((success) -> {
                log.info("ReportBug report sent successfully to Jer.");
            });
        });
        event.reactSuccess();
    }
}
