package com.webservices.projectweb.controller;

import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import com.webservices.projectweb.model.Notification;
import com.webservices.projectweb.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;
   /*
    @MessageMapping("/send")  // WebSocket endpoint for incoming messages
    @SendTo("/topic/notifications")  // Broadcast messages to the topic
    public String sendNotification(String message) {
        System.out.println("Received message: " + message); // Log incoming messages for debugging
        return message; // Broadcast the same message to subscribers
    }*/

    @MessageMapping("/send")  // WebSocket endpoint for incoming messages
    @SendTo("/topic/notifications")  // Destination for broadcasting to subscribers
    public String sendNotification(String message) {
        // Save notification to the database
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        notificationRepository.save(notification);

        // Return the message to be sent to subscribers
        return message;
    }

    @GetMapping("/notifications")
    @ResponseBody
    public List<Notification> getNotifications() {
        return notificationRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
    }
}