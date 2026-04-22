package discovery.com.di.service;

import discovery.com.di.model.*;
import discovery.com.di.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationRepository conversationRepo;
    private final MessageRepository messageRepo;

    // Create or get conversation
    public Conversation createOrGetConversation(String buyerId, String sellerId) {

        Optional<Conversation> existing =
                conversationRepo.findByBuyerIdAndSellerId(buyerId, sellerId);

        if (existing.isPresent()) return existing.get();

        Conversation convo = new Conversation();
        convo.setId(UUID.randomUUID().toString());
        convo.setBuyerId(buyerId);
        convo.setSellerId(sellerId);
        convo.setCreatedAt(LocalDateTime.now());

        return conversationRepo.save(convo);
    }

    // Get all conversations
    public List<Conversation> getUserConversations(String userId) {
        return conversationRepo.findByBuyerIdOrSellerId(userId, userId);
    }

    // Get messages
    public Page<Message> getMessages(String conversationId, Pageable pageable) {
        return messageRepo.findByConversationIdOrderByCreatedAtAsc(conversationId, pageable);
    }

    // Send message
    public Message sendMessage(String conversationId, String senderId, String content) {

        Message msg = new Message();
        msg.setId(UUID.randomUUID().toString());
        msg.setConversationId(conversationId);
        msg.setSenderId(senderId);
        msg.setContent(content);
        msg.setRead(false);
        msg.setCreatedAt(LocalDateTime.now());

        Message saved = messageRepo.save(msg);

        // update conversation
        Conversation convo = conversationRepo.findById(conversationId).orElseThrow();
        convo.setLastMessage(content);
        convo.setLastMessageAt(LocalDateTime.now());
        conversationRepo.save(convo);

        return saved;
    }
}