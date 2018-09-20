package com.onw.game.current.role.controller.controller;

import com.onw.game.current.role.controller.dao.Games;
import com.onw.game.current.role.controller.dao.GamesRepository;
import com.onw.game.current.role.controller.shared.GameControllerUtil;
import com.onw.game.current.role.controller.shared.IncomingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class InternalController {

    private final GamesRepository gamesRepository;

    @Autowired
    public InternalController(GamesRepository gamesRepository) {
        this.gamesRepository = gamesRepository;
    }

    @RequestMapping("/action/{role}")
    @PostMapping
    public String actionSeer(@PathVariable("role") String rolePath, @RequestBody IncomingDTO incomingDTO) {
        String role = rolePath.toUpperCase();
        List<Games> currentPlayers = gamesRepository.findByGameIDAndRoomIDAndPlayerIDAndPlayerInitialRoleAndPassTurnOrderByCreateDate(
                incomingDTO.getGameID(), incomingDTO.getRoomID(), incomingDTO.getUserToken().toString(), role, false);

        if(currentPlayers.size() > 0) {
            Games selectedRow = currentPlayers.get(currentPlayers.size() - 1);

            selectedRow.setPassTurn(true);
            gamesRepository.save(selectedRow);

            // TODO: add new role's action here.
            switch (role) {
                case "WEREWOLF":
                    return wereWolfAction(selectedRow, incomingDTO);
                case "SEER":
                    return seerAction(selectedRow, incomingDTO);
                case "ROBBER":
                    return robberAction(selectedRow, incomingDTO);
                case "TROUBLEMAKER":
                    return troubleMakerAction(selectedRow, incomingDTO);
                case "DRUNK":
                    return drunkAction(selectedRow, incomingDTO);
                default:
                    return "{}";
            }
        } else {
            // TODO: log and return error.
            return "{}";
        }
    }

    @RequestMapping("/vote")
    @PostMapping
    public String vote(@RequestBody IncomingDTO incomingDTO) {
        try {
            List<Games> currentPlayers = gamesRepository.findByGameIDAndRoomIDAndPlayerIDAndVoted(
                    incomingDTO.getGameID(), incomingDTO.getRoomID(), incomingDTO.getUserToken().toString(), false);
            if (currentPlayers.size() > 0) {
                Games selectedRow = currentPlayers.get(currentPlayers.size() - 1);
                selectedRow.setVoteTo(incomingDTO.getRawData().get("voteTo").toString());
                selectedRow.setVoted(true);

                gamesRepository.save(selectedRow);

                Map<String, String> frontEndInfo = new HashMap<>();
                frontEndInfo.put("voteTo", selectedRow.getVoteTo());
                return GameControllerUtil.convertToJSON(frontEndInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{}";
    }

    @RequestMapping("/resolve")
    @PostMapping
    public String resolve(@RequestBody IncomingDTO incomingDTO) {
        try {
            List<Games> currentPlayers = gamesRepository.findByGameIDAndRoomID(
                    incomingDTO.getGameID(), incomingDTO.getRoomID());
            List<Map<String, String>> revealFinalRoles = new ArrayList<>();

            Map<String, List<Games>> rolePlayerTokenMap = new HashMap<>();

            Map<String, Integer> getVotedMap = new HashMap<>();
            for(Games currentPlayer : currentPlayers) {

                Map<String, String> revealFinalRole = new HashMap<>();
                if(currentPlayer.getPlayerID().length() > 1) {
                    revealFinalRole.put("playerToken", currentPlayer.getPlayerID());
                    revealFinalRole.put("vote", currentPlayer.getVoteTo());
                } else {
                    revealFinalRole.put("middleCardIndex", currentPlayer.getPlayerID());
                }
                revealFinalRole.put("initialRole", currentPlayer.getPlayerInitialRole());
                revealFinalRole.put("currentRole", currentPlayer.getPlayerCurrentRole());
                revealFinalRoles.add(revealFinalRole);

                String playerWhoGetVoted = currentPlayer.getVoteTo();
                if(playerWhoGetVoted.length() != 1) {

                    if(!getVotedMap.containsKey(playerWhoGetVoted)) {
                        getVotedMap.put(playerWhoGetVoted, 0);
                    }
                    // TODO: some roles could + 2 instead of + 1.
                    getVotedMap.put(playerWhoGetVoted, getVotedMap.get(playerWhoGetVoted) + 1);
                }

                if(!rolePlayerTokenMap.containsKey(currentPlayer.getPlayerCurrentRole())) {
                    rolePlayerTokenMap.put(currentPlayer.getPlayerCurrentRole(), new ArrayList<>());
                }
                rolePlayerTokenMap.get(currentPlayer.getPlayerCurrentRole()).add(currentPlayer);
            }
            TreeMap<Integer, List<String>> priorityVoteMap = new TreeMap<>();
            for(Map.Entry<String, Integer> entry : getVotedMap.entrySet()) {
                if(!priorityVoteMap.containsKey(entry.getValue())) {
                    priorityVoteMap.put(entry.getValue(), new ArrayList<>());
                }
                priorityVoteMap.get(entry.getValue()).add(entry.getKey());
            }

            List<String> suspiciousPlayers;
            while(true) {
                suspiciousPlayers = priorityVoteMap.lastEntry().getValue();

                if(priorityVoteMap.lastKey() == 1) {
                    // Everyone votes himself or herself.
                    suspiciousPlayers = new ArrayList<>();
                    break;
                }
                priorityVoteMap.remove(priorityVoteMap.lastKey());

                // TODO: remove the player who voted by body guard from the list.

                if(suspiciousPlayers.size() > 0) {
                    break;
                }
            }

            // If hunter is in the list, add the player he voted into the list.
            if(suspiciousPlayers.size() > 0) {
                for(Games hunter : rolePlayerTokenMap.get("HUNTER")) {
                    if(suspiciousPlayers.contains(hunter.getPlayerID())) {
                        suspiciousPlayers.add(hunter.getVoteTo());
                        System.out.println("Add hunter's vote: " + hunter.getVoteTo());
                    }
                }
            }

            List<String> winners = new ArrayList<>();
            Map<String, Object> returnMessage = new HashMap<>();
            returnMessage.put("voteTable", getVotedMap);
            returnMessage.put("reveal", revealFinalRoles);
            if(suspiciousPlayers.size() == 0) {
                // if there is at least one werewolf, then werewolf team wins.
                if(rolePlayerTokenMap.containsKey("WEREWOLF")) {
                        for (Games winner : rolePlayerTokenMap.get("WEREWOLF")) {
                            if (winner.getPlayerID().length() != 1) {
                                winners.add(winner.getPlayerID());
                            }
                        }
                    returnMessage.put("winnerGroup", "WEREWOLF");
                } else {
                    for(Map.Entry<String, List<Games>> entry : rolePlayerTokenMap.entrySet()) {
                        if(!entry.getKey().equals("TANNER") && !entry.getKey().equals("WEREWOLF")) {
                            for (Games winner : entry.getValue()) {
                                if (winner.getPlayerID().length() != 1) {
                                    winners.add(winner.getPlayerID());
                                }
                            }
                        }
                    }
                    returnMessage.put("winnerGroup", "VILLAGERS");
                }
                returnMessage.put("winners", winners);
                return GameControllerUtil.convertToJSON(returnMessage);
            } else {
                // 1. Tanner.
                List<Games> tanners = rolePlayerTokenMap.get("TANNER");
                for (Games tanner : tanners) {
                    if (suspiciousPlayers.contains(tanner.getPlayerID())) {
                        if (tanner.getPlayerID().length() != 1) {
                            winners.add(tanner.getPlayerID());
                        }
                    }
                }
                if (winners.size() != 0) {
                    returnMessage.put("winnerGroup", "TANNERS");
                    returnMessage.put("winners", winners);
                    return GameControllerUtil.convertToJSON(returnMessage);
                } else {
                    List<Games> werewolves = rolePlayerTokenMap.get("WEREWOLF");
                    boolean aWerewolfDead = false;
                    for(Games werewolf : werewolves) {

                        if (suspiciousPlayers.contains(werewolf.getPlayerID())) {
                            aWerewolfDead = true;
                            break;
                        }
                    }
                    // If a werewolf dead, villagers win.
                    if(aWerewolfDead) {
                        returnMessage.put("winnerGroup", "VILLAGERS");
                        for(Map.Entry<String, List<Games>> entry : rolePlayerTokenMap.entrySet()) {
                            if(!(entry.getKey().equals("WEREWOLF") || entry.getKey().equals("TANNER"))) {
                                for(Games winner : entry.getValue()) {
                                    if(winner.getPlayerID().length() != 1) {
                                        winners.add(winner.getPlayerID());
                                    }
                                }
                            }
                        }
                    } else {
                        returnMessage.put("winnerGroup", "WEREWOLVES");
                        for(Map.Entry<String, List<Games>> entry : rolePlayerTokenMap.entrySet()) {
                            if(entry.getKey().equals("WEREWOLF")) {
                                for(Games winner : entry.getValue()) {
                                    if(winner.getPlayerID().length() != 1) {
                                        winners.add(winner.getPlayerID());
                                    }
                                }
                            }
                        }
                    }
                    returnMessage.put("winners", winners);
                    return GameControllerUtil.convertToJSON(returnMessage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    private String wereWolfAction(Games games, IncomingDTO incomingDTO) {
        try {
            List<Games> werewolves = gamesRepository.findByGameIDAndRoomIDAndPlayerInitialRole(games.getGameID(), games.getRoomID(), games.getPlayerInitialRole());
            if (werewolves.size() == 2) {
                List<String> playerList = new ArrayList<>();
                for (Games werewolf : werewolves) {
                    playerList.add(werewolf.getPlayerID());
                    if(werewolf.getPlayerID().length() == 1) {
                        Map<String, String> frontEndInfo = new HashMap<>();
                        frontEndInfo.put("pickedCard", viewMiddleCard(games.getGameID(), games.getRoomID(), (Integer) incomingDTO.getRawData().get("index")));
                        return GameControllerUtil.convertToJSON(frontEndInfo);
                    }
                }
                Map<String, List<String>> frontEndInfo = new HashMap<>();
                frontEndInfo.put("werewolves", playerList);
                return GameControllerUtil.convertToJSON(frontEndInfo);
            } else {
                // TODO: game logic error.
                System.out.println("Error, shouldn't be here.");
                return "{}";
            }
        } catch(Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    private String seerAction(Games games, IncomingDTO incomingDTO) {
        try {
            Map<String, Object> actionMap = incomingDTO.getRawData();
            if (actionMap.containsKey("playerID")) {
                if(actionMap.get("playerID").toString().equals(games.getPlayerID())) {
                    return "{}";
                }
                Map<String, String> frontEndInfo = new HashMap<>();
                frontEndInfo.put(actionMap.get("playerID").toString(), viewPlayerCard(games.getGameID(), games.getRoomID(), actionMap.get("playerID").toString()));
                return GameControllerUtil.convertToJSON(frontEndInfo);
            } else if (actionMap.containsKey("index")) {
                @SuppressWarnings("unchecked")
                List<Integer> middleCards = (List<Integer>) actionMap.get("index");
                if (middleCards.size() == 2) {
                    Map<Integer, String> middleCardRoles = new HashMap<>();
                    for (Integer middleCardIndex : middleCards) {
                        middleCardRoles.put(middleCardIndex, viewMiddleCard(games.getGameID(), games.getRoomID(), middleCardIndex));
                    }
                    return GameControllerUtil.convertToJSON(middleCardRoles);
                } else {
                    return "{}";
                }
            } else {
                return "{}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    private String robberAction(Games games, IncomingDTO incomingDTO) {
        String robbedPlayerID = incomingDTO.getRawData().get("robbed").toString();
        try {
            if(robbedPlayerID.equals(games.getPlayerID())) {
                return "{}";
            }

            Games robbedPlayer = gamesRepository.findByGameIDAndRoomIDAndPlayerID(games.getGameID(), games.getRoomID(), robbedPlayerID).get(0);
            String robbedRole = robbedPlayer.getPlayerCurrentRole();
            robbedPlayer.setPlayerCurrentRole(games.getPlayerCurrentRole());
            gamesRepository.save(robbedPlayer);

            games.setPlayerCurrentRole(robbedRole);
            gamesRepository.save(games);

            Map<String, String> frontEndInfo = new HashMap<>();
            frontEndInfo.put("robRole", robbedRole);
            return GameControllerUtil.convertToJSON(frontEndInfo);
        } catch (Exception e) {
            return "{}";
        }
    }

    private String troubleMakerAction(Games games, IncomingDTO incomingDTO) {
        try {
            Map<String, Object> actionMap = incomingDTO.getRawData();
            @SuppressWarnings("unchecked")
            List<String> playerIDs = (List<String>) actionMap.get("players");
            if(playerIDs.size() == 2) {
                if(playerIDs.contains(games.getPlayerID())) {
                   return "{}";
                } else {
                    Games firstPlayer = gamesRepository.findByGameIDAndRoomIDAndPlayerID(games.getGameID(), games.getRoomID(), playerIDs.get(0)).get(0);
                    Games secondPlayer = gamesRepository.findByGameIDAndRoomIDAndPlayerID(games.getGameID(), games.getRoomID(), playerIDs.get(1)).get(0);
                    String firstPlayerPreviousRole = firstPlayer.getPlayerCurrentRole();
                    firstPlayer.setPlayerCurrentRole(secondPlayer.getPlayerCurrentRole());
                    secondPlayer.setPlayerCurrentRole(firstPlayerPreviousRole);
                    gamesRepository.save(firstPlayer);
                    gamesRepository.save(secondPlayer);

                    Map<String, List<String>> frontEndInfo = new HashMap<>();
                    frontEndInfo.put("swapped", playerIDs);
                    return GameControllerUtil.convertToJSON(frontEndInfo);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{}";
    }

    private String drunkAction(Games games, IncomingDTO incomingDTO) {
        try {
            Integer middleCardIndex = (Integer) incomingDTO.getRawData().get("swap");
            Games middlePlayer = gamesRepository.findByGameIDAndRoomIDAndPlayerID(games.getGameID(), games.getRoomID(), middleCardIndex.toString()).get(0);
            String middlePlayerRole = middlePlayer.getPlayerCurrentRole();
            middlePlayer.setPlayerCurrentRole(games.getPlayerCurrentRole());
            gamesRepository.save(middlePlayer);

            games.setPlayerCurrentRole(middlePlayerRole);
            gamesRepository.save(games);

            Map<String, Integer> frontEndInfo = new HashMap<>();
            frontEndInfo.put("swap", middleCardIndex);
            return GameControllerUtil.convertToJSON(frontEndInfo);
        } catch (Exception e) {
            return "{}";
        }
    }

    private String viewMiddleCard(UUID gameID, UUID roomID, Integer index) {
        List<Games> middleCard = gamesRepository.findByGameIDAndRoomIDAndPlayerID(gameID, roomID, String.format("%d", index));

        if(middleCard.size() > 0) {
            Games pickedMiddleCard = middleCard.get(0);
            return pickedMiddleCard.getPlayerCurrentRole();
        } else {
            return "UNKNOWN";
        }
    }

    private String viewPlayerCard(UUID gameID, UUID roomID, String playerID) {
        if(playerID.length() == 36) {
            List<Games> playerCard = gamesRepository.findByGameIDAndRoomIDAndPlayerID(gameID, roomID, playerID);
            if (playerCard.size() > 0) {
                Games pickedPlayerCard = playerCard.get(0);
                return pickedPlayerCard.getPlayerCurrentRole();
            }
        }
        return "UNKNOWN";
    }

}
