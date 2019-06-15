package com.sicaga.finchbot.util;

import net.dv8tion.jda.core.entities.Role;

public class RoleEmotePair {
    private String emote;
    private Role role;
    private boolean removeEmoteAferAdding;

    public RoleEmotePair() {
        this.emote = null;
        this.role = null;
    }

    public RoleEmotePair(String emote, Role role, boolean removeAfterAdding) {
        this.emote = emote;
        this.role = role;
        this.removeEmoteAferAdding = removeAfterAdding;
    }

    public String getEmote() {
        return emote;
    }

    public void setEmote(String emote) {
        this.emote = emote;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isShouldRemoveEmoteAferAdding() {
        return removeEmoteAferAdding;
    }

    public void setRemoveEmoteAfterAdding(boolean shouldRemove) {
        this.removeEmoteAferAdding = shouldRemove;
    }
}
