import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  private baseUrl = 'http://localhost:8080/user/comments';

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
   * Create a new comment.
   * @param content - The content of the comment.
   * @param task - The associated task.
   * @param user - The user creating the comment.
   * @returns Observable<Comment>
   */
  createComment(content: string, taskId: number, userId: number): Observable<any> {
    const body = { content, taskId, userId };
    return this.http.post(`${this.baseUrl}/addcomment`, body, {
      headers: this.getAuthHeaders(),
    });
  }
  

  /**
   * Get all comments.
   * @returns Observable<Comment[]>
   */
  getAllComments(): Observable<any> {
    return this.http.post(`${this.baseUrl}/getallcomments`, null, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * Get a comment by ID.
   * @param id - Comment ID.
   * @returns Observable<Comment>
   */
  getCommentById(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}`, {
      headers: this.getAuthHeaders(),
    });
  }

  /**
   * Delete a comment by ID.
   * @param id - Comment ID.
   * @returns Observable<void>
   */
  deleteComment(commentId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${commentId}`, {
      headers: this.getAuthHeaders(),
    });
  }
  
}
