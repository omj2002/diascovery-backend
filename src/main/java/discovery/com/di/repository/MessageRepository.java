package discovery.com.di.repository;

import discovery.com.di.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, String> {

    Page<Message> findByConversationIdOrderByCreatedAtAsc(String conversationId, Pageable pageable);
}