# Doodle Mini - Meeting Scheduling Service

A meeting scheduling platform built with Spring Boot and Java 17.

## Overview

Users can create available time slots in their personal calendar, then convert those slots into meetings with a title, description, and participants. The service supports querying free or busy slots for any time range.

## Tech Stack

- Java 17 + Spring Boot 3.4.5
- PostgreSQL 16
- Flyway (database migrations)
- Docker + docker-compose
- SpringDoc OpenAPI (Swagger UI)
- Micrometer + Actuator (metrics)

## Running Locally

**Requirements:** Docker and Docker Compose installed.

Build the jar, then start all services:

```bash
./gradlew bootJar
docker-compose up --build
```

The API will be available at `http://localhost:8080`.

**Swagger UI:** `http://localhost:8080/swagger-ui.html`

To stop:

```bash
docker-compose down
```

## Running Tests

Requires Docker running locally (used by Testcontainers to spin up a real PostgreSQL instance):

```bash
./gradlew test
```

## API Usage

### Users

**Create a user** (also creates a personal calendar automatically):

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Alice", "email": "alice@example.com"}'
```

**Get a user:**

```bash
curl http://localhost:8080/api/users/1
```

### Slots

**Create a time slot:**

```bash
curl -X POST http://localhost:8080/api/slots \
  -H "Content-Type: application/json" \
  -d '{"calendarId": 1, "startTime": "2026-06-10T10:00:00", "endTime": "2026-06-10T11:00:00"}'
```

**Update a slot (change time or status):**

```bash
curl -X PUT http://localhost:8080/api/slots/1 \
  -H "Content-Type: application/json" \
  -d '{"startTime": "2026-06-10T10:00:00", "endTime": "2026-06-10T11:00:00", "status": "BUSY"}'
```

**Delete a slot:**

```bash
curl -X DELETE http://localhost:8080/api/slots/1
```

**Query free/busy slots for a time range:**

```bash
# All slots in range
curl "http://localhost:8080/api/slots/availability?calendarId=1&from=2026-06-10T00:00:00&to=2026-06-11T00:00:00"

# Only available
curl "http://localhost:8080/api/slots/availability?calendarId=1&from=2026-06-10T00:00:00&to=2026-06-11T00:00:00&status=AVAILABLE"

# Only busy
curl "http://localhost:8080/api/slots/availability?calendarId=1&from=2026-06-10T00:00:00&to=2026-06-11T00:00:00&status=BUSY"
```

### Meetings

**Create a meeting from an available slot:**

```bash
curl -X POST http://localhost:8080/api/meetings \
  -H "Content-Type: application/json" \
  -d '{
    "slotId": 1,
    "title": "Team sync",
    "description": "Weekly meeting",
    "participantUserIds": [2, 3]
  }'
```

**Get a meeting:**

```bash
curl http://localhost:8080/api/meetings/1
```

**Update meeting details:**

```bash
curl -X PUT http://localhost:8080/api/meetings/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Team sync", "description": "Updated", "participantUserIds": [2]}'
```

**Get all meetings for a calendar:**

```bash
curl http://localhost:8080/api/meetings/calendar/1
```

## Observability

```bash
# Health check
curl http://localhost:8080/actuator/health

# Prometheus metrics
curl http://localhost:8080/actuator/prometheus
```

## Design Decisions

**One calendar per user** - created automatically on registration. The calendar is the domain concept that owns slots.

**Slots are the core entity** - a slot has a time range and a status (`AVAILABLE` or `BUSY`). When booked as a meeting, the status changes to `BUSY` and the slot gets a reference to the meeting.

**Optimistic locking on slots** - `Slot` uses `@Version` to prevent double-booking under concurrent requests. If two requests try to book the same slot simultaneously, one will fail with a conflict error.

**Overlap detection** - creating or updating a slot checks for time conflicts with existing slots in the same calendar.

**Indexed for scale** - slots are indexed on `(calendar_id, start_time, end_time)` to support efficient availability queries across thousands of slots per user.
