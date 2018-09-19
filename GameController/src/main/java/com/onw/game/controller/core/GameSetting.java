package com.onw.game.controller.core;

import com.onw.game.controller.shared.GameControllerUtil;

import java.util.List;
import java.util.UUID;

public class GameSetting {

    private String roomID;
    private List<UUID> playerTokens;
    private List<String> roles;
    private Long prepareTime;
    private Long shortStunTime;                 // In milliseconds.
    private Long phaseTime;                     // In milliseconds.
    private Long discussTime;                   // In milliseconds.
    private Long voteTime;                      // In milliseconds.

    @SuppressWarnings("WeakerAccess")
    public GameSetting() {

    }

    @SuppressWarnings("WeakerAccess")
    public String getRoomID() {
        return roomID;
    }

    @SuppressWarnings("WeakerAccess")
    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    @SuppressWarnings("WeakerAccess")
    public List<UUID> getPlayerTokens() {
        return playerTokens;
    }

    @SuppressWarnings("WeakerAccess")
    public void setPlayerTokens(List<UUID> playerTokens) {
        this.playerTokens = playerTokens;
    }

    @SuppressWarnings("WeakerAccess")
    public List<String> getRoles() {
        return roles;
    }

    @SuppressWarnings("WeakerAccess")
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @SuppressWarnings("WeakerAccess")
    public Long getPrepareTime() {
        return prepareTime;
    }

    @SuppressWarnings("WeakerAccess")
    public void setPrepareTime(Long prepareTime) {
        this.prepareTime = prepareTime;
    }

    @SuppressWarnings("WeakerAccess")
    public Long getPhaseTime() {
        return phaseTime;
    }

    @SuppressWarnings("WeakerAccess")
    public void setPhaseTime(Long phaseTime) {
        this.phaseTime = phaseTime;
    }

    @SuppressWarnings("WeakerAccess")
    public Long getDiscussTime() {
        return discussTime;
    }

    @SuppressWarnings("WeakerAccess")
    public void setDiscussTime(Long discussTime) {
        this.discussTime = discussTime;
    }

    @SuppressWarnings("WeakerAccess")
    public Long getVoteTime() {
        return voteTime;
    }

    @SuppressWarnings("WeakerAccess")
    public void setVoteTime(Long voteTime) {
        this.voteTime = voteTime;
    }

    @SuppressWarnings("WeakerAccess")
    public Long getShortStunTime() {
        return shortStunTime;
    }

    @SuppressWarnings("WeakerAccess")
    public void setShortStunTime(Long shortStunTime) {
        this.shortStunTime = shortStunTime;
    }

    @Override
    public String toString() {
        return GameControllerUtil.convertToJSON(this);
    }

}
