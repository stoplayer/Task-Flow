// app.routes.ts
import { Routes } from '@angular/router';
import { SignInComponent } from './auth/sign-in/sign-in.component';
import { SignUpComponent } from './auth/sign-up/sign-up.component';
import { ForgotPasswordComponent } from './auth/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './auth/reset-password/reset-password.component';

import { TaskManagementComponent } from './dashboard/task-management/task-management.component';
import { DashboardLayoutComponent } from './dashboard/dashboard-layout/dashboard-layout.component';
import { OverdueProjectsComponent } from './dashboard/overdue-projects/overdue-projects.component';
import { CalendarComponent } from './dashboard/calendar/calendar.component';
import { CompletedProjectsComponent } from './dashboard/completed-projects/completed-projects.component';
import { CreateProjectsComponent } from './dashboard/create-projects/create-projects.component';
import { OngoingTasksComponent } from './dashboard/ongoing-projects/ongoing-projects.component';
import { ProjectDetailsComponent } from './dashboard/project-details/project-details.component';
import { TaskDetailComponent } from './dashboard/task-detail/task-detail.component';
import { UpdateProjectProgressComponent } from './dashboard/update-project-progress/update-project-progress.component';
import { UpdateTaskComponent } from './dashboard/update-task/update-task.component';

export const routes: Routes = [
  // Auth routes (without sidebar)
  { path: '', redirectTo: '/sign-in', pathMatch: 'full' },
  { path: 'sign-in', component: SignInComponent },
  { path: 'sign-up', component: SignUpComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },

  // Dashboard routes (with sidebar)
  {
    path: 'dashboard',
    component: DashboardLayoutComponent,
    children: [
      { path: '', redirectTo: 'tasks', pathMatch: 'full' },
      { path: 'tasks', component: TaskManagementComponent },
      { path: 'completed-project', component: CompletedProjectsComponent }, 
      { path: 'ongoing-project', component: OngoingTasksComponent },
      { path: 'overdue-project', component: OverdueProjectsComponent },
      { path: 'calendar', component: CalendarComponent },
      { path: 'create-project', component: CreateProjectsComponent },
      { path: 'project-id/:id', component: ProjectDetailsComponent },
      { path: 'task/:id', component: TaskDetailComponent },
      { path: 'update-project/:id',component: UpdateProjectProgressComponent },
      { path: 'update-task/:id', component: UpdateTaskComponent }
    ]
  }
];
