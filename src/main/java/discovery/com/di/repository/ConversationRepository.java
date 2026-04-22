package discovery.com.di.repository;

import discovery.com.di.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, String> {

    List<Conversation> findByBuyerIdOrSellerId(String buyerId, String sellerId);

    Optional<Conversation> findByBuyerIdAndSellerId(String buyerId, String sellerId);
}