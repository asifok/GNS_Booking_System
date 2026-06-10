# GNS Booking — Frontend

> **NAACH '26** — Event ticket booking SPA for *Gurudev Natraj Studio* presents **NAACH '26** (Theme: *Mumbai Meri Jaan* · 25 April · SMT Bhuriben Golwala Auditorium, Ghatkopar West).

Built with **React 18 + TypeScript + Vite**, backed by a **Spring Boot** REST API.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Features](#features)
- [API Endpoints](#api-endpoints)
- [Environment Variables](#environment-variables)
- [Getting Started](#getting-started)
- [Production Build](#production-build)
- [Deployment](#deployment)
- [Seat Pricing](#seat-pricing)
- [UPI Payment](#upi-payment)
- [Admin Access](#admin-access)

---

## Tech Stack

| Layer | Technology |
|---|---|
| UI Framework | React 18 |
| Language | TypeScript 5.6 |
| Build Tool | Vite 5 |
| Routing | React Router DOM v6 |
| Styling | Plain CSS (BEM conventions) |
| Backend | Spring Boot (separate repo) |

---

## Project Structure

```
src/
├── apiBase.ts                  # Builds API URLs from VITE_API_URL
├── App.tsx                     # Root router + lazy-loaded admin page
├── main.tsx                    # ReactDOM entry point
├── index.css                   # Global styles
├── data/
│   ├── eventInfo.ts            # Event metadata (title, date, venue)
│   ├── paymentConfig.ts        # UPI VPA + QR image config
│   └── staticAdminBookings.ts  # Static demo data for admin
├── components/
│   ├── AppShell.tsx            # Layout wrapper (header + outlet)
│   ├── BookFlowContext.tsx     # Context to trigger booking flow from anywhere
│   └── Redirect.tsx           # Simple redirect helper
├── views/
│   ├── Home.tsx                # Landing / hero page
│   ├── TicketsPage.tsx         # User's booked tickets
│   └── EventTicket.tsx         # Printable ticket component
└── modules/
    ├── user/                   # OTP login, user context, user profile
    ├── seat/                   # Seat map, seat selection, confirm dialog
    ├── booking/                # Booking API, payment modal, UPI URI builder
    └── admin/                  # Admin tickets page, accordion, profile
```

---

## Features

### User Flow
- **OTP Login** — email-based OTP sent via `/user/send-otp`; verified at `/user/verify-otp`
- **Seat Map** — live seat grid fetched from backend; rows A–S, sections LEFT / CENTER / RIGHT
- **Seat Selection** — multi-seat selection with real-time availability; blocked/booked seats are dimmed
- **Booking** — POST to `/booking/save` with email + selected seat numbers
- **UPI Payment** — auto-generated `upi://pay?` deep-link + static Paytm QR code displayed in modal
- **My Tickets** — fetches user's bookings, shows seat numbers, status, and price

### Admin Flow
- **Admin detection** — role parsed from OTP verify response (`parseIsAdmin.ts`)
- **All Bookings** — paginated accordion list with booking ID, email, seats, total, status
- **Approve / Update** — POST `/booking/confirm` to set payment method and approval
- **Cancel Seats** — POST `/booking/cancel-seats` to free specific seats from a booking
- **Notify Paid** — POST `/booking/notify-paid` to trigger confirmation email

---

## API Endpoints

All endpoints are relative to `VITE_API_URL`. The Spring Boot backend must enable CORS for the SPA origin.

| Method | Path | Description |
|---|---|---|
| `POST` | `/user/send-otp` | Send OTP to user email |
| `POST` | `/user/verify-otp` | Verify OTP; returns user/admin info |
| `GET` | `/user/exists?email=` | Check if user is registered (optional) |
| `GET` | `/seat/all/get?email=` | Fetch all seats with status |
| `POST` | `/booking/save` | Create a new booking |
| `GET` | `/booking/tickets/all/get?email=` | Fetch user's own tickets |
| `GET` | `/booking/tickets/admin/all/get?email=` | Fetch all bookings (admin) |
| `POST` | `/booking/confirm` | Admin: approve booking + set payment method |
| `POST` | `/booking/cancel-seats` | Admin: cancel specific seats |
| `POST` | `/booking/notify-paid` | Trigger paid notification email |

---

## Environment Variables

Copy `.env.example` to `.env` (dev) or `.env.local` and fill in values.
Only `VITE_*` variables are exposed to the browser.

| Variable | Required | Default | Description |
|---|---|---|---|
| `VITE_API_URL` | **Yes** | `http://localhost:9090` | Spring Boot base URL (no trailing slash) |
| `VITE_UPI_ID` | No | `7738612015@ptaxis` | UPI VPA for payment deep-link |
| `VITE_UPI_PAYEE_NAME` | No | `Charmi Shailesh Shah` | UPI payee display name |
| `VITE_UPI_QR_IMAGE` | No | `/upi-paytm-qr.png` | Path to QR image under `/public` |
| `VITE_ADMIN_BOOKINGS_STATIC` | No | — | Set `true` to use static demo data in admin page |

---

## Getting Started

### Prerequisites

- Node.js 18+
- npm 9+
- Spring Boot backend running (or set `VITE_ADMIN_BOOKINGS_STATIC=true` to demo without it)

### Install & Run

```bash
# 1. Clone / unzip the project
cd GNS-Booking-Project-FE

# 2. Install dependencies
npm install

# 3. Configure environment
cp .env.example .env
# Edit .env — set VITE_API_URL to your backend URL

# 4. Start dev server
npm run dev
# → http://localhost:3000
```

---

## Production Build

```bash
# Set env vars first (CI or shell), then:
npm run build
```

Output goes to `dist/` — plain static files (HTML + JS + CSS + images). Host anywhere:

- **Nginx** — serve `dist/` as document root with `try_files $uri /index.html` for SPA routing
- **Netlify / Vercel** — set env vars in the dashboard, point to `dist/` as publish directory
- **AWS S3 + CloudFront** — upload `dist/`, configure error page to `index.html`

### Preview build locally

```bash
npm run preview
# → http://localhost:4173
```

---

## Deployment

See [`DEPLOY.md`](./DEPLOY.md) for a full walkthrough including CORS setup on the backend.

**Key CORS requirement:** The Spring Boot app must allow the SPA's origin with `OPTIONS` preflight support for `POST` requests with `Content-Type: application/json`.

---

## Seat Pricing

Seats are color-coded by price tier:

| Price | CSS class | Typical section |
|---|---|---|
| ₹200 | `seat-map__seat--p200` | Back rows |
| ₹250 | `seat-map__seat--p250` | Mid rows |
| ₹300 | `seat-map__seat--p300` | Front/side |
| ₹300 | `seat-map__seat--p300c` | Center premium |
| ₹400 | `seat-map__seat--p400` | Front center |

Seats with `status=BOOKED`, `status=LOCKED`, `section=BLOCKED`, or `price≤0` are shown as muted/unselectable.

---

## UPI Payment

After seat confirmation, a payment modal shows:

1. A `upi://pay?pa=...&pn=...&am=...&tn=...` deep-link button (opens any UPI app)
2. A static QR code image (`/public/upi-paytm-qr.png` by default)

Once the user completes payment externally, they click **"I've paid"** which calls `POST /booking/notify-paid`.

Override UPI config via `VITE_UPI_*` environment variables at build time.

---

## Admin Access

Admin role is determined by the `/user/verify-otp` response (`parseIsAdmin.ts`). Admins see additional routes:

- `/admin/tickets` — full booking management (approve, cancel seats, set payment method)
- `/admin/profile` — admin profile page

The admin tickets page is **lazy-loaded** to keep the main bundle lean.

---

## Scripts

| Script | Command | Description |
|---|---|---|
| Dev server | `npm run dev` | Starts Vite dev server on port 3000 |
| Type-check + build | `npm run build` | Runs `tsc --noEmit` then Vite production build |
| Preview prod build | `npm run preview` | Serves the `dist/` folder locally |

---

## License

Private project — Gurudev Natraj Studio. Not for public distribution.
