package com.att.tdp.issueflow.controllers;

import com.att.tdp.issueflow.entities.Project;
import com.att.tdp.issueflow.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProjectById(projectId));
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody CreateProjectRequest request) {
        Project created = projectService.createProject(request.name(), request.description(), request.ownerId());
        return ResponseEntity.ok(created);
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<Project> updateProject(
            @PathVariable Long projectId,
            @RequestBody UpdateProjectRequest request) {
        Project updated = projectService.updateProject(projectId, request.name(), request.description());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> softDeleteProject(@PathVariable Long projectId) {
        projectService.softDeleteProject(projectId);
        return ResponseEntity.ok().build();
    }

    // these records help us to receive the JSON exactly in the format that the client sends
    public record CreateProjectRequest(String name, String description, Long ownerId) {}
    public record UpdateProjectRequest(String name, String description) {}

    @GetMapping("/deleted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Project>> getDeletedProjects() {
        return ResponseEntity.ok(projectService.getSoftDeletedProjects());
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Project> restoreProject(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.restoreProject(id));
    }
}