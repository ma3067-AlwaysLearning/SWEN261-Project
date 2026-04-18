// Allows this service to be used anywhere in the Angular app
import { Injectable, inject } from '@angular/core';

// Used to send HTTP requests to the backend
import { HttpClient } from '@angular/common/http';

// Used for the return type of the API call
import { Observable } from 'rxjs';

// This describes one event object coming from the backend
export interface RegisteredEvent {
  eventId: number;        // Event ID
  title: string;          // Event title
  description: string;    // Event description
  scheduledDate: string;  // Event date
  startTime: string;      // Event start time
  endTime: string;        // Event end time
  category: string;       // Event category
  location: string;       // Event location
  organizerName: string;  // Organizer name
  status: string;         // Event status
}

// This describes the full response from the backend
export interface MyEventsResponse {
  count: number;                 // Total number of registered events
  events: RegisteredEvent[];     // List of registered events
}

@Injectable({
  providedIn: 'root'
})
export class MyEventsApi {

  // Lets us send HTTP requests
  private http = inject(HttpClient);

  // Calls the backend API to get the logged-in student's registered events
  getMyEvents(): Observable<MyEventsResponse> {
    return this.http.get<MyEventsResponse>('/events/my/api', { withCredentials: true });
  }
}
