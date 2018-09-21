package com.onw.game.current.role.controller.dao;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@SuppressWarnings("unused")
@Entity
public class Games {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    @Column
    @Type(type="uuid-char")
    private UUID gameID;

    @Column
    @Type(type="uuid-char")
    private UUID roomID;

    @Column
    private String playerID;

    @Column
    private String playerInitialRole;

    @Column
    private String playerCurrentRole;

    @Column
    private String voteTo;

    @Column
    private Boolean voted;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date createDate;

    @Column
    private Boolean passTurn;

    @Column
    private String note;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UUID getGameID() {
        return gameID;
    }

    public void setGameID(UUID gameID) {
        this.gameID = gameID;
    }

    public UUID getRoomID() {
        return roomID;
    }

    public void setRoomID(UUID roomID) {
        this.roomID = roomID;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public String getPlayerInitialRole() {
        return playerInitialRole;
    }

    public void setPlayerInitialRole(String playerInitialRole) {
        this.playerInitialRole = playerInitialRole;
    }

    public String getPlayerCurrentRole() {
        return playerCurrentRole;
    }

    public void setPlayerCurrentRole(String playerCurrentRole) {
        this.playerCurrentRole = playerCurrentRole;
    }

    public Date getCreateDate() {
        return new Date(createDate.getTime());
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate != null ? new Date(createDate.getTime()) : null;
    }

    public Boolean getPassTurn() {
        return passTurn;
    }

    public void setPassTurn(Boolean passTurn) {
        this.passTurn = passTurn;
    }

    public String getVoteTo() {
        return voteTo;
    }

    public void setVoteTo(String voteTo) {
        this.voteTo = voteTo;
    }

    public Boolean getVoted() {
        return voted;
    }

    public void setVoted(Boolean voted) {
        this.voted = voted;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
