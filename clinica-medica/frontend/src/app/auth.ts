import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpInterceptorFn } from '@angular/common/http';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, tap, throwError } from 'rxjs';

/** Guarda o usuario logado (login/senha vao no header Authorization - HTTP Basic do Spring Security). */
@Injectable({ providedIn: 'root' })
export class AuthService {
  usuario: any = null;

  constructor(private http: HttpClient, private router: Router) {
    const salvo = sessionStorage.getItem('usuario');
    if (salvo) this.usuario = JSON.parse(salvo);
  }

  login(login: string, senha: string) {
    sessionStorage.setItem('token', btoa(login + ':' + senha));
    return this.http.get<any>('/api/auth/me').pipe(
      tap(u => {
        this.usuario = u;
        sessionStorage.setItem('usuario', JSON.stringify(u));
      }),
      catchError(e => {
        sessionStorage.removeItem('token');
        return throwError(() => e);
      })
    );
  }

  logout() {
    sessionStorage.clear();
    this.usuario = null;
    this.router.navigate(['/login']);
  }

  tem(...perfis: string[]): boolean {
    return !!this.usuario && perfis.some(p => this.usuario.perfis.includes(p));
  }
}

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = sessionStorage.getItem('token');
  const router = inject(Router);
  const r = token ? req.clone({ setHeaders: { Authorization: 'Basic ' + token } }) : req;
  return next(r).pipe(
    catchError(e => {
      if (e.status === 401 && token && !req.url.endsWith('/api/auth/me')) {
        sessionStorage.clear();
        router.navigate(['/login']);
      }
      return throwError(() => e);
    })
  );
};

export const guard: CanActivateFn = route => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (!auth.usuario) return router.parseUrl('/login');
  const perfis = route.data['perfis'] as string[];
  if (perfis && !auth.tem(...perfis)) return router.parseUrl('/');
  return true;
};
