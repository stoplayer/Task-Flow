import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/AuthService/auth.service'; // Ensure correct path to AuthService
import { ToastrService } from 'ngx-toastr'; // Import ToastrService

@Component({
  selector: 'app-sign-up',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  providers: [ToastrService], // Add ToastrService to providers
  templateUrl: './sign-up.component.html',
  styleUrls: ['../auth.scss']  // Note the changed path
})
export class SignUpComponent {
  name: string = '';
  email: string = '';
  password: string = '';
  confirmPassword: string = '';
  errorMessage: string = ''; // For displaying errors

  constructor(private router: Router, private authService: AuthService, private toastr: ToastrService) {}

  onSubmit() {
    // Check if passwords match
    if (this.password !== this.confirmPassword) {
      this.errorMessage = "Passwords don't match!";
      this.toastr.warning(this.errorMessage, 'Warning'); // Show warning toastr
      return;
    }

    // Ensure required fields are filled
    if (this.email && this.password && this.name) {
      this.toastr.info('Sign up attempt with: ' + this.email, 'Info'); // Show info toastr

      // Call the signup method from AuthService
      this.authService.signup({ email: this.email, password: this.password, name: this.name }).subscribe({
        next: (response) => {
          if (response.body?.statusCode === 200 || response.body?.statusCode === 201) {
            this.toastr.success('Signup successful!', 'Success'); // Show success toastr
            // Redirect to sign-in page on success
            this.router.navigate(['/sign-in']);
          } else if (response.body?.statusCode === 500) {
            this.toastr.error('Error: ' + response.body.error, 'Error'); // Show error toastr
          } else {
            this.errorMessage = response.body?.message || 'An error occurred. Please try again.';
            this.toastr.error(this.errorMessage, 'Error'); // Show error toastr
          }
        },
        error: (error) => {
          this.errorMessage = 'An unexpected error occurred. Please try again later.';
          this.toastr.error(this.errorMessage, 'Error'); // Show error toastr
        }
      });
    } else {
      this.errorMessage = 'Please fill in all required fields.';
      this.toastr.warning(this.errorMessage, 'Warning'); // Show warning toastr
    }
  }
}
