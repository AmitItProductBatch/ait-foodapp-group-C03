import { Component, effect, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth.service';
import { CartService } from './core/cart.service';
import { ToastService } from './core/toast.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <header class="nav">
      <div class="nav-inner">
        <a class="brand" routerLink="/">🍔 FoodDelivery</a>

        <nav class="nav-links">
          <a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{ exact: true }">Restaurants</a>

          @if (auth.isLoggedIn()) {
            @if (auth.role() === 'USER') {
              <a routerLink="/orders" routerLinkActive="active">My orders</a>
            }
            @if (auth.role() === 'PARTNER') {
              <a routerLink="/partner" routerLinkActive="active">My restaurants</a>
            }
            @if (auth.role() === 'ADMIN') {
              <a routerLink="/admin" routerLinkActive="active">Admin</a>
            }
          }
        </nav>

        <div class="nav-right">
          @if (auth.isLoggedIn()) {
            @if (auth.role() !== 'ADMIN') {
              <a class="cart-link" routerLink="/cart" aria-label="Cart">
                🛒 @if (cartService.count() > 0) { <span class="count">{{ cartService.count() }}</span> }
              </a>
            }
            <a class="user-link" routerLink="/profile">{{ auth.user()?.name }}
              <span class="badge info">{{ auth.role() }}</span></a>
            <button class="btn ghost small" (click)="auth.logout()">Log out</button>
          } @else {
            <a class="btn ghost small" routerLink="/login">Log in</a>
            <a class="btn primary small" routerLink="/register">Sign up</a>
          }
        </div>
      </div>
    </header>

    <main class="container">
      <router-outlet />
    </main>

    <div class="toasts">
      @for (t of toast.toasts(); track t.id) {
        <div class="toast" [class.err]="t.type === 'err'" (click)="toast.dismiss(t.id)">{{ t.text }}</div>
      }
    </div>
  `,
})
export class AppComponent {
  auth = inject(AuthService);
  cartService = inject(CartService);
  toast = inject(ToastService);
  private router = inject(Router);

  constructor() {
    // Load the cart badge whenever someone logs in (or the page is reloaded while logged in).
    effect(() => {
      if (this.auth.isLoggedIn() && this.auth.role() !== 'ADMIN') {
        this.cartService.refresh().catch(() => undefined);
      } else {
        this.cartService.clearLocal();
      }
    }, { allowSignalWrites: true });
  }
}
