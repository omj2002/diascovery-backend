package discovery.com.di.controller;

import discovery.com.di.model.Project;
import discovery.com.di.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Project APIs", description = "Operations related to project postings and requirements")
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "Create a new project posting")
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        return ResponseEntity.ok(projectService.createProject(project));
    }

    @Operation(summary = "Get all project postings")
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @Operation(summary = "Get projects by owner ID")
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Project>> getProjectsByOwnerId(@PathVariable String ownerId) {
        return ResponseEntity.ok(projectService.getProjectsByOwnerId(ownerId));
    }

    @Operation(summary = "Get project details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable String id) {
        return projectService.getProjectById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update an existing project posting")
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable String id, @RequestBody Project projectDetails) {
        try {
            return ResponseEntity.ok(projectService.updateProject(id, projectDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a project posting")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
