package com.att.tdp.issueflow.repositories;

import com.att.tdp.issueflow.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    List<Ticket> findByProjectId(Long projectId);

    // Retrieving deleted tickets by project
    @Query(value = "SELECT * FROM tickets WHERE is_deleted = true AND project_id = :projectId", nativeQuery = true)
    List<Ticket> findDeletedByProjectId(@Param("projectId") Long projectId);
}