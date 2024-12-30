package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.dto.TeamMemberRequest;
import com.twd.SpringSecurityJWT.entity.Project;
import com.twd.SpringSecurityJWT.entity.OurUsers;
import com.twd.SpringSecurityJWT.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/useradmin/projects/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Project> getProjectById(@PathVariable int id) {
        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @GetMapping("/useradmin/projects/getall")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @PostMapping("/admin/projects/addproject")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Project> addProject(@RequestBody Project project) {
        Project savedProject = projectService.addProject(project);
        return ResponseEntity.ok(savedProject);
    }

    @PutMapping("/admin/projects/update/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Project> updateProjectById(@PathVariable int id, @RequestBody Project updatedProject) {
        Project project = projectService.updateProjectById(id, updatedProject);
        return ResponseEntity.ok(project);
    }

    @DeleteMapping("/user/projects/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProjectById(@PathVariable int id) {
        projectService.deleteProjectById(id);
        return ResponseEntity.ok("Project with ID " + id + " has been deleted successfully.");
    }

    @GetMapping("/{projectId}/team")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<OurUsers>> getProjectTeam(@PathVariable int projectId) {
        List<OurUsers> team = projectService.getProjectTeam(projectId);
        return ResponseEntity.ok(team);
    }

    @PostMapping("/{projectId}/team")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Project> addTeamMember(
            @PathVariable int projectId,
            @RequestBody TeamMemberRequest teamMemberRequest) {
        Project updatedProject = projectService.addTeamMember(projectId, teamMemberRequest.getUserId());
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{projectId}/team")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Project> removeTeamMember(
            @PathVariable int projectId,
            @RequestBody TeamMemberRequest teamMemberRequest) {
        Project updatedProject = projectService.removeTeamMember(projectId, teamMemberRequest.getUserId());
        return ResponseEntity.ok(updatedProject);
    }

    // New endpoint: Get overdue projects (estimatedEndtime within 3 days)
    @GetMapping("/user/projects/overdue")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Project>> getOverdueProjects() {
        LocalDateTime threeDaysFromNow = LocalDateTime.now().plusDays(3);
        List<Project> overdueProjects = projectService.getOverdueProjects(threeDaysFromNow);
        return ResponseEntity.ok(overdueProjects);
    }

    @GetMapping("/user/projects/{id}/progress")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Double> getProjectProgress(@PathVariable int id) {
        double progress = projectService.calculateProjectProgress(id);
        return ResponseEntity.ok(progress);
    }

    // Endpoint to mark a project as complete and set its endtime
    @PostMapping("/user/projects/{id}/complete")
    public ResponseEntity<String> completeProject(@PathVariable int id) {
        projectService.completeProject(id);  // Calls service to mark project as completed
        return ResponseEntity.ok("Project with ID " + id + " has been marked as completed.");
    }

    @PutMapping("/user/projects/{id}/update-status")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> updateProjectStatus(@PathVariable int id) {
        projectService.updateProjectStatus(id);
        return ResponseEntity.ok("Project status has been updated.");
    }
}

