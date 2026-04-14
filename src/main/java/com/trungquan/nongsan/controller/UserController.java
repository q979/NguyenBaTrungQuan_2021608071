package com.trungquan.nongsan.controller;

import com.trungquan.nongsan.entity.User;
import com.trungquan.nongsan.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserController {

    UserService userService;

    // Message mapping is used to map Websocket messages to a specific methods in a controller
    // When a message is sent to the WebSocket endpoint with the destination /user.addUser, this method will be invoked.
    // It is similar to @RequestMapping in Spring MVC but for WebSocket messaging.

    // This annotation indicates that the return value of the method should be sent to the specified destination.
    // After processing the message, the resulting User object will be sent to the /user/topic destination.
    // Other WebSocket subscribers listening to this destination will receive the message.

    // This annotation is used to bind the payload of the WebSocket message to the method parameter.
    // The incoming message payload will be converted to a User object and passed as an argument to the method.
    @MessageMapping("/user.addUser")
    @SendTo("/user/topic")
    public User addUser(@Payload User user){
        userService.saveUserForWebSocket(user);
        return user;
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/user/topic")
    public User connectUser(@Payload User user){
        userService.disconnectUser(user);
        return user;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> findConnectedUser(){
        return ResponseEntity.ok().body(userService.findConnedUsers());
    }

}
