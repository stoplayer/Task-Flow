package com.twd.SpringSecurityJWT.repository;

import com.twd.SpringSecurityJWT.entity.Priority;
import com.twd.SpringSecurityJWT.entity.Status;
import com.twd.SpringSecurityJWT.entity.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Tasks, Integer> {
    List<Tasks> findByProjectId(int projectId);
    List<Tasks> findByUserId(int userId);
    List<Tasks> findByStatus(Status status);
    List<Tasks> findByPriority(Priority priority);
    Optional<Tasks> findByNameAndProjectId(String name, int projectId);

    // Find tasks with estimatedEndtime before the specified date
    List<Tasks> findByEstimatedEndtimeBefore(LocalDate date);
}
