package discovery.com.di.service;

import discovery.com.di.dto.*;
import discovery.com.di.model.*;
import discovery.com.di.repository.CandidateSkillRepository;
import discovery.com.di.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final CandidateSkillRepository candidateSkillRepository;

    private final PasswordEncoder passwordEncoder;
    private final discovery.com.di.security.JwtUtils jwtUtils;

    // 🔐 REGISTER
    @Transactional
    public String register(RegisterRequest request) {

        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password is required");
        }
        if (request.getRole() == null || request.getRole().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "role is required");
        }

        // check email exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        // create user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        // Ensure we have the persisted User id before creating Candidate
        userRepository.saveAndFlush(user);
        User savedUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User persistence failed"));

        // if seller → create candidate
        if ("seller".equalsIgnoreCase(request.getRole())) {

            CandidateProfile cp = request.getCandidateProfile();
            if (cp == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "candidateProfile is required for role=seller");
            }
            if (cp.getRoleTitle() == null || cp.getRoleTitle().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "candidateProfile.roleTitle is required");
            }
            if (cp.getSubcategoryId() == null || cp.getSubcategoryId().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "candidateProfile.subcategoryId is required");
            }
            if (cp.getLocation() == null || cp.getLocation().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "candidateProfile.location is required");
            }
            if (cp.getSkills() == null || cp.getSkills().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "candidateProfile.skills is required");
            }

            SubCategory subCategory = subCategoryRepository.findById(cp.getSubcategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subcategory not found"));

            Candidate candidate = new Candidate();
            candidate.setId(UUID.randomUUID().toString());
            candidate.setName(user.getName());
            candidate.setRole(cp.getRoleTitle());
            candidate.setExperience(cp.getExperience());
            candidate.setLocation(cp.getLocation());
            candidate.setPriceFrom(cp.getPriceFrom());
            candidate.setBio(cp.getBio());
            candidate.setRoleEntity(subCategory);
            candidate.setUserId(savedUser.getId());
            candidate.setProfileImage(cp.getProfileImage());

            candidateRepository.save(candidate);

            // save skills
            String[] skills = cp.getSkills().split(",");
            for (String skill : skills) {
                String s = skill == null ? "" : skill.trim();
                if (s.isEmpty()) continue;
                CandidateSkill cs = new CandidateSkill();
                cs.setCandidate(candidate);
                cs.setSkill(s);
                candidateSkillRepository.save(cs);
            }
        }

        return "User registered successfully";
    }

    // 🔐 LOGIN
    public AuthResponse login(AuthRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new org.springframework.security.authentication.BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new org.springframework.security.authentication.BadCredentialsException("Invalid email or password");
        }

        // generate JWT token
        String token = jwtUtils.generateJwtToken(user);

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }
}