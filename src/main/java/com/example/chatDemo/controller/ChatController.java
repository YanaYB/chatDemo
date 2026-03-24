package com.example.chatDemo.controller;

import com.example.chatDemo.service.ChatService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;


    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public String chat(@RequestBody String message, HttpSession session) {

        String conversationId = (String) session.getAttribute("conversationId");

        if (conversationId == null) {
            conversationId = UUID.randomUUID().toString();
            session.setAttribute("conversationId", conversationId);
        }

        return chatService.ask(conversationId, message);
    }
}
