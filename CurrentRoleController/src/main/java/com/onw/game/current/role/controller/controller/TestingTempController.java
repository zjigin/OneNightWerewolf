package com.onw.game.current.role.controller.controller;

import com.onw.game.current.role.controller.mq.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TestingTempController {

    @Autowired
    Sender sender;

    @RequestMapping(value = "/startGame", method = { RequestMethod.GET})
    String startGame(HttpServletRequest request) {
        sender.send("onw.game.controller.start", "{}");
        return "{}";
    }

    @RequestMapping(value = "/startGame", method = { RequestMethod.POST})
    String startGame(HttpServletRequest request, @RequestBody String input) {
        sender.send("onw.game.controller.start", input);
        return input;
    }
}
