import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { Auth, CurrentUser } from '../../core/auth';
import { EventService, EventSummary, EventFilters } from '../../core/event.service';

@Component({
  selector: 'app-events',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './events.html',
  styleUrls: ['./events.css']
})
export class Events implements OnInit {
  user: CurrentUser | null = null;
  events: EventSummary[] = [];
  resultCount = 0;
  loading = false;
  errorMessage = '';
  successMessage = '';

  filters: EventFilters = {
    keyword: '',
    category: '',
    location: '',
    organizer: '',
    startDate: '',
    endDate: ''
  };

  hasFilters = false;
  categories = ['Workshop', 'Talk', 'Social', 'Sports', 'Academic'];

  private auth = inject(Auth);
  private eventService = inject(EventService);
  private router = inject(Router);

  ngOnInit(): void {
    this.auth.me().subscribe({
      next: (user) => {
        this.user = user;
      },
      error: () => {
        this.user = null;
      }
    });

    this.loadEvents();
  }

  loadEvents(): void {
    this.loading = true;
    this.errorMessage = '';

    this.eventService.getEvents(this.activeFilters()).subscribe({
      next: (res) => {
        this.events = res.events;
        this.resultCount = res.count;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err?.error?.message || 'Failed to load events.';
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.hasFilters = this.checkHasFilters();
    this.loadEvents();
  }

  clearFilters(): void {
    this.filters = {
      keyword: '',
      category: '',
      location: '',
      organizer: '',
      startDate: '',
      endDate: ''
    };

    this.hasFilters = false;
    this.loadEvents();
  }

  private activeFilters(): EventFilters {
    const f: EventFilters = {};

    if (this.filters.keyword) f.keyword = this.filters.keyword;
    if (this.filters.category) f.category = this.filters.category;
    if (this.filters.location) f.location = this.filters.location;
    if (this.filters.organizer) f.organizer = this.filters.organizer;
    if (this.filters.startDate) f.startDate = this.filters.startDate;
    if (this.filters.endDate) f.endDate = this.filters.endDate;

    return f;
  }

  private checkHasFilters(): boolean {
    return !!(
      this.filters.keyword ||
      this.filters.category ||
      this.filters.location ||
      this.filters.organizer ||
      this.filters.startDate ||
      this.filters.endDate
    );
  }

  isLoggedIn(): boolean {
    return !!this.user;
  }

  isStudent(): boolean {
    return this.user?.role === 'STUDENT';
  }

  isOrganizerOrAdmin(): boolean {
    return this.user?.role === 'ORGANIZER' || this.user?.role === 'ADMIN';
  }

  canRegister(): boolean {
    return this.user?.role === 'STUDENT';
  }

  register(eventId: number): void {
    if (!this.user) {
      this.router.navigate(['/login']);
      return;
    }

    if (!this.canRegister()) {
      this.errorMessage = 'Only students can register for events.';
      return;
    }

    const event = this.events.find(e => e.eventId === eventId);

    if (event && event.spotLeft <= 0) {
      this.errorMessage = 'This event is full.';
      return;
    }

    this.successMessage = '';
    this.errorMessage = '';

    this.auth.getCsrfToken().subscribe({
      next: () => {
        this.eventService.registerForEvent(eventId).subscribe({
          next: (res) => {
            if (res.success) {
              this.successMessage = res.message;
              this.loadEvents();
            } else {
              this.errorMessage = res.message;
            }
          },
          error: () => {
            this.errorMessage = 'Registration failed. Please try again.';
          }
        });
      },
      error: () => {
        this.errorMessage = 'Could not initialize request.';
      }
    });
  }

  onLogout(): void {
    this.auth.getCsrfToken().subscribe({
      next: () => {
        this.auth.logout().subscribe({
          next: () => this.router.navigate(['/login']),
          error: () => this.router.navigate(['/login'])
        });
      }
    });
  }
}
