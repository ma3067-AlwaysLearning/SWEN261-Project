// Import Component so we can create an Angular page
// Import OnInit so code runs when the page opens
import { Component, OnInit } from '@angular/core';

// Import CommonModule to use Angular features like *ngIf and *ngFor in HTML
import { CommonModule } from '@angular/common';

// Import Router so we can move the user to another page
// Import RouterLink so links work in the HTML file
import { Router, RouterLink } from '@angular/router';

// Import the API service that gets the student's registered events
// Import the RegisteredEvent type for the events array
import { MyEventsApi, RegisteredEvent } from '../../core/my-events';

@Component({
  // Name of this component
  selector: 'app-my-events',

  // This component works by itself without needing a module
  standalone: true,

  // These are needed inside the HTML file
  imports: [CommonModule, RouterLink],

  // HTML file connected to this page
  templateUrl: './my-events.html',

  // CSS file connected to this page
  styleUrl: './my-events.css'
})
export class MyEvents implements OnInit {

  // Stores the list of registered events from the backend
  events: RegisteredEvent[] = [];

  // Used to show loading message while data is being fetched
  loading = true;

  // Used to show an error message if something goes wrong
  errorMessage = '';

  // Constructor gives us access to the API service and router
  constructor(
    private myEventsApi: MyEventsApi,
    private router: Router
  ) {}

  // This runs automatically when the page opens
  ngOnInit(): void {
    this.loadMyEvents();
  }

  // This method calls the backend and gets the student's registered events
  loadMyEvents(): void {

    // Start loading
    this.loading = true;

    // Clear any old error message
    this.errorMessage = '';

    // Call the backend API
    this.myEventsApi.getMyEvents().subscribe({

      // If the request is successful
      next: (response) => {
        // Save the returned events in the events array
        this.events = response.events;

        // Stop loading
        this.loading = false;
      },

      // If something goes wrong
      error: (err) => {
        // Stop loading
        this.loading = false;

        // If user is not logged in, send them to login page
        if (err.status === 401) {
          this.router.navigate(['/login']);
          return;
        }

        // If user is not allowed, send them to unauthorized page
        if (err.status === 403) {
          this.router.navigate(['/unauthorized']);
          return;
        }

        // Show backend error message if it exists
        // Otherwise show a normal fallback message
        this.errorMessage = err?.error?.message || 'Failed to load your events.';
      }
    });
  }
}
