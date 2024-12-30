import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TaskService } from '../../services/TaskService/task-service.service';

@Component({
  selector: 'app-overdue-tasks',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './overdue-projects.component.html',
  styleUrls: ['./overdue-projects.component.scss'],
})
export class OverdueProjectsComponent implements OnInit {
  overdueTasks: any[] = []; // Array to hold overdue tasks
  isLoading: boolean = true; // Loading indicator

  constructor(private taskService: TaskService) {}

  ngOnInit(): void {
    this.loadOverdueTasks();
  }

  /**
   * Load overdue tasks using TaskService.
   */
  loadOverdueTasks(): void {
    this.taskService.getOverdueTasks().subscribe({
      next: (response) => {
        this.overdueTasks = response.tasks.map((task: any) => ({
          id: task.id,
          title: task.name,
          description: task.description,
          dueDate: new Date(task.estimatedEndtime).toLocaleDateString(),
          daysOverdue: this.calculateDaysOverdue(task.estimatedEndtime),
          priority: task.priority,
          assignedTo: task.user?.name || 'Unassigned',
        }));
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error fetching overdue tasks:', error);
        this.isLoading = false;
      },
    });
  }

  /**
   * Calculate days overdue for a task.
   * @param estimatedEndtime - The estimated end time of the task
   * @returns Number of days overdue
   */
  private calculateDaysOverdue(estimatedEndtime: string): number {
    const today = new Date();
    const dueDate = new Date(estimatedEndtime);
    const timeDiff = today.getTime() - dueDate.getTime();
    return Math.ceil(timeDiff / (1000 * 3600 * 24)); // Convert milliseconds to days
  }

  /**
   * Mark a task as complete.
   * @param task - Task object
   */
  markComplete(task: any): void {
    console.log(`Marking task ${task.id} as complete`);
    // You can integrate the `completeTask` method from TaskService here
    this.taskService.completeTask(task.id).subscribe({
      next: (response) => {
        console.log('Task marked as complete:', response);
        this.loadOverdueTasks(); // Refresh the task list after marking complete
      },
      error: (error) => {
        console.error('Error marking task as complete:', error);
      },
    });
  }
}
