# 🧪 NexusEnroll Microservices Testing Guide

This guide provides instructions on how to execute test suites for the **NexusEnroll Microservices Architecture**. There are two ways to test the application:

1. **Automated End-to-End API Test Suite (`run-api-tests.js`)** — Tests the live running microservices via Docker & API Gateway.
2. **Maven Unit & Integration Tests (`mvn test`)** — Tests individual Spring Boot microservices using JUnit 5 & Mockito.

---

## 🚀 Option 1: Automated End-to-End API Tests (`node run-api-tests.js`)

This option runs **40 automated API test cases** against the live system through the **API Gateway (`http://localhost:8080`)**.

### **Prerequisites**
All microservices, MySQL, and the frontend must be running in Docker. Always wipe the volume
first if you've pulled changes to any `V2__seed_*.sql` file (Flyway will otherwise refuse to
start against stale checksums) - see [DOCKER.md](DOCKER.md) for the full explanation:
```powershell
docker compose down -v
docker compose up --build -d
```

### **Run Command**
```powershell
node run-api-tests.js
```

### **What It Tests (40/40 Endpoints Passing):**
- 🔑 **Auth Service**: Student Registration, public registration correctly rejecting non-STUDENT roles, JWT Login (seeded demo accounts and freshly registered ones), Admin-only Staff Provisioning (`/api/auth/provision-staff`), Role Validation
- 📚 **Course Service**: Paginated Search, Course Creation, Section Queries, Degree Programs, Change Requests
- 👨‍🎓 **Student Service**: Profiles, Schedule Queries, Degree Progress
- 📝 **Enrollment Service**: Course Enrollment, Student Enrollments Query, Waitlist Management
- 👩‍🏫 **Faculty Service**: Faculty Profiles, Class Roster, Grade Drafts, Grade Submission & Approval Workflow
- 📜 **Academic Record Service**: Transcripts, Completed Courses, Credit Tracking
- 🔔 **Notification Service**: Notification Triggers, User Inbox, Unread Count
- 📊 **Reporting Service**: Enrollment Statistics, Course Popularity, Faculty Workload Reports
- 🛑 **Validation & Error Handling**: Invalid Password (401), Missing Auth Token (401), Non-Existent Resources (404), Invalid Grade Submission (400)

---

## 🛠️ Option 2: Maven Backend Unit & Integration Tests (`mvn test`)

This option runs all internal Java unit and controller integration tests using **JUnit 5**, **Mockito**, and **Spring MockMvc**.

### **Run All Microservice Unit Tests**
Run the following command from the project root:
```powershell
mvn test
```

### **Run Tests for a Specific Microservice**
To run test cases for an individual microservice, specify the project module:

```powershell
# Course Service
mvn test -pl course-service

# Auth Service
mvn test -pl auth-service

# Student Service
mvn test -pl student-service

# Enrollment Service
mvn test -pl enrollment-service

# Faculty Service
mvn test -pl faculty-service

# Academic Record Service
mvn test -pl academic-record-service

# Notification Service
mvn test -pl notification-service

# Reporting Service
mvn test -pl reporting-service

# API Gateway
mvn test -pl api-gateway
```

---

## 📋 Comparison Table

| Feature | Option 1: `node run-api-tests.js` | Option 2: `mvn test` |
| :--- | :--- | :--- |
| **Type** | End-to-End (E2E) Integration Testing | Unit & Controller Mock Testing |
| **Environment Required** | Docker Containers Running (`localhost:8080`) | Offline (No Docker required) |
| **Target Scope** | Real Network Calls via API Gateway | Mocked Beans & Controllers |
| **Total Test Count** | 40 API Endpoints | 80+ Java JUnit Test Cases |
| **Execution Time** | ~3 - 5 seconds | ~1 minute |

---

## ✅ Current Test Status
- **`node run-api-tests.js`**: `40 / 40 PASSED` 🎉
- **`mvn test`**: `11 / 11 MODULES BUILD SUCCESS` 🎉

---

## 🔑 Demo Login Credentials

All seeded demo accounts share the password **`Password123`**. See [DOCKER.md](DOCKER.md) for
the full list, the one-command startup flow, and troubleshooting.
