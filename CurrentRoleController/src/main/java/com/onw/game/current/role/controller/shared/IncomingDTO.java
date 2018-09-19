package com.onw.game.current.role.controller.shared;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public class IncomingDTO {

    private UUID roomID;
    private UUID gameID;
    private UUID userToken;
    private Long frontEndSendTime;
    private Map<String, Object> rawData;

    public IncomingDTO() {}

    public UUID getRoomID() {
        return roomID;
    }

    public void setRoomID(UUID roomID) {
        this.roomID = roomID;
    }

    public UUID getGameID() {
        return gameID;
    }

    public void setGameID(UUID gameID) {
        this.gameID = gameID;
    }

    public UUID getUserToken() {
        return userToken;
    }

    public void setUserToken(UUID userToken) {
        this.userToken = userToken;
    }

    public Long getFrontEndSendTime() {
        return frontEndSendTime;
    }

    public void setFrontEndSendTime(Long frontEndSendTime) {
        this.frontEndSendTime = frontEndSendTime;
    }

    public Map<String, Object> getRawData() {
        return rawData;
    }

    public void setRawData(Map<String, Object> rawData) {
        this.rawData = rawData;
    }
}
