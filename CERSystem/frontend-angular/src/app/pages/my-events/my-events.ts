import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MyEventsApi, RegisteredEvent } from '../../core/my-events';

@Component({
  selector: 'app-my-events',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './my-events.html',
  styleUrl: './my-events.css'
})
export class MyEvents implements OnInit {
  events: RegisteredEvent[] = [];
  loading = true;
  errorMessage = '';

  constructor(
    private myEventsApi: MyEventsApi,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadMyEvents();
  }

  loadMyEvents(): void {
    this.loading = true;
    this.errorMessage = '';

    this.myEventsApi.getMyEvents().subscribe({
      next: (response) => {
        this.events = response.events;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;

        if (err.status === 401) {
          this.router.navigate(['/login']);
          return;
        }

        if (err.status === 403) {
          this.router.navigate(['/unauthorized']);
          return;
        }

        this.errorMessage = err?.error?.message || 'Failed to load your events.';
      }
    });
  }
}
