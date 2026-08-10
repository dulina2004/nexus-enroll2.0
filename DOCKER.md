# NexusEnroll 2.0 — Docker Startup Guide

All commands below are run from this directory (the repo root of `nexus-enroll2.0`):

```
d:\sandhanu\UCSC\project UCSC\nexusenroll microservices\nexus-enroll2.0
```

This assumes `nexus-enroll2.0-frontend` is checked out as a **sibling directory**
(`../nexus-enroll2.0-frontend`), since the frontend service in `docker-compose.yml`
builds from that relative path.

---

## One Command, Full Stack

Every service Dockerfile is multi-stage and builds its own JAR **inside** Docker via
Maven — there is no separate "build the jars first" step anymore. `docker compose build`
(or `up --build`) is the only build step you need, for backend and frontend alike.

```powershell
# Always start from a clean database - see "Why down -v" below.
docker compose down -v
docker compose up --build -d
```

First build downloads the Maven dependency cache and can take several minutes; a
BuildKit cache mount on `/root/.m2` makes every build after the first one much faster.
Rebuilds after code changes are just `docker compose up --build -d` again.

Once containers are healthy:

| What | URL |
|---|---|
| Frontend | http://localhost:8085 |
| API Gateway | http://localhost:8080 |
| MySQL (host access, e.g. a GUI client) | localhost:3307 (root / password) |

### Why `down -v`

`nexus-mysql` keeps its data in the named volume `mysql_data`. A plain `docker compose down`
does **not** remove that volume, so old rows and old Flyway checksums survive between runs.
If you change a `V2__seed_*.sql` file (as this fix did) and don't wipe the volume first,
Flyway will refuse to start because the checksum of the already-applied migration no longer
matches the file on disk. **Always `docker compose down -v` before `up` when seed data changes.**

---

## Demo Credentials

All seeded accounts share the same password: **`Password123`**

| Username | Role | Notes |
|---|---|---|
| `admin` | ADMIN | |
| `faculty1` | FACULTY | Sarah Connor — teaches CS-101 §01, CS-201, CS-401, PHYS-101 |
| `faculty2` | FACULTY | Albert Einstein — teaches CS-101 §02, MATH-101, MATH-201 |
| `student1` | STUDENT | John Doe — richest demo data: 2 completed + graded courses, 2 in-progress enrollments, degree progress, notifications |
| `student2` | STUDENT | James Bond |
| `student3` | STUDENT | Maria Garcia |
| `student4` | STUDENT | Kevin Chen |

Log in from the frontend at http://localhost:8085, or directly against the gateway:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"identifier":"student1","password":"Password123"}'
```

---

## Useful Commands

### Check container status (all should be `healthy`, none `restarting`)
```powershell
docker compose ps
```

### View logs
```powershell
docker compose logs -f                  # everything
docker compose logs auth-service -f     # one service
docker compose logs api-gateway -f
docker compose logs mysql -f
```

### Stop / restart without touching data
```powershell
docker compose stop
docker compose up -d
```

### Full wipe (containers + volumes)
```powershell
docker compose down -v
```

---

## Service Ports

The gateway on **8080** and the frontend on **8085** are the only ports you should need.
Every other service port is published too (for direct debugging), matching the
`server.port` each service runs on internally:

| Service | Port |
|---|---|
| Frontend (nginx) | **8085** |
| API Gateway (public) | **8080** |
| MySQL (debug access) | **3307** → container 3306 |
| auth-service | 8081 |
| course-service | 8082 |
| student-service | 8083 |
| enrollment-service | 8084 |
| faculty-service | 8085 (container-internal; not published, would collide with the frontend's 8085) |
| academic-record-service | 8086 |
| notification-service | 8087 |
| reporting-service | 8088 |

---

## Health Check

```bash
docker compose ps
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/auth/roles
```

Then, with a token from the login call above, spot-check that real seeded data comes back
(not empty arrays):

```bash
curl -H "Authorization: Bearer <token>" "http://localhost:8080/api/students?userId=4"
curl -H "Authorization: Bearer <token>" "http://localhost:8080/api/students/1/schedule"
curl -H "Authorization: Bearer <token>" "http://localhost:8080/api/courses"
curl -H "Authorization: Bearer <token>" "http://localhost:8080/api/records?studentId=1"
curl -H "Authorization: Bearer <token>" "http://localhost:8080/api/faculty/user/2"
curl -H "Authorization: Bearer <token>" "http://localhost:8080/api/notifications/user/4"
curl -H "Authorization: Bearer <token>" "http://localhost:8080/api/reports/enrollment-stats?semester=FALL&year=2025"
```

---

## Troubleshooting

### "Port 8080 refused" / can't reach the gateway
The gateway container isn't up yet, or one of its dependencies failed its health check
and the gateway's `depends_on: condition: service_healthy` is holding it back. Check:

```powershell
docker compose ps
docker compose logs api-gateway --tail 100
docker compose logs auth-service --tail 100
```

The most common cause is a stale volume from before this fix — a service that expects the
current `V2__seed_*.sql` files finds an old, different set of rows/checksums already in the
database and Flyway refuses to start. Fix: `docker compose down -v` (see above), then
`docker compose up --build -d` again.

### Login returns 401
- Confirm you're using one of the seeded usernames above with password `Password123` exactly
  (case-sensitive).
- If you changed `V2__seed_auth_data.sql` and didn't `down -v` first, the database may still
  contain the *old* password hash. Wipe the volume and restart.
- Check `docker compose logs auth-service` for the actual rejection reason — invalid
  credentials, inactive account, and account-locked all return 401 but log different messages
  server-side.

### A service is stuck `restarting`
```powershell
docker compose logs <service-name> --tail 200
```
Almost always either a Flyway checksum mismatch (wipe the volume) or the service starting
before MySQL is ready (shouldn't happen — `depends_on: condition: service_healthy` on `mysql`
is set for every backend service — but if you edited the compose file, check that's still there).

### Frontend loads but every page is empty or shows mock-looking data
The frontend build sets `VITE_USE_MOCK_FALLBACK=false`, so a dead backend now surfaces as a
visible error instead of fake data — if you're seeing plausible-looking data with the backend
down, the frontend image is stale. Rebuild: `docker compose up --build -d frontend`.

### CORS errors in the browser console
Should not happen in the Docker setup — the frontend's nginx proxies `/api/` and `/actuator/`
straight to `api-gateway:8080` same-origin, so the browser never makes a cross-origin request.
If you *are* seeing CORS errors, you're probably running the frontend outside Docker
(`npm run dev`) against a Dockerised gateway; make sure `http://localhost:5173` (Vite's default)
or `http://localhost:8085` is in the allowed origins list in `api-gateway`'s `CorsConfig.java`.
