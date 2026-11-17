import { HttpErrorResponse } from '@angular/common/http';
import { OrderStatus } from './models';

/** Turns any backend error into one readable sentence. */
export function errMsg(e: unknown): string {
  if (e instanceof HttpErrorResponse) {
    if (e.status === 0) {
      return 'Cannot reach the server. Is the backend running?';
    }
    const body: any = e.error;
    if (typeof body === 'string' && body.trim()) {
      return body;
    }
    if (body && typeof body === 'object') {
      if (Array.isArray(body.errors) && body.errors.length) {
        return body.errors.join(', ');
      }
      if (body.message) {
        return body.message;
      }
    }
    return e.statusText || 'Request failed (' + e.status + ')';
  }
  return (e as any)?.message || 'Something went wrong';
}

/** Next steps a restaurant (PARTNER) or admin may take for an order. */
export const NEXT_STATUS: Record<OrderStatus, OrderStatus[]> = {
  PLACED: ['CONFIRMED', 'CANCELLED'],
  CONFIRMED: ['PREPARING', 'CANCELLED'],
  PREPARING: ['READY', 'CANCELLED'],
  READY: ['OUT_FOR_DELIVERY'],
  OUT_FOR_DELIVERY: ['DELIVERED'],
  DELIVERED: [],
  CANCELLED: [],
};

export function statusLabel(s: string | null | undefined): string {
  return (s || '').replace(/_/g, ' ').toLowerCase().replace(/^\w/, (c) => c.toUpperCase());
}

export function statusClass(s: string | null | undefined): string {
  switch (s) {
    case 'DELIVERED':
    case 'APPROVED':
    case 'PAID':
      return 'ok';
    case 'CANCELLED':
    case 'REJECTED':
    case 'FAILED':
      return 'bad';
    case 'PENDING':
    case 'PLACED':
      return 'warn';
    default:
      return 'info';
  }
}
