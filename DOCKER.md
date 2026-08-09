# NexusEnroll — Docker Startup Guide

All commands must be run from this directory:

```
d:\migrate\new\backend\nexus-backend
```

---

## First Time / Full Clean Start

Use this when starting fresh or after deleting everything from Docker.

```powershell
# Step 1 — Build all Spring Boot JARs (skip tests for speed)
.\mvnw.cmd clean package -DskipTests --no-transfer-progress

# Step 2 — Build Docker images and start all containers
docker compose up --build -d
```

Wait ~30 seconds for MySQL to pass its health check, then all services will start automatically.

---

## Quick Restart (no code changes)

Use this when containers were stopped/removed but images still exist.

```powershell
docker compose up -d
```

---

## Full Nuke + Rebuild

Use this when you want to completely wipe everything and start from zero.

```powershell
# Step 1 — Stop and remove all containers, networks, and volumes
docker compose down -v

# Step 2 — Force remove any stale containers (safety net)
docker rm -f nexus-mysql nexus-auth-service nexus-student-service nexus-course-service nexus-enrollment-service nexus-faculty-service nexus-academic-record-service nexus-notification-service nexus-reporting-service nexus-api-gateway

# Step 3 — Remove old images to force a fresh build
docker rmi nexus-backend-auth-service nexus-backend-course-service nexus-backend-student-service nexus-backend-enrollment-service nexus-backend-faculty-service nexus-backend-academic-record-service nexus-backend-notification-service nexus-backend-reporting-service nexus-backend-api-gateway

# Step 4 — Rebuild JARs
.\mvnw.cmd clean package -DskipTests --no-transfer-progress

# Step 5 — Build images and start
docker compose up --build -d
```

---

## Useful Commands

### Check container status
```powershell
docker compose ps
```

### View logs for all services
```powershell
docker compose logs -f
```

### View logs for a specific service
```powershell
docker compose logs auth-service -f
docker compose logs api-gateway -f
docker compose logs mysql -f
docker compose logs course-service -f
```

### Stop all containers (keeps volumes)
```powershell
docker compose stop
```

### Stop and remove containers (keeps volumes)
```powershell
docker compose down
```

### Stop and remove containers + volumes (wipes database)
```powershell
docker compose down -v
```

---

## Service Ports

All traffic goes through the **API Gateway on port 8080**.
Individual service ports are internal only (not exposed to host).

| Service                  | Internal Port |
|--------------------------|--------------|
| API Gateway (public)     | **8080**     |
| auth-service             | 8081         |
| course-service           | 8082         |
| student-service          | 8083         |
| enrollment-service       | 8084         |
| faculty-service          | 8085         |
| academic-record-service  | 8086         |
| notification-service     | 8087         |
| reporting-service        | 8088         |

---

## Test the Stack

Once all containers are running, test with the included HTTP file:

```
nexusenroll-api-tests.http
```

Recommended test order:

1. `POST http://localhost:8080/api/auth/register` — create a user
2. `POST http://localhost:8080/api/auth/login` — get JWT token
3. Set the token in the `@token` variable at the top of the file
4. `GET  http://localhost:8080/api/courses` — verify routing works

---

## Health Check

```powershell
# Gateway actuator health
curl http://localhost:8080/actuator/health

# View gateway routes
curl http://localhost:8080/actuator/routes
```
