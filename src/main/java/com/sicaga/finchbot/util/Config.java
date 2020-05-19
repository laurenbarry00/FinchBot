package com.sicaga.finchbot.util;

import com.google.gson.*;
import com.sicaga.finchbot.FinchBot;
import net.dv8tion.jda.api.entities.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Config {
    private String token;
    private String ownerId;
    private String guildId;
    private String prefix;
    private String roleEmoteChannel;

    private boolean devModeEnabled;
    private boolean collectEmotesModeEnabled;
    private boolean shouldSkipRoleEmoteInit;

    private ArrayList<String> devUserIds;
    private HashMap<String, ArrayList<RoleEmotePair>> trackedMessages;
    private ArrayList<String> socialTemplates;

    private Logger log = LoggerFactory.getLogger(Config.class);

    public Config() {
        this.token = null;
        this.ownerId = null;
        this.guildId = null;
        this.prefix = null;
        this.roleEmoteChannel = null;

        this.devModeEnabled = false;
        this.shouldSkipRoleEmoteInit = false;
        this.collectEmotesModeEnabled = false;

        this.devUserIds = new ArrayList<>();
        this.trackedMessages = new HashMap<>();
        this.socialTemplates = new ArrayList<>();
    }

    public String getToken() {
        return token;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getRoleEmoteChannel() {
        return roleEmoteChannel;
    }

    public boolean isDevModeEnabled() {
        return devModeEnabled;
    }

    public boolean isCollectEmotesModeEnabled() {
        return collectEmotesModeEnabled;
    }

    public ArrayList<String> getDevUserIds() {
        return devUserIds;
    }

    public HashMap<String, ArrayList<RoleEmotePair>> getTrackedMessages() {
        return trackedMessages;
    }

    public boolean shouldSkipRoleEmoteInit() {
        return this.shouldSkipRoleEmoteInit;
    }

    public ArrayList<String> getSocialTemplates() {
        return this.socialTemplates;
    }

    public void load() {
        log.info("Loading Bot Configuration from file.");
        try {
            // For reading in the raw JSON from file
            JsonParser parser = new JsonParser();

            log.info("Reading and parsing file config.json.");
            // Parse the json from file and create a JsonObject of the whole thing
            JsonObject root = (JsonObject) parser.parse(new FileReader("config.json"));

            // Get token, owner ID, and dev mode status
            this.token = root.get("token").getAsString();
            if (token == null) {
                log.error("Token failed to load from file.");
            } else {
                log.debug("Token loaded successfully from file.");
            }

            this.ownerId = root.get("ownerId").getAsString();
            if (ownerId == null) {
                log.error("Owner ID failed to load from file.");
            } else {
                log.debug("Owner ID loaded successfully from file.");
            }

            this.guildId = root.get("guildId").getAsString();
            if (guildId == null) {
                log.error("Sicaga Guild ID failed to load from file.");
            } else {
                log.debug("Sicaga Guild ID loaded successfully from file.");
            }

            this.prefix = root.get("prefix").getAsString();
            if (prefix == null) {
                log.error("Command Prefix failed to load from file.");
            } else {
                log.debug("Command Prefix loaded successfully from file.");
            }

            this.roleEmoteChannel = root.get("roleEmoteChannel").getAsString();
            if (roleEmoteChannel == null) {
                log.error("Role Emote Channel failed to load from file.");
            } else {
                log.debug("Role Emote Channel loaded successfully from file.");
            }

            this.shouldSkipRoleEmoteInit = root.get("shouldSkipRoleEmoteInit").getAsBoolean();
            log.debug("Should Skip RoleEmote Initialization set to: " + shouldSkipRoleEmoteInit);

            this.devModeEnabled = root.getAsJsonObject("dev").get("devModeEnabled").getAsBoolean();
            log.debug("Dev Mode set to: " + devModeEnabled);

            this.collectEmotesModeEnabled = root.getAsJsonObject("dev").get("collectEmotesModeEnabled").getAsBoolean();
            log.debug("Emote Collection Mode set to: " + collectEmotesModeEnabled);

            //initialize the list
            devUserIds = new ArrayList<>();

            // convert list of dev user ids to a json array
            JsonArray jsonDevIds = root.getAsJsonObject("dev").get("devUserIds").getAsJsonArray();
            log.debug("Dev User IDs successfully loaded from file.");

            // Loop through json array and add the ids to our outward-facing ArrayList of dev user ids.
            for (int i = 0; i < jsonDevIds.size(); i++) {
                devUserIds.add(jsonDevIds.get(i).getAsString());
                log.debug("Dev User ID loaded: " + jsonDevIds.get(i).getAsString());
            }

        } catch (FileNotFoundException e) {
            log.error("Could not load config file! Aborting bot initialization.");
            System.exit(-1);
        }
    }

    public void loadRoleEmotePairs() {
        log.info("Loading RoleEmote Pair Configuration from file.");
        try {
            // For reading in the raw JSON from file
            JsonParser parser = new JsonParser();

            log.info("Reading and parsing file config.json.");
            // Parse the json from file and create a JsonObject of the whole thing
            JsonObject root = (JsonObject) parser.parse(new FileReader("config.json"));

            // initialize and populate the list of tracked message IDs
            trackedMessages = new HashMap<>();
            JsonObject jsonTrackedMessages = root.get("trackedMessages").getAsJsonObject();

            // iterate through all the tracked messages
            Set<String> messageKeys = jsonTrackedMessages.keySet();
            for (String key : messageKeys) {
                // JsonObject for the individual message that we're tracking
                JsonObject message = jsonTrackedMessages.get(key).getAsJsonObject();

                // this will be the list of RoleEmotePairs that are associated with the message
                ArrayList<RoleEmotePair> reps = new ArrayList<>();

                // all the roles associated with the message
                Set<String> roles = jsonTrackedMessages.get(key).getAsJsonObject().keySet();

                for (String roleKey : roles) {
                    //
                    JsonArray ar = message.get(roleKey).getAsJsonArray();

                    // Get a role object from the role ID in the json
                    Role role = FinchBot.getJda().getGuildById(guildId).getRoleById(roleKey);

                    // this is either the unicode or emote id
                    String emote = ar.get(0).getAsString();

                    // whether or not the bot should auto-remove the reaction after it's added by a user
                    // (ie. for color roles)
                    boolean shouldRemoveAfterAdding = ar.get(1).getAsBoolean();

                    // create the RoleEmotePair and add it to the list
                    RoleEmotePair rep = new RoleEmotePair(emote, role, shouldRemoveAfterAdding);
                    log.debug("Loaded role: " + role.getName() + ", emote: " + emote + ", shouldRemoveAfterAdding: " + shouldRemoveAfterAdding);
                    reps.add(rep);
                }
                // Add the message and REP list to our hashmap
                trackedMessages.put(key, reps);
            }
        } catch (FileNotFoundException e) {
            log.error("Could not load config file! Role-Emote Pair functionality will not work!");
        }
    }

    public JsonObject getSocialMediaWhitelist() {
        JsonObject socialMediaWhitelist;
        try {
            // get the social media whitelist and the users' comic details from remote json
            // getting the json from airtable
            URL whitelistUrl = new URL("http://138.68.253.109/sicagacomics/index.json");

            log.info("Downloading social media whitelist.");
            socialMediaWhitelist = retrieveJsonFromUrl(whitelistUrl);

            if (socialMediaWhitelist.size() > 0) {
                log.debug("Social media whitelist successfully loaded with " + socialMediaWhitelist.size() + " users from remote file.");
                return socialMediaWhitelist;
            }

        } catch (MalformedURLException e) {
            log.error("URL to social media whitelist invalid. Social media functions will not work correctly!");
        }
        return null;
    }

    public void loadSocialMediaTemplates() {
        try {
            // get the post templates from file and populate the list of templates
            // getting the json from airtable
            URL templatesUrl = new URL("http://138.68.253.109/tweetstrings/index.json");

            // read templates
            log.info("Downloading social media templates.");
            JsonObject root = retrieveJsonFromUrl(templatesUrl);
            JsonArray templatesArray = root.get("Tweet Strings").getAsJsonArray();

            if (templatesArray != null) {
                log.debug("Social media templates successfully loaded from remote file.");
            }

            for (JsonElement templateElement : templatesArray) {
                String template = templateElement.getAsJsonObject().get("tweetString").getAsString();
                if (template.length() > 0) {
                    log.debug("Social media template loaded: " + template);
                    socialTemplates.add(template);
                }
            }

            if (socialTemplates.size() > 0) {
                log.debug("Social media templates successfully loaded from file.");
            }
        } catch (MalformedURLException e) {
            log.error("URL to social media whitelist invalid. Social media functions will not work correctly!");
        }
    }

    private JsonObject retrieveJsonFromUrl(URL url) {
        JsonParser parser = new JsonParser();
        StringBuilder sb = new StringBuilder();

        log.info("Attempting to download JSON from: " + url.toString());
        try {
            // read JSON from url as raw string
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                sb.append(chars, 0, read);
            }
        } catch (IOException e) {
            log.error("Error occurred while reading JSON from URL. Social media functions will not work correctly!");
        }

        // parse raw string JSON to JsonObject
        return (JsonObject) parser.parse(sb.toString());
    }
}
