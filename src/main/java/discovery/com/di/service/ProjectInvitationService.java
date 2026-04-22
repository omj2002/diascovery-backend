package discovery.com.di.service;

import discovery.com.di.model.Candidate;
import discovery.com.di.model.Project;
import discovery.com.di.model.ProjectInvitation;
import discovery.com.di.repository.CandidateRepository;
import discovery.com.di.repository.ProjectInvitationRepository;
import discovery.com.di.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectInvitationService {

    private final ProjectInvitationRepository invitationRepo;
    private final ProjectRepository projectRepo;
    private final CandidateRepository candidateRepo;

    public ProjectInvitation sendInvite(String projectId, String candidateId, String senderId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        Candidate candidate = candidateRepo.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        ProjectInvitation invite = ProjectInvitation.builder()
                .project(project)
                .candidate(candidate)
                .senderId(senderId)
                .status("PENDING")
                .build();

        return invitationRepo.save(invite);
    }

    public ProjectInvitation updateInviteStatus(String inviteId, String status) {
        ProjectInvitation invite = invitationRepo.findById(inviteId)
                .orElseThrow(() -> new RuntimeException("Invite not found"));
        invite.setStatus(status.toUpperCase());
        return invitationRepo.save(invite);
    }

    @Transactional(readOnly = true)
    public List<ProjectInvitation> getInvitesForUser(String userId) {
        Candidate candidate = candidateRepo.findFirstByUserId(userId)
                    .orElseGet(() -> candidateRepo.findById(userId).orElse(null));
        if (candidate == null) return List.of();
        return invitationRepo.findByCandidateId(candidate.getId());
    }

    @Transactional(readOnly = true)
    public List<ProjectInvitation> getInvitesForProject(String projectId) {
        return invitationRepo.findByProjectId(projectId);
    }
}
