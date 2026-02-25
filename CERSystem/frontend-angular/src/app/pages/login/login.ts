import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { Auth } from '../../core/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login {
  email = '';
  password = '';
  errorMessage = '';
  isSubmitting = false;

  constructor(
    private auth: Auth,
    private router: Router
  ) {}

  clearApiError(): void {
    this.errorMessage = '';
  }

  onSubmit(form: NgForm): void {
    this.errorMessage = '';

    // Frontend validation first (required + email format)
    if (form.invalid) {
      Object.values(form.controls).forEach((control) => {
        control.markAsTouched();
        control.updateValueAndValidity();
      });
      return;
    }

    this.isSubmitting = true;

    // 1) Get CSRF token cookie
    this.auth.getCsrfToken().subscribe({
      next: () => {
        // 2) Login with JSON body
        this.auth.login({
          email: this.email.trim(),
          password: this.password
        }).subscribe({
          next: () => {
            this.isSubmitting = false;
            // 3) On success -> go to dashboard
            this.router.navigate(['/dashboard']);
          },
          error: (err) => {
            this.isSubmitting = false;
            this.errorMessage =
              err?.error?.message || 'Invalid email or password.';
          }
        });
      },
      error: () => {
        this.isSubmitting = false;
        this.errorMessage = 'Could not initialize login (CSRF token failed).';
      }
    });
  }
}