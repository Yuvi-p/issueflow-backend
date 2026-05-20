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

    @Query(value = "SELECT * FROM tickets WHERE project_id = :projectId AND is_deleted = true", nativeQuery = true)
    List<Ticket> findSoftDeletedTickets(@Param("projectId") Long projectId);
}