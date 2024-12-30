import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/AuthService/auth.service'; // Ensure correct path to AuthService
import { ToastrService } from 'ngx-toastr'; // Import ToastrService

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  providers: [ToastrService], // Add ToastrService to providers
  templateUrl: './reset-password.component.html',
  styleUrls: ['../auth.scss']
})
export class ResetPasswordComponent {
  token: string = ''; // Reset token received from the reset link
  newPassword: string = ''; // New password
  confirmPassword: string = ''; // Confirm new password
  errorMessage: string = ''; // To display errors
  successMessage: string = ''; // To display success message

  constructor(private router: Router, private authService: AuthService, private toastr: ToastrService) {}

  onSubmit() {
    if (!this.token) {
      this.errorMessage = 'All fields are Required';
      this.toastr.warning(this.errorMessage, 'Warning'); // Show toastr warning
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = "Passwords don't match!";
      this.toastr.warning(this.errorMessage, 'Warning'); // Show toastr warning
      return;
    }

    if (this.newPassword && this.confirmPassword) {
      // Show information message with the token
     

      // Call the resetpassword method from AuthService
      this.authService.resetpassword({
        resetToken: this.token,
        newPassword: this.newPassword,
        confirmNewPassword: this.confirmPassword
      }).subscribe({
        next: (response) => {
          if (response.body?.statusCode === 200) {
            this.successMessage = 'Your password has been reset successfully. You can now log in.';
            this.toastr.success(this.successMessage, 'Success'); // Show success toastr
            this.errorMessage = '';
            // Redirect to sign-in page after a short delay
            setTimeout(() => this.router.navigate(['/sign-in']), 3000);
          } else {
            this.errorMessage = response.body?.message || 'An error occurred. Please try again.';
            this.toastr.error(this.errorMessage, 'Error'); // Show error toastr
            this.successMessage = '';
          }
        },
        error: (error) => {


          this.errorMessage = 'Your token is invalid or expired.';
          this.toastr.error(this.errorMessage, 'Error'); // Show error toastr
          this.successMessage = '';
        }
      });
    } else {
      this.errorMessage = 'Please fill in all required fields.';
      this.toastr.warning(this.errorMessage, 'Warning'); // Show toastr warning
    }
  }
}
