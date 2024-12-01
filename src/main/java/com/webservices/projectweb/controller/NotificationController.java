package com.webservices.projectweb.controller;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    @MessageMapping("/send")  // WebSocket endpoint for incoming messages
    @SendTo("/topic/notifications")  // Destination for broadcasting to subscribers
    public String sendNotification(String message) {
        return message;  // Broadcast the incoming message
    }
}
