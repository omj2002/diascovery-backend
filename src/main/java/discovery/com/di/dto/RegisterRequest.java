package discovery.com.di.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String role; // buyer / seller
    private CandidateProfile candidateProfile;
}