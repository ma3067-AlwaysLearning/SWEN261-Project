import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface CurrentUser {
  name: string;
  email: string;
  university_id: string;
  role: string;
}

@Injectable({
  providedIn: 'root'
})
export class Auth {
  private http = inject(HttpClient);

  getCsrfToken(): Observable<unknown> {
    return this.http.get('/api/auth/csrf', { withCredentials: true });
  }

  login(body: LoginRequest): Observable<unknown> {
    return this.http.post('/api/auth/login', body, { withCredentials: true });
  }

  me(): Observable<CurrentUser> {
    return this.http.get<CurrentUser>('/api/auth/me', { withCredentials: true });
  }

  logout(): Observable<unknown> {
    return this.http.post('/api/auth/logout', {}, { withCredentials: true });
  }
}