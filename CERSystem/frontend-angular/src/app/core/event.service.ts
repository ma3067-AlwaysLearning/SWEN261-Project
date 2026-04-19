import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
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

export interface EventSummary {
  eventId: number;
  title: string;
  description: string;
  scheduledDate: string;
  startTime: string;
  endTime: string;
  registrationStart: string;
  registrationEnd: string;
  category: string;
  location: string;
  organizerName: string;
  status: string;
  capacity: number;
  spotLeft: number;
}

export interface EventFilters {
  keyword?: string;
  category?: string;
  location?: string;
  organizer?: string;
  startDate?: string;
  endDate?: string;
}

export interface EventsResponse {
  count: number;
  events: EventSummary[];
}
@Injectable({ providedIn: 'root' })
export class EventService {
  private http = inject(HttpClient);

  getEvents(filters: EventFilters = {}): Observable<EventsResponse> {
    let params = new HttpParams();
    if (filters.keyword)   params = params.set('keyword',   filters.keyword);
    if (filters.category)  params = params.set('category',  filters.category);
    if (filters.location)  params = params.set('location',  filters.location);
    if (filters.organizer) params = params.set('organizer', filters.organizer);
    if (filters.startDate) params = params.set('startDate', filters.startDate);
    if (filters.endDate)   params = params.set('endDate',   filters.endDate);

    return this.http.get<EventsResponse>('/events/api', {
      params,
      withCredentials: true
    });
  }

  createEvent(event: EventRequest): Observable<any> {
    return this.http.post('/events/api/create', event, { withCredentials: true });
  }

  registerForEvent(eventId: number): Observable<any> {
    return this.http.post(`/events/register/${eventId}`, {}, {
      withCredentials: true
    });
  }

  getAll(): Observable<any> {
    return this.http.get('/events/api', { withCredentials: true });
  }
}
