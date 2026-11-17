# FoodDelivery – Angular frontend

Angular 18 (standalone components, signals). It talks to the Spring Boot backend through relative `/api/...` URLs,
so there is no CORS setup and no backend URL baked into the build.

## Run locally

1. Start the backend (profile `local` listens on **8081**). In the `local` profile an admin is created for you:
   `admin@fooddelivery.local` / `Admin@12345` (change it in `application-local.properties`).
2. In this folder:

       npm install
       npm start          # http://localhost:4200, /api is proxied to http://localhost:8081 (see proxy.conf.json)

   If your backend runs on another port, edit `proxy.conf.json`.

## Run with Docker

`docker compose up --build` from the project root. nginx serves the app on port 4003 and forwards `/api/` to the
backend container. Put `ADMIN_EMAIL`, `ADMIN_PASSWORD` and `JWT_SECRET` in `.env` for a real deployment.

## What each role can do

| Role | Screens |
|---|---|
| Anyone | Browse restaurants, filter by cuisine, view menu and reviews |
| USER (customer) | Cart, checkout (addresses, payment, delivery-fee estimate), order history and tracking, reviews, profile |
| PARTNER (restaurant owner) | Add restaurants (need admin approval), manage menu items, see and progress incoming orders |
| ADMIN | Approve / reject / suspend restaurants, categories, cuisine types, delivery fee rules, roles |

Typical first run: register a partner → add a restaurant → log in as admin and approve it → log back in as the partner
and add menu items (an admin must have created at least one category) → register a customer and order.
