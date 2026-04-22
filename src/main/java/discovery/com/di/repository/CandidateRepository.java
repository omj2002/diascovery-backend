package discovery.com.di.repository;

import discovery.com.di.model.Candidate;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, String>,
        JpaSpecificationExecutor<Candidate> {
    Optional<Candidate> findByUserId(String userId);
    Optional<Candidate> findFirstByUserId(String userId);
}