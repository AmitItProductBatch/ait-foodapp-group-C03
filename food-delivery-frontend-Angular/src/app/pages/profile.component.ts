import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { Address } from '../core/models';
import { ToastService } from '../core/toast.service';
import { errMsg } from '../core/util';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="page-head"><h1>My profile</h1><p class="muted">{{ auth.user()?.email }} · <span class="badge info">{{ auth.role() }}</span></p></div>

    <div class="grid two">
      <div class="card">
        <h2>Details</h2>
        <form (ngSubmit)="saveProfile()">
          <div class="field"><label>Name</label><input name="name" [(ngModel)]="name"></div>
          <div class="field"><label>Mobile number</label><input name="phone" [(ngModel)]="phone"></div>
          <button class="btn primary" type="submit" [disabled]="busy()">Save changes</button>
        </form>

        <hr style="border:none; border-top:1px solid var(--line); margin:1.25rem 0">
        <h3>Deactivate account</h3>
        <p class="muted small-text">You will be logged out and will no longer be able to log in.</p>
        <button class="btn danger" (click)="deactivate()" [disabled]="busy()">Deactivate my account</button>
      </div>

      <div class="card">
        <div class="row between">
          <h2>Addresses</h2>
          <button class="btn small" (click)="startNew()">+ New address</button>
        </div>

        @if (addresses().length === 0 && !editing()) { <p class="muted">No saved addresses.</p> }

        @for (a of addresses(); track a.id) {
          <div class="menu-item">
            <div>
              <strong>{{ a.addressLabel || 'Address' }}</strong>
              <p class="muted small-text">{{ [a.streetAddress, a.apartment, a.landmark, a.city, a.postalCode].filter(present).join(', ') }}</p>
            </div>
            <div><button class="btn small" (click)="edit(a)">Edit</button></div>
          </div>
        }

        @if (editing()) {
          <form class="mt" (ngSubmit)="saveAddress()">
            <h3>{{ form.id ? 'Edit address' : 'New address' }}</h3>
            <div class="form-grid">
              <div class="field"><label>Label</label><input name="label" [(ngModel)]="form.addressLabel"></div>
              <div class="field"><label>Street address *</label><input name="street" [(ngModel)]="form.streetAddress"></div>
              <div class="field"><label>Apartment / flat</label><input name="apt" [(ngModel)]="form.apartment"></div>
              <div class="field"><label>Landmark</label><input name="landmark" [(ngModel)]="form.landmark"></div>
              <div class="field"><label>City *</label><input name="city" [(ngModel)]="form.city"></div>
              <div class="field"><label>Postal code</label><input name="postal" [(ngModel)]="form.postalCode"></div>
            </div>
            <div class="field"><label>Delivery instructions</label><textarea name="instr" [(ngModel)]="form.deliveryInstructions"></textarea></div>
            <div class="row">
              <button class="btn primary" type="submit" [disabled]="busy()">Save address</button>
              <button class="btn" type="button" (click)="editing.set(false)">Cancel</button>
            </div>
          </form>
        }
      </div>
    </div>
  `,
})
export class ProfileComponent implements OnInit {
  private api = inject(ApiService);
  private toast = inject(ToastService);
  auth = inject(AuthService);

  name = '';
  phone = '';
  addresses = signal<Address[]>([]);
  editing = signal(false);
  busy = signal(false);
  form: Address = {};

  present = (x: unknown) => !!x;

  ngOnInit(): void {
    const u = this.auth.user();
    this.name = u?.name ?? '';
    this.phone = u?.phonenumber ?? '';

    this.api.getUser(this.auth.userId).subscribe({
      next: (me) => { this.name = me.name; this.phone = me.phonenumber; },
      error: (e) => this.toast.err(errMsg(e)),
    });
    this.loadAddresses();
  }

  private loadAddresses(): void {
    this.api.addresses(this.auth.userId).subscribe({
      next: (a) => this.addresses.set(a),
      error: (e) => this.toast.err(errMsg(e)),
    });
  }

  saveProfile(): void {
    if (!this.name.trim()) {
      this.toast.err('Name cannot be empty');
      return;
    }
    this.busy.set(true);
    this.api.updateProfile(this.auth.userId, { name: this.name.trim(), phonenumber: this.phone.trim() }).subscribe({
      next: () => {
        this.busy.set(false);
        this.auth.refreshUser({ name: this.name.trim(), phonenumber: this.phone.trim() });
        this.toast.ok('Profile updated');
      },
      error: (e) => { this.busy.set(false); this.toast.err(errMsg(e)); },
    });
  }

  startNew(): void {
    this.form = {};
    this.editing.set(true);
  }

  edit(a: Address): void {
    this.form = { ...a };
    this.editing.set(true);
  }

  saveAddress(): void {
    if (!this.form.streetAddress?.trim() || !this.form.city?.trim()) {
      this.toast.err('Street address and city are required');
      return;
    }
    this.busy.set(true);

    const done = () => {
      this.busy.set(false);
      this.editing.set(false);
      this.toast.ok('Address saved');
      this.loadAddresses();
    };
    const fail = (e: unknown) => { this.busy.set(false); this.toast.err(errMsg(e)); };

    if (this.form.id) {
      this.api.updateProfile(this.auth.userId, { adressUpdateDto: this.form }).subscribe({ next: done, error: fail });
    } else {
      this.api.addAddress(this.auth.userId, this.form).subscribe({ next: done, error: fail });
    }
  }

  deactivate(): void {
    if (!confirm('Deactivate your account? You will not be able to log in again.')) {
      return;
    }
    this.busy.set(true);
    this.api.deactivateUser(this.auth.userId).subscribe({
      next: () => { this.toast.ok('Account deactivated'); this.auth.logout(); },
      error: (e) => { this.busy.set(false); this.toast.err(errMsg(e)); },
    });
  }
}
