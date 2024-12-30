import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ProjectService } from '../../services/ProjectService/project-service.service';
import { TaskService } from '../../services/TaskService/task-service.service';
import { RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-project-details',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule], // Ensure FormsModule is imported
  templateUrl: './project-details.component.html',
  styleUrls: ['./project-details.component.scss'],
})
export class ProjectDetailsComponent implements OnInit {
  projectId!: number; // Project ID from the route
  projectData: any = null; // To hold the project data
  isLoading: boolean = true; // Loading indicator
  taskData: any = {
    name: '',
    description: '',
    estimatedEndtime: '',
    priority: '',
    projectId: null,
    userId: null, // User ID to be retrieved from localStorage
  }; // Task data to be sent to the API

  constructor(
    private route: ActivatedRoute,
    private projectService: ProjectService,
    private taskService: TaskService,
    private toastr: ToastrService,
  ) {}

  ngOnInit() {
    // Get the project ID from the route
    this.route.paramMap.subscribe((params) => {
      this.projectId = Number(params.get('id'));
      if (this.projectId) {
        this.taskData.projectId = this.projectId; // Set project ID in task data
        this.taskData.userId = Number(localStorage.getItem('userId')); // Set User ID from localStorage
        this.fetchProjectDetails(this.projectId);
      }
    });
  }

  /**
   * Fetch project details using the service
   * @param projectId - The ID of the project
   */
  fetchProjectDetails(projectId: number) {
    this.projectService.getProjectById(projectId).subscribe({
      next: (project) => {
        this.projectData = project;
        this.isLoading = false;
        console.log('Project Details:', this.projectData);
      },
      error: (error) => {
        this.toastr.error('Error fetching project details:', error);
        this.isLoading = false;
      },
    });
  }

  /**
   * Add a new task using the service
   */
  addTask() {
    // Check if any field is missing
    if (!this.taskData.name || !this.taskData.description || !this.taskData.estimatedEndtime || !this.taskData.priority) {
      this.toastr.warning('All fields are required.', 'Warning');
      return;
    }

    const taskToSend = {
      name: this.taskData.name,
      description: this.taskData.description,
      estimatedEndtime: this.taskData.estimatedEndtime,
      priority: this.taskData.priority,
      projectId: this.taskData.projectId,
      userId: this.taskData.userId, // Retrieved from localStorage
    };

    this.taskService.addTask(taskToSend).subscribe({
      next: (response) => {
        console.log('Task added successfully:', response);
        this.toastr.success('Task created successfully!');
        // Optionally, fetch project details again to refresh the task list
        this.fetchProjectDetails(this.projectId);
      },
      error: (error) => {
        this.toastr.error('Error creating task:', error);
        alert('Failed to create task. Please try again.');
      },
    });
  }
}
