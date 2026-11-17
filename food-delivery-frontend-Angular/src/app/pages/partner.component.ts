import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { Category, Cuisine, ManagedMenuItem, ManagedRestaurant, OrderHistory } from '../core/models';
import { ToastService } from '../core/toast.service';
import { NEXT_STATUS, errMsg, statusClass, statusLabel } from '../core/util';

type Tab = 'restaurants' | 'menu' | 'orders';

@Component({
  selector: 'app-partner',
  standalone: true,
  imports: [CurrencyPipe, DatePipe, FormsModule, RouterLink],
  template: `
    <div class="page-head"><h1>Restaurant dashboard</h1><p class="muted">Manage your restaurants, menus and incoming orders.</p></div>

    <div class="tabs">
      <button class="tab" [class.active]="tab() === 'restaurants'" (click)="tab.set('restaurants')">My restaurants</button>
      <button class="tab" [class.active]="tab() === 'menu'" (click)="openMenu()">Menu</button>
      <button class="tab" [class.active]="tab() === 'orders'" (click)="openOrders()">Orders</button>
    </div>

    @if (tab() === 'restaurants') {
      <div class="grid two">
        <div class="card">
          <h2>Your restaurants</h2>
          @if (restaurants().length === 0) { <p class="muted">You have not added a restaurant yet.</p> }
          @for (r of restaurants(); track r.restaurantId) {
            <div class="menu-item">
              <div>
                <strong>{{ r.name }}</strong>
                <p class="muted small-text">{{ r.address }} · {{ r.cuisine }}</p>
                <p class="muted small-text">🕒 {{ r.hours }} · 📞 {{ r.contact }}</p>
              </div>
              <div class="right">
                <span class="badge" [class]="'badge ' + cls(r.status)">{{ label(r.status) }}</span>
                @if (r.status === 'PENDING') { <p class="muted small-text mt">Waiting for admin approval</p> }
              </div>
            </div>
          }
        </div>

        <div class="card">
          <h2>Add a restaurant</h2>
          <p class="muted small-text mb">New restaurants are reviewed by an admin before customers can see them.</p>
          <form (ngSubmit)="createRestaurant()">
            <div class="field"><label>Name *</label><input name="rname" [(ngModel)]="newRestaurant.name"></div>
            <div class="field"><label>Address *</label><input name="raddr" [(ngModel)]="newRestaurant.address"></div>
            <div class="field">
              <label>Cuisine *</label>
              <input name="rcuisine" list="cuisine-list" [(ngModel)]="newRestaurant.cuisine" placeholder="e.g. Indian">
              <datalist id="cuisine-list">
                @for (c of cuisines(); track c.id) { <option [value]="c.name"></option> }
              </datalist>
            </div>
            <div class="field"><label>Contact (10 digits) *</label><input name="rcontact" [(ngModel)]="newRestaurant.contact" inputmode="numeric"></div>
            <div class="field"><label>Opening hours *</label><input name="rhours" [(ngModel)]="newRestaurant.hours" placeholder="10:00 - 22:00"></div>
            <button class="btn primary" type="submit" [disabled]="busy()">Submit for approval</button>
          </form>
        </div>
      </div>
    }

    @if (tab() === 'menu') {
      <div class="row mb">
        <label class="muted small-text" for="mr">Restaurant</label>
        <select id="mr" name="mr" style="width:auto" [ngModel]="selectedId()" (ngModelChange)="selectRestaurant($event, 'menu')">
          @for (r of restaurants(); track r.restaurantId) { <option [ngValue]="r.restaurantId">{{ r.name }}</option> }
        </select>
      </div>

      @if (restaurants().length === 0) {
        <div class="card empty">Add a restaurant first.</div>
      } @else {
        <div class="grid two">
          <div class="card">
            <h2>Menu items</h2>
            @if (menuLoading()) { <div class="spinner">Loading…</div> }
            @else if (items().length === 0) { <p class="muted">No menu items yet.</p> }
            @for (i of items(); track i.itemId) {
              <div class="menu-item" style="display:block">
                @if (editingId() === i.itemId) {
                  <div class="form-grid">
                    <div class="field"><label>Price</label><input type="number" min="0.01" step="0.01" name="eprice" [(ngModel)]="edit.price"></div>
                    <div class="field"><label>Available</label>
                      <select name="eavail" [(ngModel)]="edit.availability"><option [ngValue]="true">Yes</option><option [ngValue]="false">No</option></select>
                    </div>
                  </div>
                  <div class="field"><label>Description</label><textarea name="edesc" [(ngModel)]="edit.description"></textarea></div>
                  <div class="row">
                    <button class="btn primary small" (click)="saveEdit(i)" [disabled]="busy()">Save</button>
                    <button class="btn small" (click)="editingId.set(null)">Cancel</button>
                  </div>
                } @else {
                  <div class="row between">
                    <div>
                      <strong>{{ i.name }}</strong>
                      <span class="badge" [class.ok]="i.availability" [class.bad]="!i.availability">{{ i.availability ? 'Available' : 'Unavailable' }}</span>
                      <p class="muted small-text">{{ i.description }}</p>
                      <span class="price">{{ i.price | currency: 'INR' : 'symbol' : '1.0-2' }}</span>
                      <span class="muted small-text"> · {{ i.category }}</span>
                    </div>
                    <div class="row">
                      <button class="btn small" (click)="startEdit(i)">Edit</button>
                      <button class="btn danger small" (click)="deleteItem(i)" [disabled]="busy()">Delete</button>
                    </div>
                  </div>
                }
              </div>
            }
          </div>

          <div class="card">
            <h2>Add menu item</h2>
            <form (ngSubmit)="createItem()">
              <div class="field"><label>Name *</label><input name="iname" [(ngModel)]="newItem.name"></div>
              <div class="field"><label>Description *</label><textarea name="idesc" [(ngModel)]="newItem.description"></textarea></div>
              <div class="form-grid">
                <div class="field"><label>Price *</label><input type="number" min="0.01" step="0.01" name="iprice" [(ngModel)]="newItem.price"></div>
                <div class="field"><label>Category *</label>
                  <select name="icat" [(ngModel)]="newItem.categoryId">
                    <option [ngValue]="null" disabled>Choose…</option>
                    @for (c of categories(); track c.id) { <option [ngValue]="c.id">{{ c.name }}</option> }
                  </select>
                </div>
              </div>
              <div class="field"><label><input type="checkbox" name="iavail" [(ngModel)]="newItem.availability"> Available to order</label></div>
              @if (categories().length === 0) { <p class="small-text muted">No categories exist yet. Ask an admin to create some.</p> }
              <button class="btn primary" type="submit" [disabled]="busy()">Add item</button>
            </form>
          </div>
        </div>
      }
    }

    @if (tab() === 'orders') {
      <div class="row mb">
        <label class="muted small-text" for="or">Restaurant</label>
        <select id="or" name="or" style="width:auto" [ngModel]="selectedId()" (ngModelChange)="selectRestaurant($event, 'orders')">
          @for (r of restaurants(); track r.restaurantId) { <option [ngValue]="r.restaurantId">{{ r.name }}</option> }
        </select>
        <select name="ostatus" style="width:auto" [ngModel]="orderStatus()" (ngModelChange)="orderStatus.set($event); loadOrders(0)">
          <option value="">All statuses</option>
          @for (s of statuses; track s) { <option [value]="s">{{ label(s) }}</option> }
        </select>
        <button class="btn small" (click)="loadOrders(orders()?.page ?? 0)">Refresh</button>
      </div>

      @if (ordersLoading()) { <div class="spinner">Loading orders…</div> }
      @else if (!orders() || orders()!.orders.length === 0) { <div class="card empty">No orders found.</div> }
      @else {
        <div class="card">
          <div class="table-wrap">
            <table>
              <thead><tr><th>Order</th><th>Placed</th><th>Status</th><th class="right">Total</th><th>Actions</th></tr></thead>
              <tbody>
                @for (o of orders()!.orders; track o.orderId) {
                  <tr>
                    <td><a [routerLink]="['/orders', o.orderId]">#{{ o.orderId }}</a></td>
                    <td>{{ o.createdAt | date: 'short' }}</td>
                    <td><span class="badge" [class]="'badge ' + cls(o.status)">{{ label(o.status) }}</span></td>
                    <td class="right price">{{ o.totalAmount | currency: 'INR' : 'symbol' : '1.0-2' }}</td>
                    <td>
                      <div class="row" style="gap:.4rem">
                        @for (s of next(o.status); track s) {
                          <button class="btn small" [class.danger]="s === 'CANCELLED'" (click)="setStatus(o.orderId, s)" [disabled]="busy()">
                            {{ s === 'CANCELLED' ? 'Cancel' : label(s) }}
                          </button>
                        }
                      </div>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
          @if (orders()!.totalPages > 1) {
            <div class="pager">
              <button class="btn small" [disabled]="orders()!.page === 0" (click)="loadOrders(orders()!.page - 1)">Prev</button>
              <span class="muted small-text">Page {{ orders()!.page + 1 }} of {{ orders()!.totalPages }}</span>
              <button class="btn small" [disabled]="orders()!.page + 1 >= orders()!.totalPages" (click)="loadOrders(orders()!.page + 1)">Next</button>
            </div>
          }
        </div>
      }
    }
  `,
})
export class PartnerComponent implements OnInit {
  private api = inject(ApiService);
  private auth = inject(AuthService);
  private toast = inject(ToastService);

  readonly statuses = ['PLACED', 'CONFIRMED', 'PREPARING', 'READY', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED'];
  label = statusLabel;
  cls = statusClass;
  next = (s: any) => NEXT_STATUS[s as keyof typeof NEXT_STATUS] || [];

  tab = signal<Tab>('restaurants');
  busy = signal(false);

  restaurants = signal<ManagedRestaurant[]>([]);
  categories = signal<Category[]>([]);
  cuisines = signal<Cuisine[]>([]);
  selectedId = signal<number | null>(null);

  items = signal<ManagedMenuItem[]>([]);
  menuLoading = signal(false);
  editingId = signal<number | null>(null);
  edit: { price?: number; availability?: boolean; description?: string } = {};

  orders = signal<OrderHistory | null>(null);
  ordersLoading = signal(false);
  orderStatus = signal('');

  newRestaurant = { name: '', address: '', cuisine: '', contact: '', hours: '' };
  newItem: { name: string; description: string; price: number | null; availability: boolean; categoryId: number | null } =
    { name: '', description: '', price: null, availability: true, categoryId: null };

  ngOnInit(): void {
    this.api.categories().subscribe({ next: (c) => this.categories.set(c), error: () => undefined });
    this.api.cuisines().subscribe({ next: (c) => this.cuisines.set(c), error: () => undefined });
    this.loadRestaurants();
  }

  loadRestaurants(): void {
    this.api.managedRestaurants().subscribe({
      next: (list) => {
        this.restaurants.set(list);
        if (this.selectedId() === null && list.length) {
          this.selectedId.set(list[0].restaurantId);
        }
      },
      error: (e) => this.toast.err(errMsg(e)),
    });
  }

  createRestaurant(): void {
    const r = this.newRestaurant;
    if (!r.name.trim() || !r.address.trim() || !r.cuisine.trim() || !r.hours.trim()) {
      this.toast.err('Please fill in all restaurant fields');
      return;
    }
    if (!/^\d{10}$/.test(r.contact.trim())) {
      this.toast.err('Contact must be exactly 10 digits');
      return;
    }
    this.busy.set(true);
    this.api.createRestaurant({
      name: r.name.trim(), address: r.address.trim(), cuisine: r.cuisine.trim(),
      contact: r.contact.trim(), hours: r.hours.trim(), ownerId: this.auth.userId,
    }).subscribe({
      next: () => {
        this.busy.set(false);
        this.toast.ok('Restaurant submitted for approval');
        this.newRestaurant = { name: '', address: '', cuisine: '', contact: '', hours: '' };
        this.loadRestaurants();
      },
      error: (e) => { this.busy.set(false); this.toast.err(errMsg(e)); },
    });
  }

  // ----- menu -----
  openMenu(): void {
    this.tab.set('menu');
    this.loadMenu();
  }

  selectRestaurant(id: number, tab: 'menu' | 'orders'): void {
    this.selectedId.set(id);
    if (tab === 'menu') {
      this.loadMenu();
    } else {
      this.loadOrders(0);
    }
  }

  loadMenu(): void {
    const id = this.selectedId();
    if (id === null) {
      return;
    }
    this.menuLoading.set(true);
    this.api.managedMenu(id).subscribe({
      next: (m) => { this.items.set(m); this.menuLoading.set(false); },
      error: (e) => { this.menuLoading.set(false); this.toast.err(errMsg(e)); },
    });
  }

  createItem(): void {
    const id = this.selectedId();
    const i = this.newItem;
    if (id === null || !i.name.trim() || !i.description.trim() || !i.price || i.price <= 0 || !i.categoryId) {
      this.toast.err('Name, description, price and category are required');
      return;
    }
    this.busy.set(true);
    this.api.createMenuItem(id, {
      name: i.name.trim(), description: i.description.trim(), price: Number(i.price),
      availability: i.availability, categoryId: i.categoryId, adminId: this.auth.userId,
    }).subscribe({
      next: () => {
        this.busy.set(false);
        this.toast.ok('Menu item added');
        this.newItem = { name: '', description: '', price: null, availability: true, categoryId: null };
        this.loadMenu();
      },
      error: (e) => { this.busy.set(false); this.toast.err(errMsg(e)); },
    });
  }

  startEdit(i: ManagedMenuItem): void {
    this.edit = { price: i.price, availability: i.availability, description: i.description };
    this.editingId.set(i.itemId);
  }

  saveEdit(i: ManagedMenuItem): void {
    this.busy.set(true);
    this.api.updateMenuItem(i.itemId, this.auth.userId, {
      price: Number(this.edit.price), availability: this.edit.availability, description: this.edit.description,
    }).subscribe({
      next: () => { this.busy.set(false); this.editingId.set(null); this.toast.ok('Menu item updated'); this.loadMenu(); },
      error: (e) => { this.busy.set(false); this.toast.err(errMsg(e)); },
    });
  }

  deleteItem(i: ManagedMenuItem): void {
    if (!confirm('Delete "' + i.name + '"?')) {
      return;
    }
    this.busy.set(true);
    this.api.deleteMenuItem(i.itemId, this.auth.userId).subscribe({
      next: () => { this.busy.set(false); this.toast.ok('Menu item deleted'); this.loadMenu(); },
      error: (e) => { this.busy.set(false); this.toast.err(errMsg(e)); },
    });
  }

  // ----- orders -----
  openOrders(): void {
    this.tab.set('orders');
    this.loadOrders(0);
  }

  loadOrders(page: number): void {
    const id = this.selectedId();
    if (id === null) {
      return;
    }
    this.ordersLoading.set(true);
    this.api.restaurantOrders(id, page, 10, this.orderStatus() || undefined).subscribe({
      next: (o) => { this.orders.set(o); this.ordersLoading.set(false); },
      error: (e) => { this.ordersLoading.set(false); this.toast.err(errMsg(e)); },
    });
  }

  setStatus(orderId: number, status: string): void {
    this.busy.set(true);
    this.api.updateOrderStatus(orderId, status).subscribe({
      next: () => { this.busy.set(false); this.toast.ok('Order updated'); this.loadOrders(this.orders()?.page ?? 0); },
      error: (e) => { this.busy.set(false); this.toast.err(errMsg(e)); },
    });
  }
}
