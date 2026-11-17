import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';
import { CartService } from '../core/cart.service';
import { ToastService } from '../core/toast.service';
import { errMsg } from '../core/util';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CurrencyPipe, RouterLink],
  template: `
    <div class="page-head">
      <h1>Your cart</h1>
      @if (restaurantName()) { <p class="muted">Ordering from <strong>{{ restaurantName() }}</strong></p> }
    </div>

    @if (loading()) {
      <div class="spinner">Loading your cart…</div>
    } @else if (!cartService.cart() || cartService.cart()!.items.length === 0) {
      <div class="card empty">
        <p>Your cart is empty.</p>
        <a class="btn primary" routerLink="/">Browse restaurants</a>
      </div>
    } @else {
      <div class="card">
        <div class="table-wrap">
          <table>
            <thead><tr><th>Item</th><th>Price</th><th>Quantity</th><th class="right">Subtotal</th><th></th></tr></thead>
            <tbody>
              @for (i of cartService.cart()!.items; track i.cartItemId) {
                <tr>
                  <td><strong>{{ i.itemName || ('Item #' + i.itemId) }}</strong></td>
                  <td>{{ i.unitPrice | currency: 'INR' : 'symbol' : '1.0-2' }}</td>
                  <td>
                    <div class="qty">
                      <button (click)="setQty(i.cartItemId, i.quantity - 1)" [disabled]="busy()">−</button>
                      <span>{{ i.quantity }}</span>
                      <button (click)="setQty(i.cartItemId, i.quantity + 1)" [disabled]="busy()">+</button>
                    </div>
                  </td>
                  <td class="right price">{{ i.subtotal | currency: 'INR' : 'symbol' : '1.0-2' }}</td>
                  <td class="right"><button class="btn danger small" (click)="remove(i.cartItemId)" [disabled]="busy()">Remove</button></td>
                </tr>
              }
            </tbody>
          </table>
        </div>

        <div class="row between mt">
          <button class="btn danger" (click)="clear()" [disabled]="busy()">Clear cart</button>
          <div class="right">
            <p class="muted small-text">Total</p>
            <h2 style="margin:0">{{ cartService.cart()!.totalAmount | currency: 'INR' : 'symbol' : '1.0-2' }}</h2>
          </div>
        </div>
        <div class="right mt">
          <a class="btn primary" routerLink="/checkout">Proceed to checkout →</a>
        </div>
      </div>
    }
  `,
})
export class CartComponent implements OnInit {
  cartService = inject(CartService);
  private api = inject(ApiService);
  private toast = inject(ToastService);
  private router = inject(Router);

  loading = signal(true);
  busy = signal(false);
  restaurantName = signal('');

  ngOnInit(): void {
    this.cartService.refresh()
      .then((c) => {
        if (c?.restaurantId) {
          this.api.restaurant(c.restaurantId).subscribe({ next: (r) => this.restaurantName.set(r.name), error: () => undefined });
        }
      })
      .catch((e) => this.toast.err(errMsg(e)))
      .finally(() => this.loading.set(false));
  }

  async setQty(cartItemId: number, quantity: number): Promise<void> {
    if (quantity < 1) {
      return this.remove(cartItemId);
    }
    await this.run(() => this.cartService.setQuantity(cartItemId, quantity));
  }

  async remove(cartItemId: number): Promise<void> {
    await this.run(() => this.cartService.remove(cartItemId));
  }

  async clear(): Promise<void> {
    await this.run(() => this.cartService.clear());
    this.restaurantName.set('');
  }

  private async run(action: () => Promise<void>): Promise<void> {
    this.busy.set(true);
    try {
      await action();
    } catch (e) {
      this.toast.err(errMsg(e));
    } finally {
      this.busy.set(false);
    }
  }
}
