import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/AuthService/auth.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-sign-in',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  providers: [AuthService, ToastrService],
  templateUrl: './sign-in.component.html',
  styleUrls: ['../auth.scss']
})
export class SignInComponent {
  email: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(private router: Router, private authService: AuthService, private toastr: ToastrService) {}

  onSubmit() {
    if (!this.email) {
      this.toastr.warning('Email is required.', 'Warning');
      return;
    }

    if (!this.password) {
      this.toastr.warning('Password is required.', 'Warning');
      return;
    }

    

    this.authService.signIn({ email: this.email, password: this.password }).subscribe({
      next: (response) => {
        const statusCode = response.body?.statusCode;
        

        if (statusCode === 200) {
          const token = response.body?.token;
          const name = response.body?.name;
          const userId = response.body?.ourUsers.id;
          const userImg = response.body?.ourUsers.profilePicture;
          if(name){
            localStorage.setItem('name', name); // Store Name in localStorage
            console.log('Name stored in localStorage:', name);

          }
          if(userImg){
            localStorage.setItem('userImg', userImg); // Store Name in localStorage
            console.log('Name stored in localStorage:', userImg);

          }
          if(userId){
            localStorage.setItem('userId', userId); // Store Name in localStorage
            console.log('User Id stored in localstorage:', name);

          }
          if (token) {
            localStorage.setItem('token', token);
            console.log('Token stored in localStorage.', 'Success');
          }
          this.toastr.success('Login successful!', 'Success');
          this.router.navigate(['/dashboard']);
        } else if (statusCode === 500) {
          this.toastr.error('Login failed: Bad credentials', 'Error');
          this.errorMessage = 'Invalid email or password. Please try again.';
        } else {
          this.toastr.error('Unexpected response statusCode: ' + statusCode, 'Error');
          this.errorMessage = 'An unexpected error occurred. Please try again later.';
        }
      },
      error: (error) => {
        this.toastr.error('Request error: ' + error, 'Error');
        this.errorMessage = 'An unexpected error occurred. Please try again later.';
      }
    });
  }

  signOut() {
    localStorage.removeItem('authToken'); // Remove the token from localStorage
    console.log('User signed out and token removed from localStorage.');
    this.router.navigate(['/signin']); // Redirect the user to the sign-in page

  }
}