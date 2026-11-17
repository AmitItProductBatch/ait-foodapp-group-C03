import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { OrderHistory } from '../core/models';
import { ToastService } from '../core/toast.service';
import { errMsg, statusClass, statusLabel } from '../core/util';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CurrencyPipe, DatePipe, FormsModule, RouterLink],
  template: `
    <div class="row between page-head">
      <h1>My orders</h1>
      <select style="width:auto" name="status" [ngModel]="status()" (ngModelChange)="setStatus($event)">
        <option value="">All statuses</option>
        @for (s of statuses; track s) { <option [value]="s">{{ label(s) }}</option> }
      </select>
    </div>

    @if (loading()) {
      <div class="spinner">Loading orders…</div>
    } @else if (!history() || history()!.orders.length === 0) {
      <div class="card empty"><p>No orders yet.</p><a class="btn primary" routerLink="/">Order something tasty</a></div>
    } @else {
      <div class="card">
        <div class="table-wrap">
          <table>
            <thead><tr><th>Order</th><th>Restaurant</th><th>Placed</th><th>Status</th><th class="right">Total</th><th></th></tr></thead>
            <tbody>
              @for (o of history()!.orders; track o.orderId) {
                <tr>
                  <td>#{{ o.orderId }}</td>
                  <td>{{ restaurantName(o.restaurantId) }}</td>
                  <td>{{ o.createdAt | date: 'medium' }}</td>
                  <td><span class="badge" [class]="'badge ' + cls(o.status)">{{ label(o.status) }}</span></td>
                  <td class="right price">{{ o.totalAmount | currency: 'INR' : 'symbol' : '1.0-2' }}</td>
                  <td class="right"><a class="btn small" [routerLink]="['/orders', o.orderId]">View</a></td>
                </tr>
              }
            </tbody>
          </table>
        </div>

        @if (history()!.totalPages > 1) {
          <div class="pager">
            <button class="btn small" [disabled]="history()!.page === 0" (click)="load(history()!.page - 1)">Prev</button>
            <span class="muted small-text">Page {{ history()!.page + 1 }} of {{ history()!.totalPages }}</span>
            <button class="btn small" [disabled]="history()!.page + 1 >= history()!.totalPages" (click)="load(history()!.page + 1)">Next</button>
          </div>
        }
      </div>
    }
  `,
})
export class OrdersComponent implements OnInit {
  private api = inject(ApiService);
  private auth = inject(AuthService);
  private toast = inject(ToastService);

  readonly statuses = ['PLACED', 'CONFIRMED', 'PREPARING', 'READY', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED'];
  history = signal<OrderHistory | null>(null);
  status = signal('');
  loading = signal(true);
  private names = signal<Record<number, string>>({});

  label = statusLabel;
  cls = statusClass;

  ngOnInit(): void {
    this.api.restaurants().subscribe({
      next: (list) => this.names.set(Object.fromEntries(list.map((r) => [r.restaurantId, r.name]))),
      error: () => undefined,
    });
    this.load(0);
  }

  restaurantName(id: number): string {
    return this.names()[id] || 'Restaurant #' + id;
  }

  setStatus(value: string): void {
    this.status.set(value);
    this.load(0);
  }

  load(page: number): void {
    this.loading.set(true);
    this.api.userOrders(this.auth.userId, page, 10, this.status() || undefined).subscribe({
      next: (h) => { this.history.set(h); this.loading.set(false); },
      error: (e) => { this.toast.err(errMsg(e)); this.loading.set(false); },
    });
  }
}
