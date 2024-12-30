import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private baseUrl = 'http://localhost:8080/user/tasks'; // Base URL for task endpoints

  constructor(private http: HttpClient) {}

  /**
   * Get the authorization headers with the Bearer token.
   * @returns HttpHeaders
   */
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token'); // Retrieve the token from localStorage
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }

  /**
   * Get a task by ID.
   * @param id - Task ID
   * @returns Observable<TaskDTO>
   */
  getTaskById(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}`, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * Get all tasks.
   * @returns Observable<List<Tasks>>
   */
  getAllTasks(): Observable<any> {
    return this.http.get(`${this.baseUrl}/getall`, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * Add a new task.
   * @param task - Task data
   * @returns Observable<Map<string, Object>>
   */
  addTask(task: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/addtask`, task, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * Update a task by ID.
   * @param id - Task ID
   * @param updatedTask - Updated task data
   * @returns Observable<Map<string, Object>>
   */
  updateTaskById(id: number, updatedTask: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/update/${id}`, updatedTask, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * Delete a task by ID.
   * @param id - Task ID
   * @returns Observable<Map<string, string>>
   */
  deleteTaskById(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/delete/${id}`, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * Get overdue tasks.
   * @returns Observable<Map<string, Object>>
   */
  getOverdueTasks(): Observable<any> {
    return this.http.get(`${this.baseUrl}/overdue`, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * Mark a task as completed.
   * @param id - Task ID
   * @returns Observable<Map<string, Object>>
   */
  completeTask(id: number): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}/complete`, null, {
      headers: this.getAuthHeaders(),
    });
  }
}
