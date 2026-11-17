import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './core/guards';

export const routes: Routes = [
  { path: '', title: 'Restaurants', loadComponent: () => import('./pages/restaurants.component').then((m) => m.RestaurantsComponent) },
  { path: 'restaurants/:id', title: 'Restaurant', loadComponent: () => import('./pages/restaurant-detail.component').then((m) => m.RestaurantDetailComponent) },
  { path: 'login', title: 'Log in', loadComponent: () => import('./pages/login.component').then((m) => m.LoginComponent) },
  { path: 'register', title: 'Sign up', loadComponent: () => import('./pages/register.component').then((m) => m.RegisterComponent) },
  { path: 'cart', title: 'Your cart', canActivate: [authGuard], loadComponent: () => import('./pages/cart.component').then((m) => m.CartComponent) },
  { path: 'checkout', title: 'Checkout', canActivate: [authGuard], loadComponent: () => import('./pages/checkout.component').then((m) => m.CheckoutComponent) },
  { path: 'orders', title: 'My orders', canActivate: [authGuard], loadComponent: () => import('./pages/orders.component').then((m) => m.OrdersComponent) },
  { path: 'orders/:id', title: 'Order', canActivate: [authGuard], loadComponent: () => import('./pages/order-detail.component').then((m) => m.OrderDetailComponent) },
  { path: 'profile', title: 'My profile', canActivate: [authGuard], loadComponent: () => import('./pages/profile.component').then((m) => m.ProfileComponent) },
  {
    path: 'partner', title: 'Restaurant dashboard', canActivate: [roleGuard], data: { roles: ['PARTNER'] },
    loadComponent: () => import('./pages/partner.component').then((m) => m.PartnerComponent),
  },
  {
    path: 'admin', title: 'Admin', canActivate: [roleGuard], data: { roles: ['ADMIN'] },
    loadComponent: () => import('./pages/admin.component').then((m) => m.AdminComponent),
  },
  { path: '**', redirectTo: '' },
];
