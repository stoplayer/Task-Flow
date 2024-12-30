// dashboard/completed-tasks/completed-tasks.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-completed-tasks',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './completed-projects.component.html',
  styleUrls: ['./completed-projects.component.scss']
})
export class CompletedProjectsComponent {
  completedTasks = [
    {
      title: 'Real Estate Website Design',
      completedDate: '2024-03-15',
      progress: 100
    },
    {
      title: 'Finance Mobile App Design',
      completedDate: '2024-03-10',
      progress: 100
    },
    // Add more completed tasks as needed
  ];
}
