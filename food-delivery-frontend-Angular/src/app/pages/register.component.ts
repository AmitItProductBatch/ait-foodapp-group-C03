import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { ToastService } from '../core/toast.service';
import { errMsg } from '../core/util';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div class="card narrow">
      <h1>Create your account</h1>
      <p class="muted mb">Order from local restaurants, or list your own.</p>

      <form (ngSubmit)="submit()">
        <div class="field">
          <label for="name">Full name</label>
          <input id="name" name="name" [(ngModel)]="name" required>
        </div>
        <div class="field">
          <label for="email">Email</label>
          <input id="email" name="email" type="email" [(ngModel)]="email" autocomplete="username" required>
        </div>
        <div class="field">
          <label for="phone">Mobile number</label>
          <input id="phone" name="phone" [(ngModel)]="phone" inputmode="numeric" placeholder="10 digits" required>
        </div>
        <div class="field">
          <label for="password">Password</label>
          <input id="password" name="password" type="password" [(ngModel)]="password" autocomplete="new-password" placeholder="At least 8 characters" required>
        </div>
        <div class="field">
          <label for="role">I want to</label>
          <select id="role" name="role" [(ngModel)]="role">
            <option value="USER">Order food (customer)</option>
            <option value="PARTNER">List my restaurant (partner)</option>
          </select>
        </div>

        @if (error()) { <p class="error-text">{{ error() }}</p> }

        <button class="btn primary" style="width:100%" type="submit" [disabled]="busy()">
          {{ busy() ? 'Creating account…' : 'Sign up' }}
        </button>
      </form>

      <p class="center muted mt small-text">Already registered? <a routerLink="/login">Log in</a></p>
    </div>
  `,
})
export class RegisterComponent {
  private api = inject(ApiService);
  private auth = inject(AuthService);
  private router = inject(Router);
  private toast = inject(ToastService);

  name = '';
  email = '';
  phone = '';
  password = '';
  role = 'USER';
  busy = signal(false);
  error = signal('');

  submit(): void {
    this.error.set('');

    if (!this.name.trim() || !this.email.trim()) {
      this.error.set('Name and email are required');
      return;
    }
    if (!/^\d{10}$/.test(this.phone.trim())) {
      this.error.set('Mobile number must be exactly 10 digits');
      return;
    }
    if (this.password.length < 8 || this.password.length > 72) {
      this.error.set('Password must be between 8 and 72 characters');
      return;
    }

    this.busy.set(true);

    this.api.register({
      name: this.name.trim(),
      email: this.email.trim(),
      password: this.password,
      phonenumber: this.phone.trim(),
      role: this.role,
    }).subscribe({
      next: () => {
        // Log in straight away so the person lands inside the app.
        this.auth.login(this.email.trim(), this.password).subscribe({
          next: () => {
            this.busy.set(false);
            this.toast.ok('Welcome, ' + this.name.trim() + '!');
            this.router.navigate([this.role === 'PARTNER' ? '/partner' : '/']);
          },
          error: () => {
            this.busy.set(false);
            this.toast.ok('Account created. Please log in.');
            this.router.navigate(['/login']);
          },
        });
      },
      error: (e) => {
        this.busy.set(false);
        this.error.set(errMsg(e));
      },
    });
  }
}
