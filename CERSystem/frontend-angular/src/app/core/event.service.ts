import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface EventRequest {
  title: string;
  description: string;
  scheduledDate: string;
  category: string;
  location: string;
  capacity: number;
  registrationStart: string;
  registrationEnd: string;
  startTime: string;
  endTime: string;
}

@Injectable({ providedIn: 'root' })
export class EventService {
  private http = inject(HttpClient);

  createEvent(event: EventRequest): Observable<any> {
    return this.http.post('/events/api/create', event, { withCredentials: true });
  }

  getAll(): Observable<any> {
    return this.http.get('/events/api', { withCredentials: true });
  }
}
