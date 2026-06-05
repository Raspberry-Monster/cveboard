package dev.raspberrykan.cveboard.controllers;

import dev.raspberrykan.cveboard.models.dto.requests.DependencyRequest;
import dev.raspberrykan.cveboard.models.dto.responses.*;
import dev.raspberrykan.cveboard.services.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dependencies")
@Slf4j
class DependenciesController {
    private final ProjectService projectService;

    public DependenciesController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ComponentResponse>> createDependency(@RequestBody DependencyRequest request) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(projectService.createDependency(request.name(), request.description())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ComponentResponse>>> listDependencies() {
        try {
            return ResponseEntity.ok(ApiResponse.ok(projectService.listDependencies()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    @DeleteMapping("/{dependencyId}")
    public ResponseEntity<ApiResponse<Void>> deleteDependency(@PathVariable Long dependencyId) {
        try {
            projectService.deleteDependency(dependencyId);
            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }
}
