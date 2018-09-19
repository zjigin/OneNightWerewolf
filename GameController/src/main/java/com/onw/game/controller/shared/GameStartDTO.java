package com.onw.game.controller.shared;

import java.util.List;
import java.util.UUID;

public class GameStartDTO {

    private String roomID;

    private List<UUID> playerTokenList;
    private String role;

    @SuppressWarnings("unused")
    public List<UUID> getPlayerTokenList() {
        return playerTokenList;
    }

    @SuppressWarnings("WeakerAccess")
    public void setPlayerTokenList(List<UUID> playerTokenList) {
        this.playerTokenList = playerTokenList;
    }

    @SuppressWarnings("unused")
    public String getRole() {
        return role;
    }

    @SuppressWarnings("WeakerAccess")
    public void setRole(String role) {
        this.role = role;
    }

    @SuppressWarnings("unused")
    public String getRoomID() {
        return roomID;
    }

    @SuppressWarnings("WeakerAccess")
    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }
}
