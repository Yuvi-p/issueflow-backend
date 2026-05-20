package com.att.tdp.issueflow.repositories;

import com.att.tdp.issueflow.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    // bypassing sql restrictions that have been deleted in soft delete
    @Query(value = "SELECT * FROM projects WHERE is_deleted = true", nativeQuery = true)
    List<Project> findDeletedProjects();
}