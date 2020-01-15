package com.sicaga.finchbot.util;

import com.google.gson.*;
import com.sicaga.finchbot.FinchBot;
import net.dv8tion.jda.api.entities.Role;

import java.io.FileReader;
import java.io.IOException;
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

    private ArrayList<String> devUserIds;
    private HashMap<String, ArrayList<RoleEmotePair>> trackedMessages;

    public Config() {
        this.token = null;
        this.ownerId = null;
        this.guildId = null;
        this.prefix = null;
        this.roleEmoteChannel = null;
        this.devModeEnabled = false;
        this.devUserIds = null;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getRoleEmoteChannel() {
        return roleEmoteChannel;
    }

    public void setRoleEmoteChannel(String roleEmoteChannel) {
        this.roleEmoteChannel = roleEmoteChannel;
    }

    public boolean isDevModeEnabled() {
        return devModeEnabled;
    }

    public void setDevModeEnabled(boolean devModeEnabled) {
        this.devModeEnabled = devModeEnabled;
    }

    public boolean isCollectEmotesModeEnabled() {
        return collectEmotesModeEnabled;
    }

    public void setCollectEmotesModeEnabled(boolean collectEmotesModeEnabled) {
        this.collectEmotesModeEnabled = collectEmotesModeEnabled;
    }

    public ArrayList<String> getDevUserIds() {
        return devUserIds;
    }

    public HashMap<String, ArrayList<RoleEmotePair>> getTrackedMessages() {
        return trackedMessages;
    }

    public void load() {
        try {
            // For reading in the raw JSON from file
            JsonParser parser = new JsonParser();

            // Parse the json from file and create a JsonObject of the whole thing
            JsonObject root = (JsonObject) parser.parse(new FileReader("config.json"));
            FinchBot.getLogger().info("Parsing file config.json.");

            // Get token, owner ID, and dev mode status
            this.token = root.get("token").getAsString();
            FinchBot.getLogger().debug("Loaded token from file.");
            this.ownerId = root.get("ownerId").getAsString();
            FinchBot.getLogger().debug("Loaded owner ID from file.");
            this.guildId = root.get("guildId").getAsString();
            FinchBot.getLogger().debug("Loaded guild ID from file.");
            this.prefix = root.get("prefix").getAsString();
            FinchBot.getLogger().debug("Loaded prefix from file.");
            this.roleEmoteChannel = root.get("roleEmoteChannel").getAsString();
            FinchBot.getLogger().debug("Loaded role emote channel from file.");
            this.devModeEnabled = root.getAsJsonObject("dev").get("devModeEnabled").getAsBoolean();
            FinchBot.getLogger().debug("Loaded dev mode status from file.");
            this.collectEmotesModeEnabled = root.getAsJsonObject("dev").get("collectEmotesModeEnabled").getAsBoolean();
            FinchBot.getLogger().debug("Loaded emote collection mode status from file.");

            //initialize the list
            devUserIds = new ArrayList<>();
            // convert list of dev user ids to a json array
            JsonArray jsonDevIds = root.getAsJsonObject("dev").get("devUserIds").getAsJsonArray();
            // Loop through json array and add the ids to our outward-facing ArrayList of dev user ids.
            for (int i = 0; i < jsonDevIds.size(); i++) {
                devUserIds.add(jsonDevIds.get(i).getAsString());
                FinchBot.getLogger().debug("Loaded dev user ID " + jsonDevIds.get(i).getAsString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadRoleEmotePairs() {
        try {
            // For reading in the raw JSON from file
            JsonParser parser = new JsonParser();

            // Parse the json from file and create a JsonObject of the whole thing
            JsonObject root = (JsonObject) parser.parse(new FileReader("config.json"));
            FinchBot.getLogger().info("Parsing file config.json.");

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
                    FinchBot.getLogger().info("Loaded role: " + role.getName() + ", emote: " + emote + ", shouldRemoveAfterAdding: " + shouldRemoveAfterAdding);
                    reps.add(rep);
                }
                // Add the message and REP list to our hashmap
                trackedMessages.put(key, reps);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
