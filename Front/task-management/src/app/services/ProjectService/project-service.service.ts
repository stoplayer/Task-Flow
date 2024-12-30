import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProjectService {
  private baseUrl = 'http://localhost:8080'; // Base URL for endpoints

  constructor(private http: HttpClient) {}

  /**
   * Helper function to set Authorization headers with Bearer token.
   * @returns HttpHeaders
   */
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token'); // Retrieve the token
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }

  /**
   * Get a project by ID.
   * @param id - Project ID
   * @returns Observable<Project>
   */
  getProjectById(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/useradmin/projects/${id}`, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * Get all projects.
   * @returns Observable<Project[]>
   */
  getAllProjects(): Observable<any> {
    return this.http.get(`${this.baseUrl}/useradmin/projects/getall`, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * Add a new project.
   * @param project - Project object to be added
   * @returns Observable<Project>
   */
  addProject(project: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/admin/projects/addproject`, project, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * Update a project by ID.
   * @param id - Project ID
   * @param updatedProject - Updated project data
   * @returns Observable<Project>
   */
  updateProjectById(id: number, updatedProject: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/admin/projects/update/${id}`, updatedProject, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * Delete a project by ID.
   * @param id - Project ID
   * @returns Observable<string>
   */
  deleteProjectById(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/user/projects/delete/${id}`, {
      headers: this.getAuthHeaders(),
      responseType: 'text', // To return plain text response
    });
  }

  /**
   * Get a project's team by project ID.
   * @param projectId - Project ID
   * @returns Observable<OurUsers[]>
   */
  getProjectTeam(projectId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${projectId}/team`, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * Add a team member to a project.
   * @param projectId - Project ID
   * @param userId - User ID to add to the project
   * @returns Observable<Project>
   */
  addTeamMember(projectId: number, userId: number): Observable<any> {
    const body = { userId: userId };
    return this.http.post(`${this.baseUrl}/${projectId}/team`, body, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * Remove a team member from a project.
   * @param projectId - Project ID
   * @param userId - User ID to remove from the project
   * @returns Observable<Project>
   */
  removeTeamMember(projectId: number, userId: number): Observable<any> {
    const body = { userId: userId };
    return this.http.request('delete', `${this.baseUrl}/${projectId}/team`, {
      body: body,
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * Get overdue projects.
   * @returns Observable<Project[]>
   */
  getOverdueProjects(): Observable<any> {
    return this.http.get(`${this.baseUrl}/user/projects/overdue`, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * Get project progress.
   * @param id - Project ID
   * @returns Observable<number>
   */
  getProjectProgress(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/user/projects/${id}/progress`, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * Mark a project as complete.
   * @param id - Project ID
   * @returns Observable<string>
   */
  completeProject(id: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/user/projects/${id}/complete`, {}, {
      headers: this.getAuthHeaders(),
      responseType: 'text', // To return plain text response
    });
  }

  /**
   * Fetch all users.
   * @returns Observable<OurUsers[]>
   */
  getAllUsers(): Observable<any> {
    return this.http.get(`${this.baseUrl}/adminuser/all-users`, {
      headers: this.getAuthHeaders(),
    });
  }
}
