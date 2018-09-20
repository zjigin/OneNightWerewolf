package com.onw.game.current.role.controller.shared;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GameControllerUtil {

    private static Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    public static String convertToJSON(Object object) {
        return gson.toJson(object);
    }

    public static Gson getGson() {
        return gson;
    }

}
