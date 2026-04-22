package discovery.com.di.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "project")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    private String id;

    private String title;
    private String description;

    @Column(name = "image_url")
    private String imageURL;

    private String projectType;

    @Column(length = 1000)
    private String requiredRoles;

    @Column(length = 1000)
    private String requiredSkills;

    private String status; // "Open", "Closed"

    @Column(name = "team_size")
    private Integer teamSize;

    @Column(name = "owner_id")
    private String ownerId; 

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    @JsonIgnore
    private Candidate candidate;
}