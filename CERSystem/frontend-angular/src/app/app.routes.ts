import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Dashboard } from './pages/dashboard/dashboard';
import {authGuard} from './core/auth.guard';
import {roleGuard} from './core/role.guard';
import {Unauthorized} from './pages/unauthorized/unauthorized';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'dashboard', component: Dashboard },

  {
    path: 'student-events',
    component: Dashboard,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['STUDENT'] }
  },
  {
    path: 'manage-events',
    component: Dashboard,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ORGANIZER', 'ADMIN'] }
  },
  {
    path: 'admin-panel',
    component: Dashboard,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMIN'] }
  },

  { path: 'unauthorized', component: Unauthorized }
];
