package com.onw.edge.api.controller;

import com.onw.edge.api.shared.IncomingDTO;
import org.springframework.web.bind.annotation.*;

@RestController
public class EdgeController {

    @RequestMapping("/create/room")
    @PostMapping
    public String createRoom(@RequestBody IncomingDTO incomingDTO) {
        System.out.println(incomingDTO);

        // TODO: send to pre-game receiver.
        return "Room Created";
    }

    @RequestMapping("/start/game")
    @PostMapping
    public String startGame(@RequestBody IncomingDTO incomingDTO) {
        System.out.println(incomingDTO);

        // TODO: send to pre-game receiver.
        return "Game Started";
    }

    @RequestMapping("/action/{role}")
    @PostMapping
    public String action(@PathVariable("role") String role, @RequestBody IncomingDTO incomingDTO) {
        System.out.println(incomingDTO);
        // TODO: ask anther service for answer.
        return role;
    }

}
