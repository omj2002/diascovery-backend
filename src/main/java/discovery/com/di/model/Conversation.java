package discovery.com.di.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
@Data
public class Conversation {

    @Id
    private String id;

    @Column(name = "buyer_id")
    private String buyerId;

    @Column(name = "seller_id")
    private String sellerId;

    @Column(name = "last_message")
    private String lastMessage;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}