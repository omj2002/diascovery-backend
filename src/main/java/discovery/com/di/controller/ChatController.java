package discovery.com.di.controller;

import discovery.com.di.model.*;
import discovery.com.di.service.ChatService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ChatController {

    private final ChatService service;

    // Create conversation
    @PostMapping("/conversations")
    public Conversation createConversation(@RequestParam String buyerId,
                                           @RequestParam String sellerId) {
        return service.createOrGetConversation(buyerId, sellerId);
    }

    // Get conversations
    @GetMapping("/conversations")
    public List<Conversation> getConversations(@RequestParam String userId) {
        return service.getUserConversations(userId);
    }

    // Get messages
    @GetMapping("/conversations/{id}/messages")
    public Page<Message> getMessages(@PathVariable String id, Pageable pageable) {
        return service.getMessages(id, pageable);
    }

    // Send message
    @PostMapping("/messages")
    public Message sendMessage(@RequestBody Message msg) {
        return service.sendMessage(
                msg.getConversationId(),
                msg.getSenderId(),
                msg.getContent()
        );
    }
}