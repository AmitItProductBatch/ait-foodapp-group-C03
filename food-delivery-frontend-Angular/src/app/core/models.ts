export type Role = 'USER' | 'PARTNER' | 'ADMIN';

export type OrderStatus =
  | 'PLACED' | 'CONFIRMED' | 'PREPARING' | 'READY' | 'OUT_FOR_DELIVERY' | 'DELIVERED' | 'CANCELLED';

export interface Address {
  id?: number;
  addressLabel?: string;
  streetAddress?: string;
  apartment?: string;
  landmark?: string;
  city?: string;
  postalCode?: string;
  deliveryInstructions?: string;
}

export interface SessionUser {
  id: number;
  name: string;
  email: string;
  phonenumber: string;
  role: Role;
  active: boolean;
}

export interface LoginResponse extends SessionUser {
  token: string;
  tokenType: string;
  expiresInSeconds: number;
  addresses: Address[];
}

export interface Restaurant {
  restaurantId: number;
  name: string;
  address: string;
  cuisine: string;
  contact?: string;
  hours?: string;
  rating?: number | null;
}

export interface ManagedRestaurant {
  restaurantId: number;
  name: string;
  address: string;
  cuisine: string;
  contact: string;
  hours: string;
  status: string;
  active: boolean;
  ownerId: number;
  rating?: number | null;
}

export interface MenuItemDetail {
  itemId: number;
  name: string;
  description: string;
  price: number;
}

export interface MenuCategory {
  category: string;
  items: MenuItemDetail[];
}

export interface RestaurantMenu {
  restaurantId: number;
  categories: MenuCategory[];
}

export interface ManagedMenuItem {
  itemId: number;
  restaurantId: number;
  name: string;
  description: string;
  price: number;
  availability: boolean;
  category: string;
}

export interface CartItem {
  cartItemId: number;
  itemId: number;
  itemName: string;
  unitPrice: number;
  quantity: number;
  subtotal: number;
}

export interface Cart {
  id: number | null;
  userId: number;
  restaurantId: number | null;
  totalAmount: number;
  items: CartItem[];
}

export interface Category {
  id: number;
  name: string;
  description: string;
}

export interface Cuisine {
  id: number;
  name: string;
  description: string;
  active: boolean;
}

export interface OrderSummary {
  orderId: number;
  userId: number;
  restaurantId: number;
  totalAmount: number;
  status: OrderStatus;
  paymentStatus: string;
  createdAt: string;
}

export interface OrderHistory {
  orders: OrderSummary[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface OrderItemDetail {
  id: number;
  menuItemId: number;
  itemNameSnapshot: string;
  unitPrice: number;
  quantity: number;
  subtotal: number;
}

export interface OrderStatusHistoryEntry {
  id: number;
  fromStatus: OrderStatus | null;
  toStatus: OrderStatus;
  changedAt: string;
  changedBy: number;
}

export interface OrderDetail {
  id: number;
  userId: number;
  restaurantId: number;
  deliveryAddressSnapshot: string;
  totalAmount: number;
  status: OrderStatus;
  paymentStatus: string;
  paymentMethod: string;
  createdAt: string;
  updatedAt: string;
  orderItems: OrderItemDetail[];
  statusHistory: OrderStatusHistoryEntry[];
}

export interface Feedback {
  id: number;
  userId: number;
  restaurantId: number;
  orderId: number;
  rating: number;
  comment: string;
  createdAt: string;
}

export interface FeedbackPage {
  feedbacks: Feedback[];
  currentPage: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
}

export interface DeliveryRule {
  id: number;
  baseFee: number;
  perKmRate: number;
  maxDeliveryRadius: number;
  freeDeliveryThreshold: number;
  active: boolean;
}

export interface DeliveryFee {
  restaurantId: number;
  addressId: number;
  distanceKm: number;
  baseFee: number;
  distanceFee: number;
  deliveryFee: number;
  freeDeliveryApplied: boolean;
}

export interface ValidationResult {
  valid: boolean;
  errors: string[];
}
