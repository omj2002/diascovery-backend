package discovery.com.di.dto;

import lombok.Data;

@Data
public class CandidateProfile {
    private String roleTitle;
    private String subcategoryId;
    private int experience;
    private String location;
    private double priceFrom;
    private String skills;
    private String bio;
    private String goals;
    private String availability;
    private String interests;
    private String profileImage;
}