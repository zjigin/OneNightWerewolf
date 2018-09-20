package com.onw.game.current.role.controller.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface GamesRepository extends CrudRepository<Games, Integer> {

    List<Games> findByGameIDAndRoomIDAndPlayerIDAndPlayerInitialRoleAndPassTurnOrderByCreateDate(UUID gameID, UUID roomID, String playerID, String playerInitialRole, Boolean passTurn);
    List<Games> findByGameIDAndRoomIDAndPlayerInitialRole(UUID gameID, UUID roomID, String playerInitialRole);
    List<Games> findByGameIDAndRoomIDAndPlayerID(UUID gameID, UUID roomID, String playerID);
    List<Games> findByGameIDAndRoomIDAndPlayerIDAndVoted(UUID gameID, UUID roomID, String playerID, Boolean voted);
    List<Games> findByGameIDAndRoomID(UUID gameID, UUID roomID);
}