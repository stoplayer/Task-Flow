import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/AuthService/auth.service'; // Ensure correct path to AuthService

import { ToastrService } from 'ngx-toastr'; // Import ToastrService


@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  providers: [ToastrService], // Add ToastrService to providers
  templateUrl: './forgot-password.component.html',
  styleUrls: ['../auth.scss']
})
export class ForgotPasswordComponent {
  email: string = '';
  errorMessage: string = '';
  successMessage: string = '';

  constructor(private router: Router, private authService: AuthService, private toastr: ToastrService) {}

  onSubmit() {
    if (!this.email) {
      this.errorMessage = 'Email is required.';
      this.toastr.warning(this.errorMessage, 'Warning'); // Show warning toastr
      return;
    }


    // Call the forgotPassword method from AuthService
    this.authService.forgotpassword({ email: this.email }).subscribe({
      next: (response) => {
        if (response.body?.statusCode === 200) {
          this.successMessage = 'If an account exists with this email, you will receive a password reset link shortly.';
          this.toastr.success(this.successMessage, 'Success'); // Show success toastr
          this.errorMessage = '';

        } else if (response.body?.statusCode === 403) {
          this.errorMessage = response.body.message || 'An email has already been sent to this adresse.';
          this.toastr.error(this.errorMessage, 'Error'); // Show error toastr
        } else {

          this.errorMessage = response.body?.message || 'An error occurred. Please try again.';
          this.toastr.error(this.errorMessage, 'Error'); // Show error toastr
        }
      },
      error: (error) => {

        this.errorMessage = 'An email has already been sent to this adresse.';
        this.toastr.error(this.errorMessage, 'Error'); // Show error toastr

      }
    });
  }
  
}
