import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { CartService } from '../core/cart.service';
import { FeedbackPage, Restaurant, RestaurantMenu } from '../core/models';
import { ToastService } from '../core/toast.service';
import { errMsg } from '../core/util';

@Component({
  selector: 'app-restaurant-detail',
  standalone: true,
  imports: [CurrencyPipe, DatePipe, RouterLink],
  template: `
    <a routerLink="/" class="muted small-text">← All restaurants</a>

    @if (loading()) {
      <div class="spinner">Loading…</div>
    } @else if (error()) {
      <div class="card mt"><p class="error-text">{{ error() }}</p></div>
    }

    @if (!loading() && !error() && restaurant(); as r) {
      <div class="card mt mb">
        <div class="row between">
          <div>
            <h1>{{ r.name }}</h1>
            <p class="muted">{{ r.address }}</p>
            <p class="small-text muted">🕒 {{ r.hours }}</p>
          </div>
          <div class="right">
            <span class="badge info">{{ r.cuisine }}</span>
            <p class="mt">
              @if (feedback()?.totalElements) {
                <span class="stars">★</span> <strong>{{ average() }}</strong>
                <span class="muted small-text">({{ feedback()?.totalElements }} reviews)</span>
              } @else { <span class="muted small-text">No reviews yet</span> }
            </p>
          </div>
        </div>
      </div>

      <div class="grid two">
        <div class="card">
          <h2>Menu</h2>
          @if (!menu() || menu()!.categories.length === 0) {
            <p class="muted">This restaurant has no items available right now.</p>
          }
          @for (cat of menu()?.categories; track cat.category) {
            <h3 class="mt" style="text-transform:capitalize">{{ cat.category }}</h3>
            @for (item of cat.items; track item.itemId) {
              <div class="menu-item">
                <div>
                  <strong>{{ item.name }}</strong>
                  <p class="muted small-text">{{ item.description }}</p>
                  <span class="price">{{ item.price | currency: 'INR' : 'symbol' : '1.0-2' }}</span>
                </div>
                @if (auth.role() !== 'ADMIN') {
                  <div class="stack" style="align-items:flex-end; justify-content:center">
                    <div class="qty">
                      <button (click)="change(item.itemId, -1)" aria-label="less">−</button>
                      <span>{{ qty(item.itemId) }}</span>
                      <button (click)="change(item.itemId, 1)" aria-label="more">+</button>
                    </div>
                    <button class="btn primary small" [disabled]="adding() === item.itemId" (click)="add(item.itemId, r.restaurantId)">
                      {{ adding() === item.itemId ? 'Adding…' : 'Add to cart' }}
                    </button>
                  </div>
                }
              </div>
            }
          }
        </div>

        <div class="card">
          <h2>Reviews</h2>
          @if (!feedback() || feedback()!.feedbacks.length === 0) {
            <p class="muted">Be the first to review after your order arrives.</p>
          }
          @for (f of feedback()?.feedbacks; track f.id) {
            <div class="menu-item" style="display:block">
              <div class="row between">
                <span class="stars">{{ stars(f.rating) }}</span>
                <span class="muted small-text">{{ f.createdAt | date: 'mediumDate' }}</span>
              </div>
              @if (f.comment) { <p>{{ f.comment }}</p> }
            </div>
          }
          @if (feedback() && feedback()!.totalPages > 1) {
            <div class="pager">
              <button class="btn small" [disabled]="feedback()!.currentPage === 0" (click)="loadFeedback(feedback()!.currentPage - 1)">Prev</button>
              <span class="muted small-text">Page {{ feedback()!.currentPage + 1 }} of {{ feedback()!.totalPages }}</span>
              <button class="btn small" [disabled]="feedback()!.currentPage + 1 >= feedback()!.totalPages" (click)="loadFeedback(feedback()!.currentPage + 1)">Next</button>
            </div>
          }
        </div>
      </div>
    }
  `,
})
export class RestaurantDetailComponent implements OnInit {
  private api = inject(ApiService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private cartService = inject(CartService);
  private toast = inject(ToastService);
  auth = inject(AuthService);

  restaurant = signal<Restaurant | null>(null);
  menu = signal<RestaurantMenu | null>(null);
  feedback = signal<FeedbackPage | null>(null);
  loading = signal(true);
  error = signal('');
  adding = signal<number | null>(null);
  private quantities = signal<Record<number, number>>({});
  private id = 0;

  ngOnInit(): void {
    this.route.paramMap.subscribe((p) => {
      this.id = Number(p.get('id'));
      this.load();
    });
  }

  private load(): void {
    this.loading.set(true);
    this.error.set('');

    this.api.restaurant(this.id).subscribe({
      next: (r) => {
        this.restaurant.set(r);
        this.loading.set(false);
        this.api.menu(this.id).subscribe({ next: (m) => this.menu.set(m), error: (e) => this.toast.err(errMsg(e)) });
        this.loadFeedback(0);
      },
      error: (e) => { this.error.set(errMsg(e)); this.loading.set(false); },
    });
  }

  loadFeedback(page: number): void {
    this.api.feedbackForRestaurant(this.id, page, 5).subscribe({
      next: (f) => this.feedback.set(f),
      error: () => undefined,
    });
  }

  average(): string {
    const list = this.feedback()?.feedbacks ?? [];
    if (!list.length) {
      return '';
    }
    return (list.reduce((s, f) => s + f.rating, 0) / list.length).toFixed(1);
  }

  stars(n: number): string {
    return '★'.repeat(n) + '☆'.repeat(Math.max(0, 5 - n));
  }

  qty(itemId: number): number {
    return this.quantities()[itemId] ?? 1;
  }

  change(itemId: number, delta: number): void {
    const next = Math.min(20, Math.max(1, this.qty(itemId) + delta));
    this.quantities.update((q) => ({ ...q, [itemId]: next }));
  }

  async add(itemId: number, restaurantId: number): Promise<void> {
    if (!this.auth.isLoggedIn()) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url } });
      return;
    }
    this.adding.set(itemId);
    try {
      await this.cartService.add(itemId, restaurantId, this.qty(itemId));
      this.toast.ok('Added to your cart');
      this.quantities.update((q) => ({ ...q, [itemId]: 1 }));
    } catch (e) {
      this.toast.err(errMsg(e));
    } finally {
      this.adding.set(null);
    }
  }
}
