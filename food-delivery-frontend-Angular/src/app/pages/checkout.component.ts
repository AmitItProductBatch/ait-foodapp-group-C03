import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { CartService } from '../core/cart.service';
import { Address, DeliveryFee } from '../core/models';
import { ToastService } from '../core/toast.service';
import { errMsg } from '../core/util';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CurrencyPipe, FormsModule, RouterLink],
  template: `
    <div class="page-head"><h1>Checkout</h1></div>

    @if (loading()) {
      <div class="spinner">Loading…</div>
    } @else if (!cartService.cart() || cartService.cart()!.items.length === 0) {
      <div class="card empty">
        <p>Your cart is empty.</p>
        <a class="btn primary" routerLink="/">Browse restaurants</a>
      </div>
    } @else {
      <div class="grid two">
        <div class="stack">
          <div class="card">
            <div class="row between">
              <h2>Delivery address</h2>
              <button class="btn small" (click)="showForm.set(!showForm())">{{ showForm() ? 'Cancel' : '+ New address' }}</button>
            </div>

            @if (addresses().length === 0 && !showForm()) {
              <p class="muted">You have no saved addresses yet. Add one to continue.</p>
            }

            @for (a of addresses(); track a.id) {
              <label class="menu-item" style="cursor:pointer; align-items:flex-start">
                <div class="row" style="align-items:flex-start; flex-wrap:nowrap">
                  <input type="radio" name="address" [value]="a.id" [checked]="selected() === a.id" (change)="selected.set(a.id!)" style="width:auto; margin-top:.3rem">
                  <div>
                    <strong>{{ a.addressLabel || 'Address' }}</strong>
                    <p class="muted small-text">{{ format(a) }}</p>
                  </div>
                </div>
              </label>
            }

            @if (showForm()) {
              <form class="mt" (ngSubmit)="saveAddress()">
                <div class="form-grid">
                  <div class="field"><label>Label</label><input name="label" [(ngModel)]="form.addressLabel" placeholder="Home, Work…"></div>
                  <div class="field"><label>Street address *</label><input name="street" [(ngModel)]="form.streetAddress"></div>
                  <div class="field"><label>Apartment / flat</label><input name="apt" [(ngModel)]="form.apartment"></div>
                  <div class="field"><label>Landmark</label><input name="landmark" [(ngModel)]="form.landmark"></div>
                  <div class="field"><label>City *</label><input name="city" [(ngModel)]="form.city"></div>
                  <div class="field"><label>Postal code</label><input name="postal" [(ngModel)]="form.postalCode"></div>
                </div>
                <div class="field"><label>Delivery instructions</label><textarea name="instr" [(ngModel)]="form.deliveryInstructions"></textarea></div>
                <button class="btn primary" type="submit" [disabled]="busy()">Save address</button>
              </form>
            }
          </div>

          <div class="card">
            <h2>Payment</h2>
            <div class="field">
              <label for="pay">Payment method</label>
              <select id="pay" name="pay" [(ngModel)]="payment">
                <option value="COD">Cash on delivery</option>
                <option value="UPI">UPI</option>
                <option value="CARD">Card</option>
              </select>
            </div>
          </div>
        </div>

        <div class="card">
          <h2>Order summary</h2>
          @for (i of cartService.cart()!.items; track i.cartItemId) {
            <div class="row between small-text" style="padding:.25rem 0">
              <span>{{ i.quantity }} × {{ i.itemName }}</span>
              <span>{{ i.subtotal | currency: 'INR' : 'symbol' : '1.0-2' }}</span>
            </div>
          }
          <hr style="border:none; border-top:1px solid var(--line)">
          <div class="row between">
            <strong>Total</strong>
            <h2 style="margin:0">{{ cartService.cart()!.totalAmount | currency: 'INR' : 'symbol' : '1.0-2' }}</h2>
          </div>

          <div class="mt">
            <button class="btn small" (click)="estimateFee()" [disabled]="!selected() || feeBusy()">
              {{ feeBusy() ? 'Estimating…' : 'Estimate delivery fee' }}
            </button>
            @if (fee(); as f) {
              <p class="small-text mt">
                {{ f.distanceKm.toFixed(1) }} km away ·
                @if (f.freeDeliveryApplied) { <strong>Free delivery</strong> } @else { delivery fee <strong>{{ f.deliveryFee | currency: 'INR' : 'symbol' : '1.0-2' }}</strong> }
              </p>
            }
            @if (feeError()) { <p class="small-text muted mt">{{ feeError() }}</p> }
          </div>

          @for (e of problems(); track e) { <p class="error-text">{{ e }}</p> }

          <button class="btn primary mt" style="width:100%" [disabled]="busy() || !selected()" (click)="placeOrder()">
            {{ busy() ? 'Placing order…' : 'Place order' }}
          </button>
        </div>
      </div>
    }
  `,
})
export class CheckoutComponent implements OnInit {
  cartService = inject(CartService);
  private api = inject(ApiService);
  private auth = inject(AuthService);
  private router = inject(Router);
  private toast = inject(ToastService);

  loading = signal(true);
  busy = signal(false);
  feeBusy = signal(false);
  showForm = signal(false);
  addresses = signal<Address[]>([]);
  selected = signal<number | null>(null);
  fee = signal<DeliveryFee | null>(null);
  feeError = signal('');
  problems = signal<string[]>([]);
  payment = 'COD';
  form: Address = {};

  async ngOnInit(): Promise<void> {
    try {
      await this.cartService.refresh();
      this.addresses.set(await this.fetchAddresses());
      if (this.addresses().length) {
        this.selected.set(this.addresses()[0].id ?? null);
      }
    } catch (e) {
      this.toast.err(errMsg(e));
    } finally {
      this.loading.set(false);
    }
  }

  private fetchAddresses(): Promise<Address[]> {
    return new Promise((resolve, reject) =>
      this.api.addresses(this.auth.userId).subscribe({ next: resolve, error: reject }),
    );
  }

  format(a: Address): string {
    return [a.streetAddress, a.apartment, a.landmark, a.city, a.postalCode].filter((x) => !!x).join(', ');
  }

  saveAddress(): void {
    if (!this.form.streetAddress?.trim() || !this.form.city?.trim()) {
      this.toast.err('Street address and city are required');
      return;
    }
    this.busy.set(true);
    this.api.addAddress(this.auth.userId, this.form).subscribe({
      next: (a) => {
        this.busy.set(false);
        this.addresses.update((list) => [...list, a]);
        this.selected.set(a.id ?? null);
        this.form = {};
        this.showForm.set(false);
        this.toast.ok('Address saved');
      },
      error: (e) => { this.busy.set(false); this.toast.err(errMsg(e)); },
    });
  }

  estimateFee(): void {
    const cart = this.cartService.cart();
    if (!cart?.restaurantId || !this.selected()) {
      return;
    }
    this.feeBusy.set(true);
    this.feeError.set('');
    this.fee.set(null);
    this.api.deliveryFee(cart.restaurantId, this.selected() as number).subscribe({
      next: (f) => { this.fee.set(f); this.feeBusy.set(false); },
      error: (e) => { this.feeError.set('Could not estimate the delivery fee: ' + errMsg(e)); this.feeBusy.set(false); },
    });
  }

  placeOrder(): void {
    const cart = this.cartService.cart();
    if (!cart || !cart.restaurantId || !this.selected()) {
      return;
    }
    this.busy.set(true);
    this.problems.set([]);

    // Ask the server to validate first so the person sees exactly what is wrong.
    this.api.validateOrder({
      userId: this.auth.userId,
      restaurantId: cart.restaurantId,
      items: cart.items.map((i) => ({ menuItemId: i.itemId, quantity: i.quantity, unitPrice: i.unitPrice })),
    }).subscribe({
      next: (v) => {
        if (!v.valid) {
          this.problems.set(v.errors);
          this.busy.set(false);
          return;
        }
        this.api.placeOrder({ userId: this.auth.userId, addressId: this.selected() as number, paymentMethod: this.payment }).subscribe({
          next: (order) => {
            this.busy.set(false);
            this.toast.ok('Order placed!');
            this.cartService.refresh().catch(() => undefined);
            this.router.navigate(['/orders', order.id]);
          },
          error: (e) => { this.busy.set(false); this.toast.err(errMsg(e)); },
        });
      },
      error: (e) => { this.busy.set(false); this.toast.err(errMsg(e)); },
    });
  }
}
