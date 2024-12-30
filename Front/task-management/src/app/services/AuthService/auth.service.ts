import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Base URL for the authentication API
  private baseUrl = 'http://localhost:8080/auth';

  constructor(private http: HttpClient) {}

  /**
   * Sign in method to authenticate a user.
   * @param credentials - Object containing email and password.
   * @param signupcredentials
   * @returns Observable with the full HTTP response.
   */
  signIn(credentials: { email: string; password: string }): Observable<HttpResponse<any>> {
    const url = `${this.baseUrl}/signin`;

    // Return full HTTP response by setting `observe: 'response'`
    return this.http.post<any>(url, credentials, { observe: 'response' });
  }

  signup(signupcredentials: {name:string; email: string; password: string}): Observable<HttpResponse<any>> {
    const url = `${this.baseUrl}/signup`;

    // Return full HTTP response by setting `observe: 'response'`
    return this.http.post<any>(url, signupcredentials, { observe: 'response' });
  }
  forgotpassword(credentials: { email: string}): Observable<HttpResponse<any>> {
    const url = `${this.baseUrl}/forgot-password`;

    // Return full HTTP response by setting `observe: 'response'`
    return this.http.post<any>(url, credentials, { observe: 'response' });
  }

  resetpassword(credentials: { resetToken: string;newPassword:string;confirmNewPassword:string}): Observable<HttpResponse<any>> {
    const url = `${this.baseUrl}/reset-password`;

    // Return full HTTP response by setting `observe: 'response'`
    return this.http.post<any>(url, credentials, { observe: 'response' });
  }


}
