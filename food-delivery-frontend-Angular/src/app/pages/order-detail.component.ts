import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { OrderDetail } from '../core/models';
import { ToastService } from '../core/toast.service';
import { NEXT_STATUS, errMsg, statusClass, statusLabel } from '../core/util';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [CurrencyPipe, DatePipe, FormsModule, RouterLink],
  template: `
    <a [routerLink]="backLink()" class="muted small-text">← Back</a>

    @if (loading()) {
      <div class="spinner">Loading order…</div>
    } @else if (error()) {
      <div class="card mt"><p class="error-text">{{ error() }}</p></div>
    }

    @if (!loading() && !error() && order(); as o) {
      <div class="row between mt mb">
        <div>
          <h1>Order #{{ o.id }}</h1>
          <p class="muted">{{ restaurantName() }} · placed {{ o.createdAt | date: 'medium' }}</p>
        </div>
        <span class="badge" [class]="'badge ' + cls(o.status)" style="font-size:.9rem">{{ label(o.status) }}</span>
      </div>

      <div class="grid two">
        <div class="stack">
          <div class="card">
            <h2>Items</h2>
            <div class="table-wrap">
              <table>
                <tbody>
                  @for (i of o.orderItems; track i.id) {
                    <tr>
                      <td>{{ i.quantity }} × {{ i.itemNameSnapshot }}</td>
                      <td class="right">{{ i.subtotal | currency: 'INR' : 'symbol' : '1.0-2' }}</td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
            <div class="row between mt"><strong>Total</strong><h2 style="margin:0">{{ o.totalAmount | currency: 'INR' : 'symbol' : '1.0-2' }}</h2></div>
            <p class="muted small-text mt">Payment: {{ o.paymentMethod }} · <span class="badge" [class]="'badge ' + cls(o.paymentStatus)">{{ label(o.paymentStatus) }}</span></p>
            <p class="small-text mt"><strong>Deliver to:</strong> {{ o.deliveryAddressSnapshot }}</p>
          </div>

          @if (isStaff() && next(o.status).length) {
            <div class="card">
              <h2>Update status</h2>
              <div class="row">
                @for (s of next(o.status); track s) {
                  <button class="btn small" [class.danger]="s === 'CANCELLED'" [class.primary]="s !== 'CANCELLED'" [disabled]="busy()" (click)="setStatus(s)">
                    {{ s === 'CANCELLED' ? 'Cancel order' : 'Mark ' + label(s) }}
                  </button>
                }
              </div>
            </div>
          }

          @if (canReview(o)) {
            <div class="card">
              <h2>How was your order?</h2>
              @if (reviewed()) {
                <p class="muted">Thanks, your review has been saved.</p>
              } @else {
                <div class="field">
                  <label for="rating">Rating</label>
                  <select id="rating" name="rating" [(ngModel)]="rating">
                    <option [ngValue]="5">★★★★★ Excellent</option>
                    <option [ngValue]="4">★★★★ Good</option>
                    <option [ngValue]="3">★★★ Okay</option>
                    <option [ngValue]="2">★★ Poor</option>
                    <option [ngValue]="1">★ Terrible</option>
                  </select>
                </div>
                <div class="field">
                  <label for="comment">Comment (optional)</label>
                  <textarea id="comment" name="comment" [(ngModel)]="comment" maxlength="1000"></textarea>
                </div>
                <button class="btn primary" [disabled]="busy()" (click)="review(o)">Submit review</button>
              }
            </div>
          }
        </div>

        <div class="card">
          <h2>Status history</h2>
          @if (o.statusHistory.length === 0) {
            <p class="muted">Order placed. Waiting for the restaurant to confirm.</p>
          }
          <ul class="timeline">
            @for (h of o.statusHistory; track h.id) {
              <li>
                <strong>{{ label(h.toStatus) }}</strong>
                <p class="muted small-text">{{ h.changedAt | date: 'medium' }}</p>
              </li>
            }
          </ul>
        </div>
      </div>
    }
  `,
})
export class OrderDetailComponent implements OnInit {
  private api = inject(ApiService);
  private route = inject(ActivatedRoute);
  private toast = inject(ToastService);
  auth = inject(AuthService);

  order = signal<OrderDetail | null>(null);
  restaurantName = signal('');
  loading = signal(true);
  busy = signal(false);
  error = signal('');
  reviewed = signal(false);
  rating = 5;
  comment = '';

  label = statusLabel;
  cls = statusClass;
  next = (s: any) => NEXT_STATUS[s as keyof typeof NEXT_STATUS] || [];

  ngOnInit(): void {
    this.route.paramMap.subscribe((p) => this.load(Number(p.get('id'))));
  }

  isStaff(): boolean {
    return this.auth.role() === 'PARTNER' || this.auth.role() === 'ADMIN';
  }

  backLink(): string {
    return this.auth.role() === 'USER' ? '/orders' : this.auth.role() === 'ADMIN' ? '/admin' : '/partner';
  }

  canReview(o: OrderDetail): boolean {
    return o.status === 'DELIVERED' && o.userId === this.auth.userId;
  }

  private load(id: number): void {
    this.loading.set(true);
    this.error.set('');
    this.api.order(id).subscribe({
      next: (o) => {
        this.order.set(o);
        this.loading.set(false);
        this.api.restaurant(o.restaurantId).subscribe({ next: (r) => this.restaurantName.set(r.name), error: () => this.restaurantName.set('Restaurant #' + o.restaurantId) });
      },
      error: (e) => { this.error.set(errMsg(e)); this.loading.set(false); },
    });
  }

  setStatus(status: string): void {
    const o = this.order();
    if (!o) {
      return;
    }
    this.busy.set(true);
    this.api.updateOrderStatus(o.id, status).subscribe({
      next: () => { this.busy.set(false); this.toast.ok('Order marked ' + statusLabel(status).toLowerCase()); this.load(o.id); },
      error: (e) => { this.busy.set(false); this.toast.err(errMsg(e)); },
    });
  }

  review(o: OrderDetail): void {
    this.busy.set(true);
    this.api.createFeedback({
      userId: this.auth.userId,
      restaurantId: o.restaurantId,
      orderId: o.id,
      rating: Number(this.rating),
      comment: this.comment.trim(),
    }).subscribe({
      next: () => { this.busy.set(false); this.reviewed.set(true); this.toast.ok('Thanks for your feedback!'); },
      error: (e) => {
        this.busy.set(false);
        const msg = errMsg(e);
        if (/already exists/i.test(msg)) {
          this.reviewed.set(true);
        }
        this.toast.err(msg);
      },
    });
  }
}
