import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../services/TaskService/task-service.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-update-task',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './update-task.component.html',
  styleUrls: ['./update-task.component.scss']
})
export class UpdateTaskComponent implements OnInit {
  taskId!: number;
  isLoading: boolean = true;
  taskData: any = {
    id: 0,
    name: '',
    description: '',
    status: 'PENDING',
    priority: 'MEDIUM',
    projectName: '',
    estimatedEndtime: '',
    endtime: null,
    user: {
      name: ''
    }
  };

  statuses = ['PENDING', 'IN_PROGRESS', 'COMPLETED'];
  priorities = ['LOW', 'MEDIUM', 'HIGH'];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private taskService: TaskService,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.taskId = Number(params.get('id'));
      this.fetchTaskDetails();
    });
  }

  fetchTaskDetails() {
    this.isLoading = true;
    this.taskService.getTaskById(this.taskId).subscribe({
      next: (response) => {
        this.taskData = {
          ...response,
          estimatedEndtime: this.formatDateForInput(response.estimatedEndtime)
        };
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error fetching task details:', error);
        this.isLoading = false;
        this.toastr.error('Failed to fetch task details.', 'Error');
      }
    });
  }

  private formatDateForInput(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toISOString().slice(0, 16); // Format: "YYYY-MM-DDThh:mm"
  }

  updateTask() {
    if (!this.validateForm()) {
      return;
    }

    const updatedTask = {
      ...this.taskData,
      estimatedEndtime: new Date(this.taskData.estimatedEndtime).toISOString()
    };

    this.taskService.updateTaskById(this.taskId, updatedTask).subscribe({
      next: (response) => {
        // Show success message
        this.toastr.success('Task updated successfully!');
        // Navigate back to task details
        this.router.navigate(['/dashboard/task', this.taskId]);
      },
      error: (error) => {
        console.error('Error updating task:', error);
        this.toastr.error('You can\'t modify a task unless you are its owner.');
      }
    });
  }

  validateForm(): boolean {
    if (!this.taskData.name.trim()) {
      this.toastr.warning('All the fields are required', 'Warning');
      return false;
    }
    if (!this.taskData.description.trim()) {
      this.toastr.warning('All the fields are required', 'Warning');
      return false;
    }
    if (!this.taskData.estimatedEndtime) {
      this.toastr.warning('All the fields are required', 'Warning');
      return false;
    }
    return true;
  }

  goBack() {
    this.router.navigate(['/dashboard/task', this.taskId]);
  }
}
