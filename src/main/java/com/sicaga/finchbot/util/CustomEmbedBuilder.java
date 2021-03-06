package com.sicaga.finchbot.util;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;
import java.time.Instant;

public class CustomEmbedBuilder extends EmbedBuilder {
    public CustomEmbedBuilder() {
        this.setColor(new Color(255, 243, 18));
        this.setFooter("Sicaga FinchBot", "https://i.imgur.com/S0SNpyv.png");
        this.setAuthor("FinchBot", "http://sicaga.com/#/", "https://i.imgur.com/S0SNpyv.png");
        this.setTimestamp(Instant.now());
    }
}
