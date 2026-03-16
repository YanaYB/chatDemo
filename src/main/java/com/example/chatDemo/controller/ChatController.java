package com.example.chatDemo.controller;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
public class ChatController {
    private final OllamaChatModel chatModel;

    private final ChatMemory chatMemory;

    private final String userId = UUID.randomUUID().toString();


    @Autowired
    public ChatController(OllamaChatModel chatModel, ChatMemory chatMemory) {
        this.chatModel = chatModel;
        this.chatMemory = MessageWindowChatMemory.builder().maxMessages(100).build();
    }
    @GetMapping("/ai/generate")
    public String generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return chatModel.call(message);
    }

    @GetMapping("/ai/generateStream")
    public Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
       UserMessage userMessage = new UserMessage(message);
       chatMemory.add(userId, userMessage);

       Prompt prompt = new Prompt(chatMemory.get(userId));

       Flux<String> response = chatModel.stream(prompt).map(chank->chank.getResult().getOutput().getText());

       response.collectList().subscribe(fullResponse -> {
           AssistantMessage assistantMessage = new AssistantMessage(String.join("",fullResponse));
           chatMemory.add(userId, assistantMessage);
       });
       return response;
    }
}
