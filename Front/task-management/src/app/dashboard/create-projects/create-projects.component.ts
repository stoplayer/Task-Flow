import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProjectService } from '../../services/ProjectService/project-service.service'; // Correct path to ProjectService
import { ToastrService } from 'ngx-toastr'; // For notifications
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-task',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-projects.component.html',
  styleUrls: ['./create-projects.component.scss'],
})
export class CreateProjectsComponent {
  // Form fields
  name: string = '';
  budget: number = 0;
  description: string = '';
  estimatedEndtime: string = '';
  endtime: string = '';

  // Static data for team members (currently not used)
  teamMembers = [
    { id: 1, name: 'John Doe', avatar: 'assets/avatar1.jpg' },
    { id: 2, name: 'Jane Smith', avatar: 'assets/avatar2.jpg' },
    { id: 3, name: 'Mike Johnson', avatar: 'assets/avatar3.jpg' },
  ];

  constructor(
    private projectService: ProjectService,
    private toastr: ToastrService,
    private router: Router
  ) {}

  /**
   * Submit form and create a new project.
   */
  createProject() {
    // Validate required fields
    if (
      !this.name.trim() || 
      this.budget <= 0 || 
      !this.description.trim() || 
      !this.estimatedEndtime.trim()
    ) {
      this.toastr.warning('All fields are required and must be valid.', 'Warning');
      return;
    }

    // Combine the date fields with default time if not provided
    const formattedEstimatedEndtime = this.estimatedEndtime
      ? `${this.estimatedEndtime}T00:00:00`
      : null;
    const formattedEndtime = this.endtime
      ? `${this.endtime}T00:00:00`
      : null;
  
    // Prepare the project payload
    const projectPayload = {
      name: this.name.trim(),
      budget: this.budget,
      description: this.description.trim(),
      estimatedEndtime: formattedEstimatedEndtime,
      endtime: formattedEndtime,
    };
  
    // Call addProject method from ProjectService
    this.projectService.addProject(projectPayload).subscribe({
      next: (response) => {
        console.log('Project created successfully:', response);
        this.toastr.success('Project created successfully!', 'Success');
        this.router.navigate(['/dashboard/ongoing-project']);
      },
      error: (error) => {
        console.error('Error creating project:', error);
        this.toastr.error('Only admins are allowed to create projects', 'Error');
      },
    });
  }
}
