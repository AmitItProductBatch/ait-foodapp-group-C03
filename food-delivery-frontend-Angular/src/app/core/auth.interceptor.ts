import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { ToastService } from './toast.service';

const PUBLIC_POSTS = ['/api/users/login', '/api/users/register'];

/** Adds the Bearer token to API calls and logs the user out when the token is rejected. */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const toast = inject(ToastService);

  const isPublicPost = req.method === 'POST' && PUBLIC_POSTS.some((u) => req.url.endsWith(u));
  const token = auth.token;

  const request = token && !isPublicPost
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(request).pipe(
    catchError((e: HttpErrorResponse) => {
      if (e.status === 401 && !isPublicPost && auth.isLoggedIn()) {
        auth.logout(false);
        toast.err('Your session has expired. Please log in again.');
        router.navigate(['/login'], { queryParams: { returnUrl: router.url } });
      }
      return throwError(() => e);
    }),
  );
};
