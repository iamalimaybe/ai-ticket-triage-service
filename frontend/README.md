# AI Ticket Triage Service Frontend

React + Vite frontend for the AI Ticket Triage Service.

This UI is intentionally lightweight. It is not a full SaaS dashboard. It exists to demonstrate the backend review workflow visually:

```text
Submit ticket
View structured analysis
Load review queue
Inspect saved analysis detail
Update review status
```

## Features

* Submit support tickets for analysis
* Display structured triage results
* Load saved analyses by review status
* Filter review queue by `NEEDS_REVIEW`, `REVIEWED`, or `NOT_REQUIRED`
* Change page size and move through queue pages
* Inspect analysis detail
* View raw model output when available
* Update review status, review reason, and reviewer name

## Tech Stack

* React
* TypeScript
* Vite
* CSS
* Backend API proxy through Vite dev server

## Local Development

Start the backend from the repository root:

```bash
docker compose up --build
```

Start the frontend:

```bash
cd frontend
npm install
npm run dev
```

Open:

```text
http://localhost:5173
```

The frontend proxies `/api` requests to:

```text
http://localhost:8080
```

This is configured in:

```text
vite.config.ts
```

## Build

```bash
npm run build
```

## Notes

The frontend is a thin review console over the Spring Boot backend. The main project value remains the backend workflow around validated, persisted, and reviewable AI output.
