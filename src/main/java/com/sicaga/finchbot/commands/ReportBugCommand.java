package com.sicaga.finchbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sicaga.finchbot.FinchBot;
import com.sicaga.finchbot.util.CustomEmbedBuilder;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;

public class ReportBugCommand extends Command {
    public ReportBugCommand() {
        this.name = "reportbug";
        this.help = "Used to report a bug with FinchBot to developers";
        this.arguments = "(description of bug here)";
        this.guildOnly = false;
        this.ownerCommand = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.replyWarning("Please add a description of the bug you've found.");
            return;
        }

        // Get users and set up a PM channel with each (cannot make a group pm)
        User fermataPlays = FinchBot.jda.getUserById(108992296896196608L);
        User ironOhki = FinchBot.jda.getUserById(222047130007764993L);
        PrivateChannel fermataPlaysPm = fermataPlays.openPrivateChannel().complete();
        PrivateChannel ironOhkiPm = ironOhki.openPrivateChannel().complete();

        CustomEmbedBuilder builder = new CustomEmbedBuilder();
        builder.setTitle("FinchBot Bug Report");
        builder.addField("Submitter", event.getAuthor().getAsMention(), false);
        builder.addField("Report Contents", event.getArgs(), false);

        fermataPlaysPm.sendMessage(builder.build()).queue();
        ironOhkiPm.sendMessage(builder.build()).queue();
        event.reactSuccess();
    }
}
