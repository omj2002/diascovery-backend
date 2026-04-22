package discovery.com.di.controller;

import discovery.com.di.model.Category;
import discovery.com.di.repository.CategoryRepository;

import io.swagger.v3.oas.annotations.*;

import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Category APIs")
public class CategoryController {

    private final CategoryRepository repo;

    @Operation(summary = "Get all categories")
    @GetMapping
    public List<Category> getAll() {
        return repo.findAll();
    }

    @Operation(summary = "Get category by ID")
    @GetMapping("/{id}")
    public Category getById(@PathVariable String id) {
        return repo.findById(id).orElseThrow();
    }
}