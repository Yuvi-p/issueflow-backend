package com.att.tdp.issueflow.services;

import com.att.tdp.issueflow.entities.Project;
import com.att.tdp.issueflow.entities.User;
import com.att.tdp.issueflow.repositories.ProjectRepository;
import com.att.tdp.issueflow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
    }

    @Transactional
    public Project createProject(String name, String description, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + ownerId));

        Project project = Project.builder()
                .name(name)
                .description(description)
                .owner(owner)
                .isDeleted(false)
                .build();

        return projectRepository.save(project);
    }

    @Transactional
    public Project updateProject(Long id, String name, String description) {
        Project project = getProjectById(id);
        if (name != null) project.setName(name);
        if (description != null) project.setDescription(description);
        return projectRepository.save(project);
    }

    @Transactional
    public void softDeleteProject(Long id) {
        Project project = getProjectById(id);
        project.setDeleted(true); // soft delete
        projectRepository.save(project);
    }
}