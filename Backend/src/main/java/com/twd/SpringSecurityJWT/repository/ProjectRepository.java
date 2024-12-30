package com.twd.SpringSecurityJWT.repository;

import com.twd.SpringSecurityJWT.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    // Existing query
    List<Project> findByBudgetGreaterThan(int budget);

    // Query to retrieve overdue projects (estimatedEndtime before a specific date)
    List<Project> findByEstimatedEndtimeBefore(LocalDateTime date);
}
