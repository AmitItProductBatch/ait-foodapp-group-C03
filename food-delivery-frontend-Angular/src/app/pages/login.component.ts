import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../core/auth.service';
import { errMsg } from '../core/util';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div class="card narrow">
      <h1>Welcome back</h1>
      <p class="muted mb">Log in to order food or manage your restaurant.</p>

      <form (ngSubmit)="submit()">
        <div class="field">
          <label for="email">Email</label>
          <input id="email" name="email" type="email" [(ngModel)]="email" autocomplete="username" required>
        </div>
        <div class="field">
          <label for="password">Password</label>
          <input id="password" name="password" type="password" [(ngModel)]="password" autocomplete="current-password" required>
        </div>

        @if (error()) { <p class="error-text">{{ error() }}</p> }

        <button class="btn primary" style="width:100%" type="submit" [disabled]="busy()">
          {{ busy() ? 'Logging in…' : 'Log in' }}
        </button>
      </form>

      <p class="center muted mt small-text">New here? <a routerLink="/register">Create an account</a></p>
    </div>
  `,
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  email = '';
  password = '';
  busy = signal(false);
  error = signal('');

  submit(): void {
    if (!this.email.trim() || !this.password) {
      this.error.set('Enter your email and password');
      return;
    }
    this.busy.set(true);
    this.error.set('');

    this.auth.login(this.email.trim(), this.password).subscribe({
      next: () => {
        this.busy.set(false);
        const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');
        const role = this.auth.role();
        if (returnUrl && returnUrl !== '/login') {
          this.router.navigateByUrl(returnUrl);
        } else if (role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else if (role === 'PARTNER') {
          this.router.navigate(['/partner']);
        } else {
          this.router.navigate(['/']);
        }
      },
      error: (e) => {
        this.busy.set(false);
        this.error.set(errMsg(e));
      },
    });
  }
}
