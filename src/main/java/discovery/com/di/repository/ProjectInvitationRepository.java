package discovery.com.di.repository;

import discovery.com.di.model.ProjectInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectInvitationRepository extends JpaRepository<ProjectInvitation, String> {
    List<ProjectInvitation> findByProjectId(String projectId);
    List<ProjectInvitation> findByCandidateId(String candidateId);
    List<ProjectInvitation> findBySenderId(String senderId);
}
