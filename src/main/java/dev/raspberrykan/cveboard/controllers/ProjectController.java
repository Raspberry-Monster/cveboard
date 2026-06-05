package dev.raspberrykan.cveboard.controllers;

import dev.raspberrykan.cveboard.models.dto.requests.ProjectDependencyRequest;
import dev.raspberrykan.cveboard.models.dto.requests.ProjectRequest;
import dev.raspberrykan.cveboard.models.dto.responses.*;
import dev.raspberrykan.cveboard.security.AuthenticatedUser;
import dev.raspberrykan.cveboard.services.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
@Slf4j
class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(@RequestBody ProjectRequest request, Authentication authentication) {
        UUID userId = currentUserId(authentication);
        try {
            return ResponseEntity.ok(ApiResponse.ok(projectService.createProject(userId, request.name(), request.description())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ListProjectResponse>>> listProjects() {
        return ResponseEntity.ok(ApiResponse.ok(projectService.listProjects()));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(@PathVariable UUID projectId, @RequestBody ProjectRequest request) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(projectService.updateProject(projectId, request.name(), request.description())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(@PathVariable UUID projectId) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(projectService.getProject(projectId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    @PostMapping("/{projectId}/dependencies")
    public ResponseEntity<ApiResponse<DependencyResponse>> addProjectDependency(@PathVariable UUID projectId, @RequestBody ProjectDependencyRequest request) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(projectService.addDependency(projectId, request.id(), request.version())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    @PutMapping("/{projectId}/dependencies")
    public ResponseEntity<ApiResponse<DependencyResponse>> updateProjectDependency(@PathVariable UUID projectId, @RequestBody ProjectDependencyRequest request) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(projectService.updateDependency(projectId, request.id(), request.version())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    @DeleteMapping("/{projectId}/dependencies/{dependencyId}")
    public ResponseEntity<ApiResponse<Void>> removeProjectDependency(@PathVariable UUID projectId, @PathVariable Long dependencyId) {
        try {
            projectService.removeDependency(projectId, dependencyId);
            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable UUID projectId) {
        try {
            projectService.deleteProject(projectId);
            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    private UUID currentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser principal) {
            return principal.getId();
        }
        return null;
    }
}
