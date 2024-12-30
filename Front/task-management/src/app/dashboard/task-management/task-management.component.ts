import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ProjectService } from '../../services/ProjectService/project-service.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-task-management',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './task-management.component.html',
  styleUrls: ['./task-management.component.scss'],
})
export class TaskManagementComponent implements OnInit {
  userName: string = 'John Doe';
  ongoingProjects: any[] = []; // Projects with status 'PENDING'
  completedProjects: any[] = []; // Projects with status 'COMPLETED'

  constructor(
    private router: Router,
    private projectService: ProjectService,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    // Retrieve the user's name directly from localStorage
    const storedName = localStorage.getItem('name');
    if (storedName) {
      this.userName = storedName;
    }

    // Fetch projects from the backend
    this.loadProjects();
  }

  loadProjects() {
    this.projectService.getAllProjects().subscribe({
      next: (projects) => {
        // Map and filter projects based on their status
        this.completedProjects = projects
          .filter((project: any) => project.status === 'COMPLETED')
          .slice(0, 3) // Limit to 3 projects
          .map((project: any) => ({
            title: project.name,
            endTime: project.estimatedEndtime ? new Date(project.estimatedEndtime).toLocaleDateString() : 'Not ended yet',
          }));

        this.ongoingProjects = projects
          .filter((project: any) => project.status === 'PENDING')
          .slice(0, 3) // Limit to 3 projects
          .map((project: any) => ({
            title: project.name,
            endTime: project.estimatedEndtime ? new Date(project.estimatedEndtime).toLocaleDateString() : 'Not ended yet',
          }));

        console.log('Completed Projects:', this.completedProjects);
        console.log('Ongoing Projects:', this.ongoingProjects);
      },
      error: (error) => {
        console.error('Error fetching projects:', error);
        this.toastr.error('Failed to load projects.', 'Error');
      },
    });
  }
}
