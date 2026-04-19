import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { EventService, EventRequest } from '../../core/event.service';
import { Auth } from '../../core/auth';

@Component({
  selector: 'app-create-event',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-event.html',
  styleUrls: ['./create-event.css']
})
export class CreateEvent {
  event = {
    title: '',
    description: '',
    scheduledDate: '',
    category: '',
    location: '',
    capacity: 0,
    registrationStart: '',
    registrationEnd: '',
    startTime: '',
    endTime: ''
  };

  categories = ['Workshop', 'Talk', 'Social', 'Sports', 'Academic'];
  fieldErrors: Record<string, string> = {};
  errorMessage = '';
  successMessage = '';
  loading = false;

  private eventService = inject(EventService);
  private router = inject(Router);
  private auth = inject(Auth);

  validate(): boolean {
    this.fieldErrors = {};

    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const scheduledDate = this.event.scheduledDate ? new Date(this.event.scheduledDate) : null;
    const regStart = this.event.registrationStart ? new Date(this.event.registrationStart) : null;
    const regEnd = this.event.registrationEnd ? new Date(this.event.registrationEnd) : null;

    if (!this.event.title.trim()) this.fieldErrors['title'] = 'Title is required.';
    if (!this.event.description.trim()) this.fieldErrors['description'] = 'Description is required.';
    if (!this.event.category) this.fieldErrors['category'] = 'Category is required.';
    if (!this.event.location.trim()) this.fieldErrors['location'] = 'Location is required.';
    if (!this.event.capacity || this.event.capacity < 1) this.fieldErrors['capacity'] = 'Capacity must be at least 1.';
    if (!this.event.scheduledDate) this.fieldErrors['scheduledDate'] = 'Scheduled date is required.';
    if (!this.event.startTime) this.fieldErrors['startTime'] = 'Start time is required.';
    if (!this.event.endTime) this.fieldErrors['endTime'] = 'End time is required.';
    if (!this.event.registrationStart) this.fieldErrors['registrationStart'] = 'Registration start is required.';
    if (!this.event.registrationEnd) this.fieldErrors['registrationEnd'] = 'Registration end is required.';

    if (scheduledDate && scheduledDate < today) {
      this.fieldErrors['scheduledDate'] = 'Scheduled date cannot be in the past.';
    }

    if (this.event.startTime && this.event.endTime) {
      if (this.event.endTime <= this.event.startTime) {
        this.fieldErrors['endTime'] = 'End time must be after start time.';
      }
    }

    if (regStart && regEnd) {
      if (regEnd <= regStart) {
        this.fieldErrors['registrationEnd'] = 'Registration end must be after registration start.';
      }
    }

    if (regStart && scheduledDate) {
      if (regStart > scheduledDate) {
        this.fieldErrors['registrationStart'] = 'Registration cannot start after the event date.';
      }
    }

    if (regEnd && scheduledDate) {
      if (regEnd > scheduledDate) {
        this.fieldErrors['registrationEnd'] = 'Registration must close on or before the event date.';
      }
    }

    return Object.keys(this.fieldErrors).length === 0;
  }

  private buildDateTime(date: string, time: string): string {
    return `${date}T${time}:00`;
  }

  onSubmit() {
    if (!this.validate()) return;

    this.errorMessage = '';
    this.successMessage = '';
    this.loading = true;

    const payload: EventRequest = {
      title: this.event.title,
      description: this.event.description,
      scheduledDate: this.event.scheduledDate,
      category: this.event.category,
      location: this.event.location,
      capacity: this.event.capacity,
      registrationStart: this.event.registrationStart + ':00',
      registrationEnd: this.event.registrationEnd + ':00',
      startTime: this.buildDateTime(this.event.scheduledDate, this.event.startTime),
      endTime: this.buildDateTime(this.event.scheduledDate, this.event.endTime)
    };

    this.auth.getCsrfToken().subscribe({
      next: () => {
        this.eventService.createEvent(payload).subscribe({
          next: (res) => {
            this.loading = false;
            this.successMessage = res.message;
            setTimeout(() => this.router.navigate(['/dashboard']), 1500);
          },
          error: (err) => {
            this.loading = false;
            if (err.status === 400) {
              this.fieldErrors = err.error;
            } else if (err.status === 403) {
              this.errorMessage = 'You do not have permission to create events.';
            } else {
              this.errorMessage = 'Something went wrong. Please try again.';
            }
          }
        });
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Could not initialize request. Please try again.';
      }
    });
  }

  goBack() {
    this.router.navigate(['/dashboard']);
  }
}
