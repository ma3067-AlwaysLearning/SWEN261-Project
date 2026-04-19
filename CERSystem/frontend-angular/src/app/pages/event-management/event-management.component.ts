import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface EventItem {
  eventId: number;
  title: string;
  description: string;
  scheduledDate: string;
  startTime?: string;
  endTime?: string;
  category?: string;
  location: string;
  capacity: number;
  status: string;
  organizerName?: string;
}

interface OrganizerEventsResponse {
  count: number;
  events: EventItem[];
}

@Component({
  selector: 'app-event-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './event-management.component.html',
  styleUrls: ['./event-management.component.css']
})
export class EventManagementComponent implements OnInit {

  myEvents: EventItem[] = [];
  selectedEvent: EventItem | null = null;
  isEditing = false;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadMyEvents();
  }

  loadMyEvents(): void {
    this.http.get<OrganizerEventsResponse>('/api/events/organizer')
      .subscribe({
        next: (response) => {
          this.myEvents = response.events;
        },
        error: (err) => {
          console.error('Failed to load organizer events', err);
          alert('Could not load your events. Please try again.');
        }
      });
  }

  editEvent(event: EventItem): void {
    this.selectedEvent = {
      ...event,
      scheduledDate: this.toIsoDate(event.scheduledDate) || ''
    };
    this.isEditing = true;
  }

  saveEdit(): void {
    if (!this.selectedEvent) return;

    const payload = {
      title: this.selectedEvent.title,
      description: this.selectedEvent.description,
      scheduledDate: this.toIsoDate(this.selectedEvent.scheduledDate),
      location: this.selectedEvent.location,
      capacity: this.selectedEvent.capacity
    };

    this.http.put<any>(`/api/events/${this.selectedEvent.eventId}`, payload)
      .subscribe({
        next: (res) => {
          if (res.success) {
            alert(res.message || 'Event updated successfully');
            this.isEditing = false;
            this.selectedEvent = null;
            window.location.reload();
          } else {
            alert(res.message || 'Update failed');
          }
        },
        error: (err) => {
          console.error('Update error', err);
          alert('Error while updating event');
        }
      });
  }

  cancelEvent(eventId: number): void {
    if (!confirm('Are you sure you want to cancel this event?')) return;

    this.http.post<any>(`/api/events/${eventId}/cancel`, {})
      .subscribe({
        next: (res) => {
          if (res.success) {
            alert(res.message || 'Event has been cancelled');
            this.isEditing = false;
            this.selectedEvent = null;
            this.loadMyEvents();
          } else {
            alert(res.message || 'Failed to cancel event');
          }
        },
        error: (err) => {
          console.error('Cancel error', err);
          alert('Error cancelling event');
        }
      });
  }

  private toIsoDate(value: string | null | undefined): string | null {
    if (!value) return null;

    if (/^\d{4}-\d{2}-\d{2}$/.test(value)) {
      return value;
    }

    const parsed = new Date(value);
    if (isNaN(parsed.getTime())) {
      return null;
    }

    return parsed.toISOString().split('T')[0];
  }
}
