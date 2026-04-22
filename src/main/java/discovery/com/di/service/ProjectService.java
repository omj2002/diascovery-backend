package discovery.com.di.service;

import discovery.com.di.model.Project;
import discovery.com.di.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public Project createProject(Project project) {
        if (project.getId() == null) {
            project.setId(UUID.randomUUID().toString());
        }
        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getProjectsByOwnerId(String ownerId) {
        return projectRepository.findByOwnerId(ownerId);
    }

    public Optional<Project> getProjectById(String id) {
        return projectRepository.findById(id);
    }

    public Project updateProject(String id, Project updatedProject) {
        return projectRepository.findById(id).map(project -> {
            project.setTitle(updatedProject.getTitle());
            project.setDescription(updatedProject.getDescription());
            project.setImageURL(updatedProject.getImageURL());
            project.setProjectType(updatedProject.getProjectType());
            project.setRequiredRoles(updatedProject.getRequiredRoles());
            project.setRequiredSkills(updatedProject.getRequiredSkills());
            project.setStatus(updatedProject.getStatus());
            project.setTeamSize(updatedProject.getTeamSize());
            project.setOwnerId(updatedProject.getOwnerId());
            return projectRepository.save(project);
        }).orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public void deleteProject(String id) {
        projectRepository.deleteById(id);
    }
}
