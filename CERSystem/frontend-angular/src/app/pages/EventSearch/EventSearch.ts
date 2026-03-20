import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { Auth, CurrentUser } from '../../core/auth';

interface EventItem {
  eventId: number;
  title: string;
  description: string;
  scheduledDate: string;
  category: string;
  location: string;
  status: string;
}

@Component({
  selector: 'app-event-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './EventSearch.html',
  styleUrl: './EventSearch.css'
})
export class EventSearch implements OnInit {
  user: CurrentUser | null = null;
  loading = true;
  errorMessage = '';

  events: EventItem[] = [];
  eventsLoading = false;
  eventsError = '';

  keyword = '';
  category = '';
  location = '';
  date = '';

  constructor(
      private auth: Auth,
      private http: HttpClient,
      private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadEvents();
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
          this.router.navigate(['/login']);
          return;
        }

        this.errorMessage = err?.error?.message || 'Failed to load user data.';
      }
    });
  }

  loadEvents(): void {
    this.eventsLoading = true;
    this.eventsError = '';

    let params = new HttpParams();

    if (this.keyword.trim()) {
      params = params.set('keyword', this.keyword.trim());
    }

    if (this.category.trim()) {
      params = params.set('category', this.category.trim());
    }

    if (this.location.trim()) {
      params = params.set('location', this.location.trim());
    }

    if (this.date.trim()) {
      params = params.set('date', this.date.trim());
    }

    this.http.get<EventItem[]>('/api/events', {
      params,
      withCredentials: true
    }).subscribe({
      next: (data) => {
        this.events = data;
        this.eventsLoading = false;
      },
      error: (err) => {
        this.eventsLoading = false;
        this.eventsError = err?.error?.message || 'Failed to load events.';
      }
    });
  }

  applyFilters(): void {
    this.loadEvents();
  }

  clearFilters(): void {
    this.keyword = '';
    this.category = '';
    this.location = '';
    this.date = '';
    this.loadEvents();
  }

  onLogout(): void {
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
        this.errorMessage = 'Could not initialize logout.';
      }
    });
  }
}