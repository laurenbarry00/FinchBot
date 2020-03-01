package com.sicaga.finchbot.commands.util;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sicaga.finchbot.FinchBot;
import com.sicaga.finchbot.util.CustomEmbedBuilder;
import net.dv8tion.jda.api.entities.User;

public class ReportBugCommand extends Command {
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

        // create and format the embed with the report contents and the info of who submitted the report
        CustomEmbedBuilder builder = new CustomEmbedBuilder();
        builder.setTitle("FinchBot Bug Report");
        builder.addField("Submitter", event.getAuthor().getAsMention(), false);
        builder.addField("Report Contents", event.getArgs(), false);

        // DM lauren and jer with the bug report
        User fermataPlays = FinchBot.getJda().getUserById(108992296896196608L);
        User ironOhki = FinchBot.getJda().getUserById(222047130007764993L);
        fermataPlays.openPrivateChannel().queue((channel) -> {
            channel.sendMessage(builder.build()).queue();
        });
        ironOhki.openPrivateChannel().queue((channel) -> {
            channel.sendMessage(builder.build()).queue();
        });
        event.reactSuccess();

        FinchBot.getLogger().info("COMMAND ReportBug by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator());
        FinchBot.getLogger().info("COMMAND ReportBug Report Contents: " + event.getArgs());
    }
}
