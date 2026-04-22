package discovery.com.di.controller;

import discovery.com.di.model.Candidate;
import discovery.com.di.service.CandidateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Candidate APIs", description = "Operations related to candidates")
public class CandidateController {

    private final CandidateService service;

    @Operation(summary = "Get all candidates with filters")
    @GetMapping
    public Page<Candidate> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String roleId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minExp,
            @RequestParam(required = false) Integer maxExp,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "rating") String sort,
            Pageable pageable) {

        return service.getCandidates(search, category, roleId, location, minExp, maxExp, maxPrice, sort, pageable);
    }

    @Operation(summary = "Get top rated candidates")
    @GetMapping("/top-rated")
    public List<Candidate> topRated() {
        return service.getTopRated();
    }

    @Operation(summary = "Get candidate by ID")
    @GetMapping("/{id}")
    public Candidate getById(@PathVariable String id) {
        return service.getById(id);
    }
}