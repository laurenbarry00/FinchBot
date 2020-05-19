package com.sicaga.finchbot.util;

import net.dv8tion.jda.api.entities.Role;

public class RoleEmotePair {
    private String emote;
    private Role role;
    private boolean removeEmoteAferAdding;

    public RoleEmotePair(String emote, Role role, boolean removeAfterAdding) {
        this.emote = emote;
        this.role = role;
        this.removeEmoteAferAdding = removeAfterAdding;
    }

    public String getEmote() {
        return emote;
    }

    public Role getRole() {
        return role;
    }

    public boolean isShouldRemoveEmoteAferAdding() {
        return removeEmoteAferAdding;
    }
}
