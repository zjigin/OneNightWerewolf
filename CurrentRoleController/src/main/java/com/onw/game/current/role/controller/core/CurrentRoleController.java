package com.onw.game.current.role.controller.core;

import com.google.gson.reflect.TypeToken;
import com.onw.game.current.role.controller.dao.Games;
import com.onw.game.current.role.controller.dao.GamesRepository;
import com.onw.game.current.role.controller.mq.Sender;
import com.onw.game.current.role.controller.shared.GameControllerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CurrentRoleController {

    @Autowired
    private GamesRepository gamesRepository;

    @Autowired
    private Sender sender;

    @Value("${mq.front_end_listen_key_prefix}")
    private String frontEndListenKeyPrefix;

    @Async
    public void createGame(String message) {

        UUID gameID = UUID.randomUUID();

        Type gameStartInfoType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> gameStartInfo = GameControllerUtil.getGson().fromJson(message, gameStartInfoType);

        String roomID = gameStartInfo.get("roomID").toString();
        gameStartInfo.remove("roomID");

        @SuppressWarnings("unchecked")
        List<String> unusedRoles = (List<String>)gameStartInfo.get("unusedRoles");
        gameStartInfo.remove("unusedRoles");
        for(int i=0;i<unusedRoles.size();i++) {
            Games games = new Games();
            games.setGameID(gameID);
            games.setRoomID(UUID.fromString(roomID));
            games.setPlayerID((String.format("%d", i)));
            games.setPlayerInitialRole(unusedRoles.get(i));
            games.setPlayerCurrentRole(unusedRoles.get(i));
            games.setPassTurn(false);
            games.setVoteTo((String.format("%d", i)));
            games.setVoted(false);
            System.out.println(GameControllerUtil.getGson().toJson(games));
            gamesRepository.save(games);
        }

        Map<String, String> gameIDMap = new HashMap<>();
        gameIDMap.put("gameID", gameID.toString());

        for(Map.Entry<String, Object> playerRoleEntry : gameStartInfo.entrySet()) {
            Games games = new Games();
            games.setGameID(gameID);
            games.setRoomID(UUID.fromString(roomID));
            games.setPlayerID((playerRoleEntry.getKey()));
            games.setPlayerInitialRole(playerRoleEntry.getValue().toString());
            games.setPlayerCurrentRole(playerRoleEntry.getValue().toString());
            games.setPassTurn(false);
            games.setVoteTo(playerRoleEntry.getKey());
            games.setVoted(false);
            System.out.println(GameControllerUtil.getGson().toJson(games));
            gamesRepository.save(games);

            // Send gameID to front end.
            sender.send(String.format("%s.%s.%s", frontEndListenKeyPrefix, roomID, games.getPlayerID()), GameControllerUtil.getGson().toJson(gameIDMap));
        }
    }

}
