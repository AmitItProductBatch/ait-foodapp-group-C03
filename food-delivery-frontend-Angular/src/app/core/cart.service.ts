import { Injectable, computed, inject, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { ApiService } from './api.service';
import { AuthService } from './auth.service';
import { Cart } from './models';

@Injectable({ providedIn: 'root' })
export class CartService {
  private api = inject(ApiService);
  private auth = inject(AuthService);

  readonly cart = signal<Cart | null>(null);
  readonly count = computed(() => (this.cart()?.items ?? []).reduce((n, i) => n + i.quantity, 0));

  async refresh(): Promise<Cart | null> {
    if (!this.auth.isLoggedIn()) {
      this.cart.set(null);
      return null;
    }
    const c = await firstValueFrom(this.api.cart());
    this.cart.set(c);
    return c;
  }

  clearLocal(): void {
    this.cart.set(null);
  }

  /** Adds an item. Creates the cart on first use and bumps the quantity if the item is already in it. */
  async add(menuItemId: number, restaurantId: number, quantity: number): Promise<void> {
    let cart = await this.refresh();
    const userId = this.auth.userId;

    if (!cart || cart.id == null) {
      cart = await firstValueFrom(this.api.createCart(userId, restaurantId));
    }

    const existing = cart.items?.find((i) => i.itemId === menuItemId);
    if (existing) {
      await firstValueFrom(this.api.updateCartItem(existing.cartItemId, userId, existing.quantity + quantity));
    } else {
      await firstValueFrom(this.api.addCartItem(cart.id as number, menuItemId, quantity));
    }
    await this.refresh();
  }

  async setQuantity(cartItemId: number, quantity: number): Promise<void> {
    await firstValueFrom(this.api.updateCartItem(cartItemId, this.auth.userId, quantity));
    await this.refresh();
  }

  async remove(cartItemId: number): Promise<void> {
    await firstValueFrom(this.api.removeCartItem(cartItemId, this.auth.userId));
    await this.refresh();
  }

  async clear(): Promise<void> {
    await firstValueFrom(this.api.clearCart(this.auth.userId));
    await this.refresh();
  }
}
