import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../core/api.service';
import { Cuisine, Restaurant } from '../core/models';
import { errMsg } from '../core/util';

@Component({
  selector: 'app-restaurants',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="hero">
      <h1>Hungry? We’ve got you.</h1>
      <p>Pick a restaurant, build your order, and track it to your door.</p>
    </div>

    <div class="row mb">
      <input class="grow" style="max-width:420px" placeholder="Search restaurants or cuisines…" [ngModel]="search()" (ngModelChange)="search.set($event)" name="search">
    </div>

    <div class="chips mb">
      <button class="chip" [class.active]="cuisine() === ''" (click)="selectCuisine('')">All</button>
      @for (c of cuisines(); track c.id) {
        <button class="chip" [class.active]="cuisine() === c.name" (click)="selectCuisine(c.name)">{{ c.name }}</button>
      }
    </div>

    @if (loading()) {
      <div class="spinner">Loading restaurants…</div>
    } @else if (error()) {
      <div class="card"><p class="error-text">{{ error() }}</p><button class="btn" (click)="load()">Try again</button></div>
    } @else if (filtered().length === 0) {
      <div class="empty">No restaurants found.</div>
    } @else {
      <div class="grid">
        @for (r of filtered(); track r.restaurantId) {
          <div class="card clickable" (click)="open(r.restaurantId)">
            <div class="row between">
              <h3>{{ r.name }}</h3>
              @if (r.rating) { <span class="badge warn">★ {{ r.rating.toFixed(1) }}</span> } @else { <span class="badge">New</span> }
            </div>
            <p class="muted small-text">{{ r.address }}</p>
            <div class="row between mt">
              <span class="badge info">{{ r.cuisine }}</span>
              <span class="muted small-text">📞 {{ r.contact }}</span>
            </div>
          </div>
        }
      </div>
    }
  `,
})
export class RestaurantsComponent implements OnInit {
  private api = inject(ApiService);
  private router = inject(Router);

  restaurants = signal<Restaurant[]>([]);
  cuisines = signal<Cuisine[]>([]);
  cuisine = signal('');
  search = signal('');
  loading = signal(true);
  error = signal('');

  filtered = computed(() => {
    const q = this.search().trim().toLowerCase();
    if (!q) {
      return this.restaurants();
    }
    return this.restaurants().filter(
      (r) => r.name.toLowerCase().includes(q) || (r.cuisine || '').toLowerCase().includes(q) || (r.address || '').toLowerCase().includes(q),
    );
  });

  ngOnInit(): void {
    this.api.cuisines().subscribe({ next: (c) => this.cuisines.set(c.filter((x) => x.active !== false)), error: () => undefined });
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set('');
    const call = this.cuisine() ? this.api.restaurantsByCuisine(this.cuisine()) : this.api.restaurants();
    call.subscribe({
      next: (list) => { this.restaurants.set(list); this.loading.set(false); },
      error: (e) => { this.error.set(errMsg(e)); this.loading.set(false); },
    });
  }

  selectCuisine(name: string): void {
    this.cuisine.set(name);
    this.load();
  }

  open(id: number): void {
    this.router.navigate(['/restaurants', id]);
  }
}
