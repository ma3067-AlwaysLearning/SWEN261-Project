import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Auth, CurrentUser } from '../../core/auth';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  user: CurrentUser | null = null;
  loading = true;
  errorMessage = '';

  constructor(
    private auth: Auth,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCurrentUser();
  }

  loadCurrentUser(): void {
    this.loading = true;
    this.errorMessage = '';

    this.auth.me().subscribe({
      next: (data) => {
        this.user = data;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;

        if (err.status === 401) {
          // Not logged in -> go back to login
          this.router.navigate(['/login']);
          return;
        }

        this.errorMessage = err?.error?.message || 'Failed to load user data.';
      }
    });
  }

  onLogout(): void {
    // Get CSRF token first, then logout
    this.auth.getCsrfToken().subscribe({
      next: () => {
        this.auth.logout().subscribe({
          next: () => {
            this.router.navigate(['/login']);
          },
          error: () => {
            this.errorMessage = 'Logout failed.';
          }
        });
      },
      error: () => {
        this.errorMessage = 'Could not initialize logout (CSRF token failed).';
      }
    });
  }
}