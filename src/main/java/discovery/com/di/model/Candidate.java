package discovery.com.di.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Candidate {

    @Id
    private String id;

    private String name;
    private String role;
    private int experience;
    private String location;
    private double priceFrom;
    
    @Column(length = 2000)
    private String bio;

    private Double rating;
    private Integer reviewCount;
    @Column(columnDefinition = "LONGTEXT")
    private String profileImage;

    @Column(length = 1000)
    private String goals;

    private String availability; // e.g., "Full-time", "Part-time", "20 hrs/week"

    @Column(length = 1000)
    private String interests;

    @Column(name = "user_id")
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private SubCategory roleEntity;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CandidateSkill> skills;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Project> projects;
}