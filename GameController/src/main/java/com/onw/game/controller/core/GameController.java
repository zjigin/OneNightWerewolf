package com.onw.game.controller.core;

import com.onw.game.controller.dao.Roles;
import com.onw.game.controller.dao.RolesRepository;
import com.onw.game.controller.mq.Sender;
import com.onw.game.controller.shared.GameControllerUtil;
import com.onw.game.controller.shared.GameStartDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class GameController {

    @Autowired
    private Sender sender;

    @Autowired
    private RolesRepository rolesRepository;

    @Value("${mq.role_controller_binding_key}")
    private String roleControllerBindingKey;

    @Value("${mq.front_end_listen_key_prefix}")
    private String frontEndListenKeyPrefix;

    @Value("${game.msg.wakeup}")
    private String wakeUpMsg;

    @Value("${game.msg.timeout}")
    private String timeOut;

    @Value("${game.msg.openeyes}")
    private String openEyesMsg;

    @Value("${game.msg.vote}")
    private String voteMsg;

    @Value("${game.msg.resolve}")
    private String resolve;

    private Map<Integer, List<String>> gamePhaseOrder;

    @PostConstruct
    public void init() {
        gamePhaseOrder = new TreeMap<>();
        for (Roles roles :rolesRepository.findAll()) {
            Integer phase = roles.getPhase();
            if(phase != -1) {
                if (!gamePhaseOrder.containsKey(phase)) {
                    List<String> rolesList = new ArrayList<>();
                    gamePhaseOrder.put(phase, rolesList);
                }
                gamePhaseOrder.get(phase).add(roles.getRoleName());
            }
        }
    }

    @Async
    public void startGame(GameSetting gameSetting) {
        System.out.println("Start Game for room " + GameControllerUtil.getGson().toJson(gameSetting));

        if(gameSetting.getRoomID() == null) {
            gameSetting = initialTestGameSetting();
        }

        String roomID = gameSetting.getRoomID();

        Map<UUID, String> initialStatus = shuffle(gameSetting.getRoles(), gameSetting.getPlayerTokens());
        Map<String, List<UUID>> reversedInitialStatus = reverseInitialStatusKeyValue(initialStatus);

        // Send initialStatus to RoleController via MQ.
        System.out.println("Send initial status to role controller via MQ...");
        Map<String, Object> initialStatusMap = convertInitialStatusToDTOWithRoomID(initialStatus, roomID, gameSetting);
        System.out.println(GameControllerUtil.convertToJSON(initialStatusMap));
        sender.send(roleControllerBindingKey, GameControllerUtil.convertToJSON(initialStatusMap));

        try {
            Thread.sleep(gameSetting.getPrepareTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        GameStartDTO gameStartDTO = new GameStartDTO();
        gameStartDTO.setPlayerTokenList(gameSetting.getPlayerTokens());
        gameStartDTO.setRoomID(roomID);
        System.out.println("Send all player's tokens and his/her corresponding role to a player via MQ...");
        for(Map.Entry<UUID, String> entry : initialStatus.entrySet()) {
            // Send all player's tokens and his/her corresponding role to a player.
            gameStartDTO.setRole(entry.getValue());
            System.out.println(String.format("%s.%s.%s", frontEndListenKeyPrefix, roomID, entry.getKey()));
            System.out.println(GameControllerUtil.convertToJSON(gameStartDTO));
            sender.send(String.format("%s.%s.%s", frontEndListenKeyPrefix, roomID, entry.getKey()), GameControllerUtil.convertToJSON(gameStartDTO));
        }

        try {
            Thread.sleep(gameSetting.getShortStunTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Send typical player wake up via MQ...");
        for(Map.Entry<Integer, List<String>> rolesEntry: gamePhaseOrder.entrySet()) {
            // Broadcast current phase to all players.
            for(Map.Entry<UUID, String> entry : initialStatus.entrySet()) {
                // Send all player's tokens and his/her corresponding role to a player.
                sender.send(String.format("%s.%s.%s", frontEndListenKeyPrefix, roomID, entry.getKey()), "Phase " + rolesEntry.getKey());
            }

            for(String role : rolesEntry.getValue()) {
                System.out.println("Wake up via MQ: " + role);
                if (reversedInitialStatus.containsKey(role)) {
                    List<UUID> wakeUpPlayerTokens = reversedInitialStatus.get(role);
                    for (UUID playerToken : wakeUpPlayerTokens) {
                        System.out.println(String.format("%s.%s.%s", frontEndListenKeyPrefix, roomID, playerToken) + ": " + wakeUpMsg);
                        sender.send(String.format("%s.%s.%s", frontEndListenKeyPrefix, roomID, playerToken), wakeUpMsg);
                    }
                }
            }

            try {
                Thread.sleep(gameSetting.getPhaseTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(String role : rolesEntry.getValue()) {
                System.out.println("Time out via MQ: " + role);
                if (reversedInitialStatus.containsKey(role)) {
                    List<UUID> wakeUpPlayerTokens = reversedInitialStatus.get(role);
                    for (UUID playerToken : wakeUpPlayerTokens) {
                        System.out.println(String.format("%s.%s.%s", frontEndListenKeyPrefix, roomID, playerToken) + ": " + timeOut);
                        sender.send(String.format("%s.%s.%s", frontEndListenKeyPrefix, roomID, playerToken), timeOut);
                    }
                }
            }
        }

        System.out.println("Send typical player open eyes via MQ...");
        for(Map.Entry<UUID, String> entry : initialStatus.entrySet()) {
            // Send all players open eyes message.
            System.out.println(String.format("%s.%s.%s", frontEndListenKeyPrefix, roomID, entry.getKey()));
            sender.send(String.format("%s.%s.%s", frontEndListenKeyPrefix, roomID, entry.getKey()), openEyesMsg);
        }

        try {
            Thread.sleep(gameSetting.getDiscussTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Send typical player vote via MQ...");
        for(Map.Entry<UUID, String> entry : initialStatus.entrySet()) {
            // Send all player's vote message.
            System.out.println(String.format("%s.%s.%s", frontEndListenKeyPrefix, roomID, entry.getKey()));
            sender.send(String.format("%s.%s.%s", frontEndListenKeyPrefix, roomID, entry.getKey()), voteMsg);
        }

        try {
            Thread.sleep(gameSetting.getVoteTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Send typical player resolve via MQ...");
        for(Map.Entry<UUID, String> entry : initialStatus.entrySet()) {
            // Send all player's vote message.
            System.out.println(String.format("%s.%s.%s", frontEndListenKeyPrefix, roomID, entry.getKey()));
            sender.send(String.format("%s.%s.%s", frontEndListenKeyPrefix, roomID, entry.getKey()), resolve);
        }
    }

    private GameSetting initialTestGameSetting() {
        GameSetting setting = new GameSetting();
        setting.setRoomID("321e4567-e89b-12d3-a456-426655440222");
        List<String> roles = new ArrayList<>();
        roles.add("DRUNK");
        roles.add("TROUBLEMAKER");
        roles.add("WEREWOLF");
        roles.add("WEREWOLF");
        roles.add("SEER");
        roles.add("ROBBER");
        roles.add("VILLAGER");
        roles.add("VILLAGER");
        roles.add("TANNER");
        roles.add("HUNTER");
        setting.setRoles(roles);

        // Players' size will always be roles' size - 3.
        List<UUID> playerTokens = new ArrayList<>();
        for(int i=0;i<roles.size() - 3;i++) {
            playerTokens.add(UUID.fromString("123e4567-e89b-12d3-a456-4266554400" + String.format("%02d", i)));
        }
        setting.setPlayerTokens(playerTokens);
        setting.setPrepareTime(3500L);
        setting.setShortStunTime(1000L);
        setting.setPhaseTime(1000L);
        setting.setDiscussTime(1000L);
        setting.setVoteTime(1000L);

        System.out.println("Initial Test GameSetting:");
        System.out.println(GameControllerUtil.convertToJSON(setting));

        return setting;
    }

    private Map<UUID, String> shuffle(List<String> roles, List<UUID> playerTokens) {
        Map<UUID, String> result = new HashMap<>();
//        Collections.shuffle(playerTokens);
        Collections.shuffle(roles);
        for(int i=0;i<playerTokens.size();i++) {
            result.put(playerTokens.get(i), roles.get(i));
        }
        return result;
    }

    private Map<String, List<UUID>> reverseInitialStatusKeyValue(Map<UUID, String> initialStatus) {
        Map<String, List<UUID>> result = new HashMap<>();
        for(Map.Entry<UUID, String> entry : initialStatus.entrySet()) {
            if(result.containsKey(entry.getValue())) {
                List<UUID> playerTokens = result.get(entry.getValue());
                playerTokens.add(entry.getKey());
                result.put(entry.getValue(), playerTokens);
            } else {
                List<UUID> playerTokens = new ArrayList<>();
                playerTokens.add(entry.getKey());
                result.put(entry.getValue(), playerTokens);
            }
        }
        return result;
    }

    private Map<String, Object> convertInitialStatusToDTOWithRoomID(Map<UUID, String> initialStatus, String roomID, GameSetting gameSetting) {
        List<String> unusedRoles = new ArrayList<>(gameSetting.getRoles());
        Map<String, Object> result = new HashMap<>();
        for(Map.Entry<UUID, String> entry : initialStatus.entrySet()) {
            result.put(entry.getKey().toString(), entry.getValue());
            unusedRoles.remove(entry.getValue());
        }
        result.put("roomID", roomID);
        Collections.shuffle(unusedRoles);
        result.put("unusedRoles", unusedRoles);
        return result;
    }
}
