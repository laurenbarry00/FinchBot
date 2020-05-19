package com.sicaga.finchbot.util;

import com.jagrosh.jdautilities.menu.Paginator;
import com.sicaga.finchbot.FinchBot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocialMediaPostSession {
    // The artist's Discord account, as a guild Member entity
    Member sessionUser;

    // The artist's name as it should be displayed
    String artistName;

    // the name of the artist's comic
    String comicTitle;

    // the URL of the artist's comic.
    // This should be where the artist wants viewers to land on, for example a "latest updates" page, or simply their home page
    String comicUrl;

    // the currently selected template
    String selectedTemplate;

    private Logger log = LoggerFactory.getLogger(SocialMediaPostSession.class);

    // constructor w/ comic info & discord user
    public SocialMediaPostSession(Member sessionUser, String artistName, String comicTitle, String comicUrl) {
        this.sessionUser = sessionUser;
        this.artistName = artistName;
        this.comicTitle = comicTitle;
        this.comicUrl = comicUrl;

        this.selectedTemplate = "";
    }

    public Member getSessionUser() {
        return sessionUser;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getSelectedTemplate() {
        return selectedTemplate;
    }

    public void showPreview(MessageChannel channel) {
        Paginator.Builder pBuilder = new Paginator.Builder()
                .waitOnSinglePage(true)
                .showPageNumbers(true)
                .wrapPageEnds(true)
                .setEventWaiter(FinchBot.getWaiter())
                .setUsers(sessionUser.getUser())
                .setColor(new Color(255, 243, 18))
                .setItemsPerPage(1)
                .setTimeout(3, TimeUnit.MINUTES)
                .setText("Navigate to a template by clicking the left and right arrow emojis." +
                        "\nClick :stop_button: to select the currently shown template." +
                        "\nThis selection menu will time out after 3 minutes. Just run `!update` again in order to continue picking a template.")
                .setFinalAction(m -> {
                    this.selectedTemplate = m.getEmbeds().get(0).getDescription();

                    try {
                        m.clearReactions().queue();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }

                    // Store the user's selection in an embed to repeat back to them
                    CustomEmbedBuilder builder = new CustomEmbedBuilder();
                    builder.addField("Type `!post` to post to Sicaga social media, or use `!update` to keep editing.", this.selectedTemplate, false);
                    builder.setAuthor(this.getSessionUser().getEffectiveName() + " selected post template: ");

                    // send the embed, log the user's selection
                    channel.sendMessage(builder.build()).queue();
                    log.debug(sessionUser.getEffectiveName() + " selected social media template: " + this.selectedTemplate);

                    // remove the paginator embed to keep clutter down
                    m.delete().queue();
                });

        ArrayList<String> templates = FinchBot.getConfig().getSocialTemplates();
        for (String t : templates) {
            // sub in the information into the template
            t = t.replace("{name}", artistName);
            t = t.replace("{comic}", comicTitle);
            t = t.replace("{url}", comicUrl);

            // append a message to the template that's currently selected
            if (selectedTemplate.equalsIgnoreCase(t)) {
                t = "**[Currently selected]**\n" + t;
            }

            pBuilder.addItems(t);
        }

        Paginator p = pBuilder.build();
        p.paginate(channel, 1);
    }
}
