import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {NgForOf} from '@angular/common';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-event-management',
  templateUrl: './event-management.component.html',
  imports: [
    NgForOf,
    FormsModule
  ],
  styleUrls: ['./event-management.component.css']
})
export class EventManagementComponent implements OnInit {

  myEvents: any[] = [];
  selectedEvent: any = null;
  isEditing = false;

  constructor(private http: HttpClient) { }

  ngOnInit() {
    this.loadMyEvents();
  }

  // Load all events (we can filter organizer's events later)
  loadMyEvents() {
    this.http.get<any[]>('/api/events')
      .subscribe({
        next: (events) => {
          this.myEvents = events;
        },
        error: (err) => {
          console.error('Failed to load events', err);
          alert('Could not load events. Please try again.');
        }
      });
  }

  editEvent(event: any) {
    this.selectedEvent = { ...event };   // copy the event
    this.isEditing = true;
  }

  saveEdit() {
    if (!this.selectedEvent) return;

    this.http.put(`/api/events/${this.selectedEvent.eventId}`, this.selectedEvent)
      .subscribe({
        next: (res: any) => {
          if (res.success) {
            alert(res.message || 'Event updated successfully!');
            this.isEditing = false;
            this.selectedEvent = null;
            this.loadMyEvents();        // refresh list
          } else {
            alert(res.message || 'Update failed');
          }
        },
        error: (err) => {
          alert('Error while updating event');
          console.error(err);
        }
      });
  }

  cancelEvent(eventId: number) {
    if (confirm('Are you sure you want to cancel this event?')) {
      this.http.post(`/api/events/${eventId}/cancel`, {})
        .subscribe({
          next: (res: any) => {
            if (res.success) {
              alert(res.message || 'Event has been cancelled');
              this.loadMyEvents();
            } else {
              alert(res.message || 'Failed to cancel event');
            }
          },
          error: (err) => {
            alert('Error cancelling event');
            console.error(err);
          }
        });
    }
  }

  protected readonly event = event;
}
