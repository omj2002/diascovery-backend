package discovery.com.di.controller;

import discovery.com.di.model.User;
import discovery.com.di.model.Candidate;
import discovery.com.di.repository.UserRepository;
import discovery.com.di.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> getUserById(@PathVariable String id) {
        Map<String, String> response = new HashMap<>();
        
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            response.put("id", user.getId());
            response.put("name", user.getName());
            return ResponseEntity.ok(response);
        }
        
        Candidate candidate = candidateRepository.findById(id).orElse(null);
        if (candidate != null) {
            response.put("id", candidate.getId());
            response.put("name", candidate.getName());
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.notFound().build();
    }
}
