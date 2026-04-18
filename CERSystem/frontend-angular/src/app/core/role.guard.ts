import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Auth, Role } from './auth';

export const roleGuard: CanActivateFn = (route) => {
  const auth = inject(Auth);
  const router = inject(Router);

  const allowedRoles = (route.data?.['roles'] as Role[] | undefined) ?? [];

  return auth.me().pipe(
    map((user) => {
      if (allowedRoles.length === 0 || allowedRoles.includes(user.role as Role)) {
        return true;
      }
      return router.createUrlTree(['/unauthorized']);
    }),
    catchError(() => of(router.createUrlTree(['/login'])))
  );
};
