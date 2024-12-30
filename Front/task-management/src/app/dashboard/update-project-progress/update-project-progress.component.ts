import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { ProjectService } from '../../services/ProjectService/project-service.service';
import { ToastrService } from 'ngx-toastr';

interface User {
  id: number;
  name: string;
  avatar?: string;
}

interface Project {
  id: number;
  name: string;
  description: string;
  progress: number;
  estimatedEndtime: Date | null;
  endtime: Date;
  status: string;
  teamMembers: User[];
  budget: number;
}

@Component({
  selector: 'app-update-project-progress',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './update-project-progress.component.html',
  styleUrls: ['./update-project-progress.component.scss'],
})
export class UpdateProjectProgressComponent implements OnInit {
  project: Project | null = null;
  newProgress: number = 0;
  isLoading: boolean = true;
  availableUsers: User[] = [];
  showUsersList: boolean = false;
  showDeleteConfirm: boolean = false;
  userToDelete: User | null = null;
  searchQuery: string = '';
  isEditingDetails: boolean = false;
  editForm = {
    description: '',
    estimatedEndtime: '',
    budget: 0
  };

  constructor(
    private projectService: ProjectService,
    private route: ActivatedRoute,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      const projectId = Number(params.get('id'));
      if (projectId) {
        this.fetchProjectDetails(projectId);
        this.fetchAvailableUsers();
        this.fetchProjectProgress(projectId);
      }
    });
  }

  fetchProjectDetails(projectId: number) {
    this.projectService.getProjectById(projectId).subscribe({
      next: (project) => {
        this.project = {
          id: project.id,
          name: project.name,
          description: project.description,
          progress: project.progress || 0,
          estimatedEndtime: project.estimatedEndtime || null,
          endtime: project.endtime,
          status: project.status || 'In Progress',
          teamMembers: [],
          budget: project.budget || 0
        };
        this.newProgress = this.project.progress;
        this.fetchTeamMembers(projectId);
        this.isLoading = false;
      },
      error: (error) => {
        this.toastr.error('Error fetching project details:', error);
        this.isLoading = false;
      },
    });
  }

  fetchProjectProgress(projectId: number) {
    this.projectService.getProjectProgress(projectId).subscribe({
      next: (progressData) => {
        console.log('Raw Progress Data:', progressData); // Log raw response
        if (this.project) {
          const progress = progressData?.progress;
          console.log('Parsed Progress:', progress); // Check parsed value
          this.project.progress = progress !== undefined && progress !== null ? Math.round(progress) : 0;
          this.newProgress = this.project.progress;
          console.log('Final Project Progress:', this.project.progress); // Log final assigned progress
        }
      },
      error: (error) => {
        console.error('Error fetching project progress:', error);
        if (this.project) {
          this.project.progress = 0; // Fallback to 0% if an error occurs
        }
      },
    });
  }

  private fetchTeamMembers(projectId: number) {
    this.projectService.getProjectTeam(projectId).subscribe({
      next: (teamMembers) => {
        if (this.project) {
          this.project.teamMembers = teamMembers;
        }
      },
      error: (error) => {
        console.error('Error fetching team members:', error);
      }
    });
  }

  fetchAvailableUsers() {
    this.projectService.getAllUsers().subscribe({
      next: (users) => {
        this.availableUsers = users;
      },
      error: (error) => {
        console.error('Error fetching users:', error);
      }
    });
  }

  startEditing() {
    if (!this.project) return;

    this.editForm = {
      description: this.project.description,
      estimatedEndtime: this.formatDateForInput(this.project.estimatedEndtime),
      budget: this.project.budget
    };
    this.isEditingDetails = true;
  }

  cancelEditing() {
    this.isEditingDetails = false;
    this.editForm = {
      description: '',
      estimatedEndtime: '',
      budget: 0
    };
  }

  private formatDateForInput(date: Date | null): string {
    if (!date) return '';
    return new Date(date).toISOString().split('T')[0];
  }

  saveProjectDetails() {
    if (!this.project) return;

    const updatedProject = {
      ...this.project,
      description: this.editForm.description,
      estimatedEndtime: new Date(this.editForm.estimatedEndtime),
      budget: this.editForm.budget
    };

    this.projectService.updateProjectById(this.project.id, updatedProject).subscribe({
      next: (response) => {
        if (this.project) {
          this.project.description = response.description;
          this.project.estimatedEndtime = response.estimatedEndtime;
          this.project.budget = response.budget;
        }
        this.isEditingDetails = false;
        this.toastr.success('Project details updated successfully!');
      },
      error: (error) => {
        this.toastr.error('You do not have permission to update a project');
      }
    });
  }

  updateProgress() {
    if (!this.project) return;

    if (this.newProgress >= 0 && this.newProgress <= 100) {
      const updatedProject = {
        ...this.project,
        progress: this.newProgress
      };

      this.projectService.updateProjectById(this.project.id, updatedProject).subscribe({
        next: () => {
          if (this.project) {
            this.project.progress = this.newProgress;
          }
          alert('Progress updated successfully!');
        },
        error: (error) => {
          console.error('Error updating progress:', error);
          alert('Failed to update progress');
        }
      });
    } else {
      alert('Progress must be between 0 and 100');
    }
  }

  toggleUsersList() {
    this.showUsersList = !this.showUsersList;
    if (!this.showUsersList) {
      this.searchQuery = '';
    }
  }

  selectUser(user: User) {
    if (!this.project) return;

    if (!this.isUserInTeam(user.id)) {
      this.projectService.addTeamMember(this.project.id, user.id).subscribe({
        next: (response) => {
          if (response?.statusCode === 200) {
            this.toastr.success('Team member added successfully!', 'Success');
          }
          this.fetchTeamMembers(this.project!.id);
          this.showUsersList = false;
        },
        error: (error) => {
          this.toastr.error('You do not have permission to update a project', 'Error');
          console.error('Error adding team member:', error);
        }
      });
    }
  }

  isUserInTeam(userId: number): boolean {
    return this.project?.teamMembers.some(member => member.id === userId) || false;
  }

  confirmDelete(member: User, event: Event) {
    event.stopPropagation();
    this.userToDelete = member;
    this.showDeleteConfirm = true;
  }

  cancelDelete() {
    this.userToDelete = null;
    this.showDeleteConfirm = false;
  }

  removeTeamMember() {
    if (!this.project || !this.userToDelete) return;

    this.projectService.removeTeamMember(this.project.id, this.userToDelete.id).subscribe({
      next: () => {
        this.fetchTeamMembers(this.project!.id);
        this.cancelDelete();
      },
      error: (error) => {
        console.error('Error removing team member:', error);
        alert('Failed to remove team member');
      }
    });
  }

  filterUsers(): User[] {
    return this.availableUsers.filter(user => 
      user.name.toLowerCase().includes(this.searchQuery.toLowerCase()) &&
      !this.isUserInTeam(user.id)
    );
  }
}
