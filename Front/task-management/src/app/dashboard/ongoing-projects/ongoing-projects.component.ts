import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectService } from '../../services/ProjectService/project-service.service';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-ongoing-tasks',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './ongoing-projects.component.html',
  styleUrls: ['./ongoing-projects.component.scss'],
})
export class OngoingTasksComponent implements OnInit {
  ongoingTasks: any[] = []; // Array to hold all ongoing projects

  constructor(private projectService: ProjectService, private router: Router) {}

  ngOnInit() {
    this.loadPendingProjects();
  }

  /**
   * Fetch projects with a status of PENDING from the backend
   */
  loadPendingProjects() {
    this.projectService.getAllProjects().subscribe({
      next: (projects) => {
        // Filter projects based on `PENDING` status and map fields for display
        this.ongoingTasks = projects
          .filter((project: any) => project.status === 'PENDING')
          .map((project: any) => ({
            id: project.id,
            title: project.name,
            endDate: project.estimatedEndtime
              ? new Date(project.estimatedEndtime).toLocaleDateString()
              : 'Not Set',
            progress: project.tasks.length > 0
              ? Math.round(
                  (project.tasks.filter((task: any) => task.status === 'COMPLETED').length /
                    project.tasks.length) *
                    100,
                )
              : 0, // Calculate progress based on completed tasks
            teamMembers: project.team || [], // Team members list
          }));
        console.log('Filtered Pending Projects:', this.ongoingTasks);
      },
      error: (error) => {
        console.error('Error fetching projects:', error);
      },
    });
  }

  /**
   * Navigate to the Project Details page with the selected project ID
   * @param projectId - The ID of the project to view
   */
  ViewProjectDetails(projectId: number) {
    console.log('Navigating to project details with ID:', projectId);
    this.router.navigate([`/dashboard/project-id/${projectId}`]); // Navigate to the details page
  }
}
