package discovery.com.di.controller;

import discovery.com.di.model.ProjectInvitation;
import discovery.com.di.service.ProjectInvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Project Invitation APIs", description = "Operations related to hiring and inviting candidates")
public class ProjectInvitationController {

    private final ProjectInvitationService invitationService;

    @Operation(summary = "Send a hire invitation to a candidate for a specific project")
    @PostMapping("/send")
    public ResponseEntity<ProjectInvitation> sendInvite(@RequestBody Map<String, String> payload) {
        String projectId = payload.get("projectId");
        String candidateId = payload.get("candidateId");
        String senderId = payload.get("senderId");
        return ResponseEntity.ok(invitationService.sendInvite(projectId, candidateId, senderId));
    }

    @Operation(summary = "Accept or Reject an invitation")
    @PutMapping("/{id}/status")
    public ResponseEntity<ProjectInvitation> updateStatus(@PathVariable String id, @RequestBody Map<String, String> payload) {
        String status = payload.get("status"); // "ACCEPTED" or "REJECTED"
        return ResponseEntity.ok(invitationService.updateInviteStatus(id, status));
    }

    @Operation(summary = "Get all invitations for a specific candidate by User ID")
    @GetMapping("/candidate/{userId}")
    public ResponseEntity<List<ProjectInvitation>> getCandidateInvites(@PathVariable String userId) {
        return ResponseEntity.ok(invitationService.getInvitesForUser(userId));
    }

    @Operation(summary = "Get all invitations for a specific project")
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ProjectInvitation>> getProjectInvites(@PathVariable String projectId) {
        return ResponseEntity.ok(invitationService.getInvitesForProject(projectId));
    }
}
