package com.sicaga.finchbot.util;

import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.time.Instant;

public class CustomEmbedBuilder extends EmbedBuilder {
    public CustomEmbedBuilder() {
        this.setColor(new Color(255, 243, 17)); // TODO: Put Sicaga yellow color
        this.setFooter("Sicaga FinchBot", "https://cdn.discordapp.com/embed/avatars/0.png"); // TODO: Put image link for FinchBot icon
        this.setAuthor("FinchBot", "http://sicaga.com/#/", "https://cdn.discordapp.com/embed/avatars/0.png");
        this.setTimestamp(Instant.now());
    }
}
