package com.sicaga.finchbot.commands.util;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sicaga.finchbot.FinchBot;
import com.sicaga.finchbot.util.CustomEmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.time.format.DateTimeFormatter;

public class WhoAmICommand extends Command {
    public WhoAmICommand() {
        this.name = "whoami";
        this.help = "Prints information about you";
        this.arguments = "";
        this.guildOnly = true;
        this.ownerCommand = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        User user = event.getAuthor();
        Guild guild = FinchBot.getJda().getGuildById(FinchBot.getConfig().getGuildId());
        Member userMember = guild.getMember(user);

        // Custom embed builder for FinchBot
        CustomEmbedBuilder builder = new CustomEmbedBuilder();
        builder.setImage(user.getAvatarUrl());

        // Add their nickname
        builder.addField("Name", userMember.getEffectiveName() + " (" + user.getName() + "#" + user.getDiscriminator() + ")", false);

        // Concat all the role names as bullet points and add
        StringBuilder rolesString = new StringBuilder();
        for (Role role : userMember.getRoles()) {
            rolesString.append("- ");
            rolesString.append(role.getName());
            rolesString.append("\n");
        }
        builder.addField("Roles", rolesString.toString(), false);

        // Add date when member first joined the discord, format: DAY, DD MMMM YYYY HH:MM:SS GMT
        builder.addField("Discord Member Since:", userMember.getTimeJoined().format(DateTimeFormatter.RFC_1123_DATE_TIME), false);

        event.reply(builder.build());
    }
}
