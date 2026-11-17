import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { ApiService } from './api.service';
import { Address, LoginResponse, Role, SessionUser } from './models';

interface Session {
  token: string;
  expiresAt: number; // epoch millis
  user: SessionUser;
  addresses: Address[];
}

const KEY = 'fd_session';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private api = inject(ApiService);
  private router = inject(Router);

  private readonly session = signal<Session | null>(this.restore());

  readonly user = computed(() => this.session()?.user ?? null);
  readonly role = computed<Role | null>(() => this.session()?.user.role ?? null);
  readonly isLoggedIn = computed(() => this.session() !== null);

  get token(): string | null {
    const s = this.session();
    if (!s) {
      return null;
    }
    if (Date.now() >= s.expiresAt) {
      this.logout(false);
      return null;
    }
    return s.token;
  }

  get userId(): number {
    return this.session()?.user.id ?? 0;
  }

  login(email: string, password: string) {
    return this.api.login(email, password).pipe(tap((res) => this.start(res)));
  }

  logout(navigate = true): void {
    localStorage.removeItem(KEY);
    this.session.set(null);
    if (navigate) {
      this.router.navigate(['/login']);
    }
  }

  /** Keeps the stored name / phone in sync after a profile edit. */
  refreshUser(user: Partial<SessionUser>): void {
    const s = this.session();
    if (!s) {
      return;
    }
    this.save({ ...s, user: { ...s.user, ...user } });
  }

  private start(res: LoginResponse): void {
    this.save({
      token: res.token,
      expiresAt: Date.now() + (res.expiresInSeconds || 3600) * 1000,
      user: {
        id: res.id,
        name: res.name,
        email: res.email,
        phonenumber: res.phonenumber,
        role: String(res.role).toUpperCase() as Role,
        active: res.active,
      },
      addresses: res.addresses || [],
    });
  }

  private save(s: Session): void {
    localStorage.setItem(KEY, JSON.stringify(s));
    this.session.set(s);
  }

  private restore(): Session | null {
    try {
      const raw = localStorage.getItem(KEY);
      if (!raw) {
        return null;
      }
      const s = JSON.parse(raw) as Session;
      if (!s.token || Date.now() >= s.expiresAt) {
        localStorage.removeItem(KEY);
        return null;
      }
      return s;
    } catch {
      localStorage.removeItem(KEY);
      return null;
    }
  }
}
