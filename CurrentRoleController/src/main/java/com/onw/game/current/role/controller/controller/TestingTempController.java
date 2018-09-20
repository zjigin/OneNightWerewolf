package com.onw.game.current.role.controller.controller;

import com.onw.game.current.role.controller.mq.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestingTempController {

    private final Sender sender;

    @Autowired
    public TestingTempController(Sender sender) {
        this.sender = sender;
    }

    @RequestMapping(value = "/startGame", method = { RequestMethod.GET})
    String startGame() {
        sender.send("onw.game.controller.start", "{}");
        return "{}";
    }

    @RequestMapping(value = "/startGame", method = { RequestMethod.POST})
    String startGame(@RequestBody String input) {
        sender.send("onw.game.controller.start", input);
        return input;
    }
}
