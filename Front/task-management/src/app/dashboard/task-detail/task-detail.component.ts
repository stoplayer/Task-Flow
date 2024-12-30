import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { TaskService } from '../../services/TaskService/task-service.service';
import { CommentService } from '../../services/CommentService/comment-service.service';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-task-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-detail.component.html',
  styleUrls: ['./task-detail.component.scss']
})
export class TaskDetailComponent implements OnInit {
  taskId!: number; // Task ID from the route
  taskData: any = null; // To hold the task data
  isLoading: boolean = true; // Loading indicator
  newComment: string = ''; // New comment input

  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService,
    private commentService: CommentService,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    // Get the task ID from the route
    this.route.paramMap.subscribe((params) => {
      this.taskId = Number(params.get('id'));
      if (this.taskId) {
        this.fetchTaskDetails(this.taskId);
      }
    });
  }

  /**
   * Fetch task details using the service
   * @param taskId - The ID of the task
   */
  fetchTaskDetails(taskId: number) {
    console.log('Fetching task details for Task ID:', taskId); // Debug log
    this.taskService.getTaskById(taskId).subscribe({
      next: (task) => {
        console.log('API Response:', task); // Log API response
        this.taskData = task;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error fetching task details:', error);
        this.isLoading = false;
        this.toastr.error('Failed to fetch task details.', 'Error');
      },
    });
  }

  /**
   * Add a new comment to the task
   */
  addComment(): void {
    if (!this.newComment.trim()) {
      this.toastr.warning('Comment cannot be empty.', 'Warning');
      return;
    }

    const userId = Number(localStorage.getItem('userId')); // Retrieve userId from localStorage

    if (!userId) {
      this.toastr.error('User is not logged in.', 'Error');
      return;
    }

    const commentData = {
      content: this.newComment,
      taskId: this.taskId,
      userId: userId,
    };

    // Call the comment service to add the comment
    this.commentService.createComment(commentData.content, commentData.taskId, commentData.userId).subscribe({
      next: () => {
        console.log('Comment added successfully');
        this.toastr.success('Comment added successfully!', 'Success');
        this.newComment = ''; // Clear the input field

        // Fetch the updated task details to refresh the comments
        this.fetchTaskDetails(this.taskId);
      },
      error: (error) => {
        console.error('Error adding comment:', error);
        this.toastr.error('Failed to add comment.', 'Error');
      },
    });
  }
}
