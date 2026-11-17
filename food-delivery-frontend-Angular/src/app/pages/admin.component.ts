import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { Category, Cuisine, ManagedRestaurant } from '../core/models';
import { ToastService } from '../core/toast.service';
import { errMsg, statusClass, statusLabel } from '../core/util';

type Tab = 'restaurants' | 'categories' | 'cuisines' | 'delivery' | 'roles';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="page-head"><h1>Admin</h1><p class="muted">Approve restaurants and manage platform settings.</p></div>

    <div class="tabs">
      <button class="tab" [class.active]="tab() === 'restaurants'" (click)="tab.set('restaurants')">Restaurants</button>
      <button class="tab" [class.active]="tab() === 'categories'" (click)="tab.set('categories')">Categories</button>
      <button class="tab" [class.active]="tab() === 'cuisines'" (click)="tab.set('cuisines')">Cuisine types</button>
      <button class="tab" [class.active]="tab() === 'delivery'" (click)="tab.set('delivery')">Delivery fees</button>
      <button class="tab" [class.active]="tab() === 'roles'" (click)="tab.set('roles')">Roles</button>
    </div>

    @if (tab() === 'restaurants') {
      <div class="card">
        <h2>All restaurants</h2>
        @if (restaurants().length === 0) { <p class="muted">No restaurants have been registered yet.</p> }
        <div class="table-wrap">
          <table>
            @if (restaurants().length) {
              <thead><tr><th>Name</th><th>Cuisine</th><th>Contact</th><th>Owner</th><th>Status</th><th></th></tr></thead>
            }
            <tbody>
              @for (r of restaurants(); track r.restaurantId) {
                <tr>
                  <td><strong>{{ r.name }}</strong><br><span class="muted small-text">{{ r.address }}</span></td>
                  <td>{{ r.cuisine }}</td>
                  <td>{{ r.contact }}</td>
                  <td>#{{ r.ownerId }}</td>
                  <td><span class="badge" [class]="'badge ' + cls(r.status)">{{ label(r.status) }}</span></td>
                  <td class="right">
                    <div class="row" style="justify-content:flex-end; gap:.4rem">
                      @if (r.status !== 'APPROVED') { <button class="btn primary small" (click)="approve(r)" [disabled]="busy()">Approve</button> }
                      @if (r.status !== 'REJECTED') { <button class="btn danger small" (click)="reject(r)" [disabled]="busy()">{{ r.status === 'APPROVED' ? 'Suspend' : 'Reject' }}</button> }
                    </div>
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      </div>
    }

    @if (tab() === 'categories') {
      <div class="grid two">
        <div class="card">
          <h2>Menu categories</h2>
          @if (categories().length === 0) { <p class="muted">No categories yet.</p> }
          @for (c of categories(); track c.id) {
            <div class="menu-item" style="display:block">
              @if (editCatId() === c.id) {
                <div class="field"><label>Name</label><input name="cn" [(ngModel)]="catForm.name"></div>
                <div class="field"><label>Description</label><input name="cd" [(ngModel)]="catForm.description"></div>
                <div class="row">
                  <button class="btn primary small" (click)="saveCategory()" [disabled]="busy()">Save</button>
                  <button class="btn small" (click)="editCatId.set(null)">Cancel</button>
                </div>
              } @else {
                <div class="row between">
                  <div style="text-transform:capitalize"><strong>{{ c.name }}</strong><p class="muted small-text" style="text-transform:none">{{ c.description }}</p></div>
                  <div class="row">
                    <button class="btn small" (click)="startEditCategory(c)">Rename</button>
                    <button class="btn danger small" (click)="deactivateCategory(c)" [disabled]="busy()">Remove</button>
                  </div>
                </div>
              }
            </div>
          }
        </div>
        <div class="card">
          <h2>Add category</h2>
          <form (ngSubmit)="createCategory()">
            <div class="field"><label>Name *</label><input name="catname" [(ngModel)]="newCat.name" maxlength="100"></div>
            <div class="field"><label>Description</label><input name="catdesc" [(ngModel)]="newCat.description" maxlength="500"></div>
            <button class="btn primary" type="submit" [disabled]="busy()">Add category</button>
          </form>
        </div>
      </div>
    }

    @if (tab() === 'cuisines') {
      <div class="grid two">
        <div class="card">
          <h2>Cuisine types</h2>
          @if (cuisines().length === 0) { <p class="muted">No cuisine types yet.</p> }
          @for (c of cuisines(); track c.id) {
            <div class="menu-item"><div><strong>{{ c.name }}</strong><p class="muted small-text">{{ c.description }}</p></div></div>
          }
        </div>
        <div class="card">
          <h2>Add cuisine type</h2>
          <form (ngSubmit)="createCuisine()">
            <div class="field"><label>Name *</label><input name="cuname" [(ngModel)]="newCuisine.name" maxlength="100"></div>
            <div class="field"><label>Description *</label><input name="cudesc" [(ngModel)]="newCuisine.description" maxlength="500"></div>
            <button class="btn primary" type="submit" [disabled]="busy()">Add cuisine</button>
          </form>
        </div>
      </div>
    }

    @if (tab() === 'delivery') {
      <div class="card" style="max-width:560px">
        <h2>Delivery fee rules</h2>
        <p class="muted small-text mb">Used when estimating the delivery fee at checkout.</p>
        <form (ngSubmit)="saveRules()">
          <div class="form-grid">
            <div class="field"><label>Base fee (₹)</label><input type="number" step="0.01" min="0.01" name="bf" [(ngModel)]="rules.baseFee"></div>
            <div class="field"><label>Per km rate (₹)</label><input type="number" step="0.01" min="0.01" name="pk" [(ngModel)]="rules.perKmRate"></div>
            <div class="field"><label>Max delivery radius (km)</label><input type="number" step="0.1" min="0.1" name="mr" [(ngModel)]="rules.maxDeliveryRadius"></div>
            <div class="field"><label>Free delivery above (₹)</label><input type="number" step="0.01" min="0" name="fd" [(ngModel)]="rules.freeDeliveryThreshold"></div>
          </div>
          <button class="btn primary" type="submit" [disabled]="busy()">Save rules</button>
        </form>
      </div>
    }

    @if (tab() === 'roles') {
      <div class="card" style="max-width:560px">
        <h2>Create role</h2>
        <p class="muted small-text mb">Role names are uppercase letters, digits and underscores (for example SUPPORT_AGENT).</p>
        <form (ngSubmit)="createRole()">
          <div class="field"><label>Name *</label><input name="rn" [(ngModel)]="newRole.name" placeholder="SUPPORT_AGENT"></div>
          <div class="field"><label>Description * (10+ characters)</label><input name="rd" [(ngModel)]="newRole.description"></div>
          <button class="btn primary" type="submit" [disabled]="busy()">Create role</button>
        </form>
      </div>
    }
  `,
})
export class AdminComponent implements OnInit {
  private api = inject(ApiService);
  private auth = inject(AuthService);
  private toast = inject(ToastService);

  label = statusLabel;
  cls = statusClass;

  tab = signal<Tab>('restaurants');
  busy = signal(false);

  restaurants = signal<ManagedRestaurant[]>([]);
  categories = signal<Category[]>([]);
  cuisines = signal<Cuisine[]>([]);
  editCatId = signal<number | null>(null);
  catForm = { name: '', description: '' };

  newCat = { name: '', description: '' };
  newCuisine = { name: '', description: '' };
  newRole = { name: '', description: '' };
  rules: { baseFee: number | null; perKmRate: number | null; maxDeliveryRadius: number | null; freeDeliveryThreshold: number | null } =
    { baseFee: null, perKmRate: null, maxDeliveryRadius: null, freeDeliveryThreshold: null };

  ngOnInit(): void {
    this.loadRestaurants();
    this.loadCategories();
    this.loadCuisines();
    this.api.deliveryRules().subscribe({
      next: (r) => (this.rules = {
        baseFee: r.baseFee, perKmRate: r.perKmRate, maxDeliveryRadius: r.maxDeliveryRadius, freeDeliveryThreshold: r.freeDeliveryThreshold,
      }),
      error: () => undefined, // no rule saved yet
    });
  }

  private loadRestaurants(): void {
    this.api.managedRestaurants().subscribe({ next: (r) => this.restaurants.set(r), error: (e) => this.toast.err(errMsg(e)) });
  }

  private loadCategories(): void {
    this.api.categories().subscribe({ next: (c) => this.categories.set(c), error: (e) => this.toast.err(errMsg(e)) });
  }

  private loadCuisines(): void {
    this.api.cuisines().subscribe({ next: (c) => this.cuisines.set(c), error: (e) => this.toast.err(errMsg(e)) });
  }

  private run(call: { subscribe: (o: any) => any }, success: string, after: () => void): void {
    this.busy.set(true);
    call.subscribe({
      next: () => { this.busy.set(false); this.toast.ok(success); after(); },
      error: (e: unknown) => { this.busy.set(false); this.toast.err(errMsg(e)); },
    });
  }

  approve(r: ManagedRestaurant): void {
    this.run(this.api.approveRestaurant(r.restaurantId), r.name + ' approved', () => this.loadRestaurants());
  }

  reject(r: ManagedRestaurant): void {
    this.run(this.api.rejectRestaurant(r.restaurantId), r.name + ' ' + (r.status === 'APPROVED' ? 'suspended' : 'rejected'), () => this.loadRestaurants());
  }

  createCategory(): void {
    if (!this.newCat.name.trim()) {
      this.toast.err('Category name is required');
      return;
    }
    this.run(this.api.createCategory({ name: this.newCat.name.trim(), description: this.newCat.description.trim() }), 'Category added', () => {
      this.newCat = { name: '', description: '' };
      this.loadCategories();
    });
  }

  saveCategory(): void {
    const id = this.editCatId();
    if (id === null || !this.catForm.name.trim()) {
      this.toast.err('Category name is required');
      return;
    }
    this.run(this.api.updateCategory(id, this.auth.userId, { name: this.catForm.name.trim(), description: (this.catForm.description || '').trim() }), 'Category updated', () => {
      this.editCatId.set(null);
      this.loadCategories();
    });
  }

  startEditCategory(c: Category): void {
    this.catForm = { name: c.name, description: c.description || '' };
    this.editCatId.set(c.id);
  }

  deactivateCategory(c: Category): void {
    if (!confirm('Remove category "' + c.name + '"?')) {
      return;
    }
    this.run(this.api.deactivateCategory(c.id), 'Category removed', () => this.loadCategories());
  }

  createCuisine(): void {
    if (!this.newCuisine.name.trim() || !this.newCuisine.description.trim()) {
      this.toast.err('Name and description are required');
      return;
    }
    this.run(this.api.createCuisine({ name: this.newCuisine.name.trim(), description: this.newCuisine.description.trim(), active: true }), 'Cuisine added', () => {
      this.newCuisine = { name: '', description: '' };
      this.loadCuisines();
    });
  }

  saveRules(): void {
    const r = this.rules;
    if (r.baseFee == null || r.perKmRate == null || r.maxDeliveryRadius == null || r.freeDeliveryThreshold == null) {
      this.toast.err('Fill in all four fields');
      return;
    }
    this.run(this.api.updateDeliveryRules(this.auth.userId, {
      baseFee: Number(r.baseFee), perKmRate: Number(r.perKmRate),
      maxDeliveryRadius: Number(r.maxDeliveryRadius), freeDeliveryThreshold: Number(r.freeDeliveryThreshold),
    }), 'Delivery rules saved', () => undefined);
  }

  createRole(): void {
    if (!/^[A-Z][A-Z0-9_]{1,49}$/.test(this.newRole.name.trim())) {
      this.toast.err('Role name must be uppercase letters, digits or underscores (2-50 characters)');
      return;
    }
    if (this.newRole.description.trim().length < 10) {
      this.toast.err('Description must be at least 10 characters');
      return;
    }
    this.run(this.api.createRole({ name: this.newRole.name.trim(), description: this.newRole.description.trim() }), 'Role created', () => {
      this.newRole = { name: '', description: '' };
    });
  }
}
