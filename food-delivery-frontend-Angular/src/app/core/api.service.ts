import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  Address, Cart, Category, Cuisine, DeliveryFee, DeliveryRule, Feedback, FeedbackPage, LoginResponse,
  ManagedMenuItem, ManagedRestaurant, OrderDetail, OrderHistory, Restaurant, RestaurantMenu,
  SessionUser, ValidationResult,
} from './models';

/**
 * One method per backend endpoint. The base path is relative (/api) so the same build works
 * behind the Angular dev proxy and behind nginx in Docker.
 */
@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);
  private base = '/api';

  // ---------- users ----------
  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.base}/users/login`, { email, password });
  }

  register(body: { name: string; email: string; password: string; phonenumber: string; role: string }): Observable<SessionUser> {
    return this.http.post<SessionUser>(`${this.base}/users/register`, body);
  }

  getUser(id: number): Observable<SessionUser> {
    return this.http.get<SessionUser>(`${this.base}/users/${id}`);
  }

  updateProfile(id: number, body: { name?: string; phonenumber?: string; adressUpdateDto?: Address }): Observable<SessionUser> {
    return this.http.put<SessionUser>(`${this.base}/users/${id}`, body);
  }

  deactivateUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/users/${id}`);
  }

  addresses(userId: number): Observable<Address[]> {
    return this.http.get<Address[]>(`${this.base}/users/${userId}/addresses`);
  }

  addAddress(userId: number, body: Address): Observable<Address> {
    return this.http.post<Address>(`${this.base}/users/${userId}/addresses`, body);
  }

  // ---------- restaurants & menu ----------
  restaurants(): Observable<Restaurant[]> {
    return this.http.get<Restaurant[]>(`${this.base}/restaurants`);
  }

  restaurantsByCuisine(cuisine: string): Observable<Restaurant[]> {
    return this.http.get<Restaurant[]>(`${this.base}/restaurants/cuisine/${encodeURIComponent(cuisine)}`);
  }

  restaurant(id: number): Observable<Restaurant> {
    return this.http.get<Restaurant>(`${this.base}/restaurants/${id}`);
  }

  menu(restaurantId: number): Observable<RestaurantMenu> {
    return this.http.get<RestaurantMenu>(`${this.base}/restaurants/${restaurantId}/menu`);
  }

  managedRestaurants(): Observable<ManagedRestaurant[]> {
    return this.http.get<ManagedRestaurant[]>(`${this.base}/restaurants/manage`);
  }

  managedMenu(restaurantId: number): Observable<ManagedMenuItem[]> {
    return this.http.get<ManagedMenuItem[]>(`${this.base}/restaurants/${restaurantId}/menu/manage`);
  }

  createRestaurant(body: { name: string; address: string; cuisine: string; contact: string; hours: string; ownerId: number }): Observable<any> {
    return this.http.post(`${this.base}/restaurants`, body);
  }

  approveRestaurant(id: number): Observable<ManagedRestaurant> {
    return this.http.put<ManagedRestaurant>(`${this.base}/restaurants/${id}/approve`, {});
  }

  rejectRestaurant(id: number): Observable<ManagedRestaurant> {
    return this.http.put<ManagedRestaurant>(`${this.base}/restaurants/${id}/reject`, {});
  }

  createMenuItem(restaurantId: number, body: {
    name: string; description: string; price: number; availability: boolean; categoryId: number; adminId: number;
  }): Observable<any> {
    return this.http.post(`${this.base}/restaurants/${restaurantId}/menu`, body);
  }

  updateMenuItem(itemId: number, adminId: number, body: { description?: string; price?: number; availability?: boolean }): Observable<any> {
    return this.http.put(`${this.base}/restaurants/menu/${itemId}`, body, { params: new HttpParams().set('adminId', adminId) });
  }

  deleteMenuItem(itemId: number, adminId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/restaurants/menu/${itemId}`, { params: new HttpParams().set('adminId', adminId) });
  }

  // ---------- categories, cuisines, roles ----------
  categories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.base}/categories`);
  }

  createCategory(body: { name: string; description: string }): Observable<Category> {
    return this.http.post<Category>(`${this.base}/categories`, body);
  }

  updateCategory(id: number, adminId: number, body: { name: string; description: string }): Observable<Category> {
    return this.http.put<Category>(`${this.base}/categories/${id}`, body, { params: new HttpParams().set('adminId', adminId) });
  }

  deactivateCategory(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/categories/${id}`);
  }

  cuisines(): Observable<Cuisine[]> {
    return this.http.get<Cuisine[]>(`${this.base}/cuisine-types`);
  }

  createCuisine(body: { name: string; description: string; active: boolean }): Observable<Cuisine> {
    return this.http.post<Cuisine>(`${this.base}/cuisine-types`, body);
  }

  createRole(body: { name: string; description: string }): Observable<any> {
    return this.http.post(`${this.base}/roles`, body);
  }

  // ---------- cart ----------
  cart(): Observable<Cart> {
    return this.http.get<Cart>(`${this.base}/cart`);
  }

  createCart(userId: number, restaurantId: number | null): Observable<Cart> {
    return this.http.post<Cart>(`${this.base}/cart`, { userId, restaurantId });
  }

  addCartItem(cartId: number, menuItemId: number, quantity: number): Observable<any> {
    return this.http.post(`${this.base}/cart/items`, { cartId, menuItemId, quantity });
  }

  updateCartItem(cartItemId: number, userId: number, quantity: number): Observable<Cart> {
    return this.http.put<Cart>(`${this.base}/cart/items/${cartItemId}`, { quantity }, { params: new HttpParams().set('userId', userId) });
  }

  removeCartItem(cartItemId: number, userId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/cart/items/${cartItemId}`, { params: new HttpParams().set('userId', userId) });
  }

  clearCart(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/cart`, { params: new HttpParams().set('userId', userId) });
  }

  // ---------- orders ----------
  validateOrder(body: { userId: number; restaurantId: number; items: { menuItemId: number; quantity: number; unitPrice: number }[] }): Observable<ValidationResult> {
    return this.http.post<ValidationResult>(`${this.base}/orders/validate`, body);
  }

  placeOrder(body: { userId: number; addressId: number; paymentMethod: string }): Observable<OrderDetail> {
    return this.http.post<OrderDetail>(`${this.base}/orders`, body);
  }

  userOrders(userId: number, page: number, size: number, status?: string): Observable<OrderHistory> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<OrderHistory>(`${this.base}/orders/user/${userId}`, { params });
  }

  restaurantOrders(restaurantId: number, page: number, size: number, status?: string): Observable<OrderHistory> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<OrderHistory>(`${this.base}/orders/restaurant/${restaurantId}`, { params });
  }

  order(id: number): Observable<OrderDetail> {
    return this.http.get<OrderDetail>(`${this.base}/orders/${id}`);
  }

  updateOrderStatus(id: number, status: string): Observable<any> {
    return this.http.put(`${this.base}/orders/${id}/status`, { status });
  }

  // ---------- feedback ----------
  feedbackForRestaurant(restaurantId: number, page: number, size: number): Observable<FeedbackPage> {
    return this.http.get<FeedbackPage>(`${this.base}/feedback/restaurant/${restaurantId}`, {
      params: new HttpParams().set('page', page).set('size', size),
    });
  }

  createFeedback(body: { userId: number; restaurantId: number; orderId: number; rating: number; comment: string }): Observable<Feedback> {
    return this.http.post<Feedback>(`${this.base}/feedback`, body);
  }

  updateFeedback(id: number, body: { rating: number; comment: string }): Observable<Feedback> {
    return this.http.put<Feedback>(`${this.base}/feedback/${id}`, body);
  }

  deleteFeedback(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/feedback/${id}`);
  }

  // ---------- prices ----------
  deliveryRules(): Observable<DeliveryRule> {
    return this.http.get<DeliveryRule>(`${this.base}/prices/delivery-rules`);
  }

  updateDeliveryRules(adminUserId: number, body: { baseFee: number; perKmRate: number; maxDeliveryRadius: number; freeDeliveryThreshold: number }): Observable<DeliveryRule> {
    return this.http.put<DeliveryRule>(`${this.base}/prices/delivery-rules`, body, { params: new HttpParams().set('adminUserId', adminUserId) });
  }

  deliveryFee(restaurantId: number, addressId: number): Observable<DeliveryFee> {
    return this.http.post<DeliveryFee>(`${this.base}/prices/delivery-fee`, { restaurantId, addressId });
  }

  orderTotal(userId: number): Observable<{ total: number }> {
    return this.http.post<{ total: number }>(`${this.base}/prices/order-total`, {}, { params: new HttpParams().set('userId', userId) });
  }
}
