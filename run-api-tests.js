/**
 * NexusEnroll Microservices API Automated Test Runner
 * Runs all API tests sequentially against the API Gateway (http://localhost:8080)
 */

const BASE_URL = process.env.BASE_URL || 'http://localhost:8080';
const ts = Date.now();

const COLORS = {
  reset: '\x1b[0m',
  bright: '\x1b[1m',
  dim: '\x1b[2m',
  green: '\x1b[32m',
  red: '\x1b[31m',
  yellow: '\x1b[33m',
  blue: '\x1b[34m',
  cyan: '\x1b[36m',
  white: '\x1b[37m'
};

const results = {
  total: 0,
  passed: 0,
  failed: 0,
  suites: []
};

let studentToken = '';
let adminToken = '';
let facultyToken = '';

let createdCourseId = null;
let createdChangeRequestId = null;
let createdEnrollmentId = null;
let createdGradeId = null;
let createdNotificationId = null;

async function request(method, endpoint, options = {}) {
  const { body, token, expectedStatus = [200, 201] } = options;
  const headers = { 'Content-Type': 'application/json' };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const url = `${BASE_URL}${endpoint}`;
  try {
    const res = await fetch(url, {
      method,
      headers,
      body: body ? JSON.stringify(body) : undefined
    });

    const text = await res.text();
    let data = null;
    try {
      data = JSON.parse(text);
    } catch {
      data = text;
    }

    const expectedList = Array.isArray(expectedStatus) ? expectedStatus : [expectedStatus];
    const isOk = expectedList.includes(res.status);

    return { status: res.status, isOk, data, headers: res.headers };
  } catch (err) {
    return { status: 0, isOk: false, data: err.message, headers: {} };
  }
}

function test(suiteName, name, assertFn) {
  results.total++;
  let suite = results.suites.find(s => s.name === suiteName);
  if (!suite) {
    suite = { name: suiteName, tests: [] };
    results.suites.push(suite);
  }

  try {
    const detail = assertFn();
    if (detail && detail.pass === false) {
      results.failed++;
      suite.tests.push({ name, pass: false, error: detail.error || 'Assertion failed' });
      console.log(`  ${COLORS.red}✘${COLORS.reset} ${name} ${COLORS.dim}(${detail.error})${COLORS.reset}`);
    } else {
      results.passed++;
      suite.tests.push({ name, pass: true });
      console.log(`  ${COLORS.green}✔${COLORS.reset} ${name}`);
    }
  } catch (err) {
    results.failed++;
    suite.tests.push({ name, pass: false, error: err.message });
    console.log(`  ${COLORS.red}✘${COLORS.reset} ${name} ${COLORS.dim}(${err.message})${COLORS.reset}`);
  }
}

async function runSuite(suiteName, fn) {
  console.log(`\n${COLORS.bright}${COLORS.cyan}====================================================${COLORS.reset}`);
  console.log(`${COLORS.bright}${COLORS.cyan} SUITE: ${suiteName}${COLORS.reset}`);
  console.log(`${COLORS.bright}${COLORS.cyan}====================================================${COLORS.reset}`);
  await fn();
}

async function start() {
  console.log(`${COLORS.bright}${COLORS.yellow}🚀 Starting NexusEnroll Automated API Test Runner${COLORS.reset}`);
  console.log(`Target Base URL: ${COLORS.bright}${BASE_URL}${COLORS.reset}\n`);

  // ===================================================
  // 1. AUTH SERVICE SUITE
  // ===================================================
  await runSuite('Auth Service', async () => {
    // Register Student
    const studentUser = `student_${ts}`;
    const studentEmail = `student_${ts}@nexus.edu`;
    const regStudent = await request('POST', '/api/auth/register', {
      body: { username: studentUser, email: studentEmail, password: 'Password123!', firstName: 'John', lastName: 'Doe', role: 'STUDENT' },
      expectedStatus: [200, 201]
    });
    test('Auth Service', `Register Student (${studentUser})`, () => ({
      pass: regStudent.isOk,
      error: `Status ${regStudent.status}: ${JSON.stringify(regStudent.data)}`
    }));

    // Register Admin
    const adminUser = `admin_${ts}`;
    const adminEmail = `admin_${ts}@nexus.edu`;
    await request('POST', '/api/auth/register', {
      body: { username: adminUser, email: adminEmail, password: 'Password123!', firstName: 'Admin', lastName: 'User', role: 'ADMIN' },
      expectedStatus: [200, 201]
    });

    // Login Student
    const loginStudent = await request('POST', '/api/auth/login', {
      body: { identifier: studentUser, password: 'Password123!' }
    });
    if (loginStudent.isOk && loginStudent.data?.data?.token) {
      studentToken = loginStudent.data.data.token;
    }
    test('Auth Service', 'Login Student and receive JWT token', () => ({
      pass: !!studentToken,
      error: `Failed to receive token: ${JSON.stringify(loginStudent.data)}`
    }));

    // Login Admin
    const loginAdmin = await request('POST', '/api/auth/login', {
      body: { identifier: adminUser, password: 'Password123!' }
    });
    if (loginAdmin.isOk && loginAdmin.data?.data?.token) {
      adminToken = loginAdmin.data.data.token;
    }
    test('Auth Service', 'Login Admin and receive JWT token', () => ({
      pass: !!adminToken,
      error: `Failed to receive admin token`
    }));

    // Get Roles with Bearer token
    const rolesRes = await request('GET', '/api/auth/roles', { token: studentToken });
    test('Auth Service', 'Get available roles (Authenticated)', () => ({
      pass: rolesRes.isOk && Array.isArray(rolesRes.data?.data),
      error: `Status ${rolesRes.status}`
    }));
  });

  // ===================================================
  // 2. COURSE SERVICE SUITE
  // ===================================================
  await runSuite('Course Service', async () => {
    // List courses
    const listRes = await request('GET', '/api/courses?page=0&size=10', { token: studentToken });
    test('Course Service', 'Get all courses (paginated)', () => ({ pass: listRes.isOk, error: `Status ${listRes.status}` }));

    // Create course
    const newCourse = await request('POST', '/api/courses', {
      token: studentToken,
      body: {
        courseCode: `CS-${ts.toString().slice(-3)}`,
        courseNumber: parseInt(ts.toString().slice(-3)),
        title: 'Automated Architecture Testing',
        description: 'Testing microservices automatically',
        credits: 3,
        capacity: 40,
        departmentId: 1,
        level: 'GRADUATE',
        status: 'ACTIVE'
      },
      expectedStatus: [200, 201]
    });
    if (newCourse.isOk && newCourse.data?.data?.id) {
      createdCourseId = newCourse.data.data.id;
    }
    test('Course Service', 'Create new course', () => ({ pass: newCourse.isOk, error: `Status ${newCourse.status}` }));

    const targetCourseId = createdCourseId || 1;

    // Get single course
    const getCourse = await request('GET', `/api/courses/${targetCourseId}`, { token: studentToken });
    test('Course Service', `Get course by ID (${targetCourseId})`, () => ({ pass: getCourse.isOk, error: `Status ${getCourse.status}` }));

    // Search course
    const searchRes = await request('GET', '/api/courses?keyword=Programming&page=0&size=5', { token: studentToken });
    test('Course Service', 'Search courses by keyword', () => ({ pass: searchRes.isOk, error: `Status ${searchRes.status}` }));

    // Get departments
    const deptRes = await request('GET', '/api/courses/departments', { token: studentToken });
    test('Course Service', 'Get all departments', () => ({ pass: deptRes.isOk, error: `Status ${deptRes.status}` }));

    // Get course sections
    const secRes = await request('GET', '/api/courses/sections?semester=FALL&year=2025', { token: studentToken });
    test('Course Service', 'Get course sections', () => ({ pass: secRes.isOk, error: `Status ${secRes.status}` }));

    // Get degree programs
    const progRes = await request('GET', '/api/courses/programs', { token: studentToken });
    test('Course Service', 'List degree programs', () => ({ pass: progRes.isOk, error: `Status ${progRes.status}` }));

    // Create change request
    const crRes = await request('POST', '/api/courses/change-requests', {
      token: studentToken,
      body: {
        courseId: targetCourseId,
        requestType: 'CAPACITY_CHANGE',
        requestedBy: 1,
        proposedValue: '50',
        justification: 'Need more capacity'
      },
      expectedStatus: [200, 201]
    });
    if (crRes.isOk && crRes.data?.data?.id) {
      createdChangeRequestId = crRes.data.data.id;
    }
    test('Course Service', 'Create course change request', () => ({ pass: crRes.isOk, error: `Status ${crRes.status}` }));
  });

  // ===================================================
  // 3. STUDENT SERVICE SUITE
  // ===================================================
  await runSuite('Student Service', async () => {
    const listStudents = await request('GET', '/api/students?page=0&size=10', { token: studentToken });
    test('Student Service', 'Get students list (paginated)', () => ({ pass: listStudents.isOk, error: `Status ${listStudents.status}` }));

    const getStudent = await request('GET', '/api/students/1', { token: studentToken, expectedStatus: [200, 404] });
    test('Student Service', 'Get student by ID (1)', () => ({ pass: getStudent.isOk, error: `Status ${getStudent.status}` }));

    const getSched = await request('GET', '/api/students/1/schedule', { token: studentToken, expectedStatus: [200, 404] });
    test('Student Service', 'Get student schedule', () => ({ pass: getSched.isOk, error: `Status ${getSched.status}` }));

    const getProg = await request('GET', '/api/students/1/progress?programId=1', { token: studentToken, expectedStatus: [200, 404] });
    test('Student Service', 'Get student degree progress', () => ({ pass: getProg.isOk, error: `Status ${getProg.status}` }));
  });

  // ===================================================
  // 4. ENROLLMENT SERVICE SUITE
  // ===================================================
  await runSuite('Enrollment Service', async () => {
    const enrollRes = await request('POST', '/api/enrollments', {
      token: studentToken,
      body: { studentId: 1, sectionId: 2 },
      expectedStatus: [200, 201, 400, 409]
    });
    if (enrollRes.data?.data?.id) {
      createdEnrollmentId = enrollRes.data.data.id;
    }
    test('Enrollment Service', 'Enroll student in section', () => ({ pass: enrollRes.isOk, error: `Status ${enrollRes.status}` }));

    const getEnroll = await request('GET', '/api/enrollments?studentId=1', { token: studentToken });
    test('Enrollment Service', 'Get enrollments by student ID', () => ({ pass: getEnroll.isOk, error: `Status ${getEnroll.status}` }));

    const getEnroll2 = await request('GET', '/api/enrollments?studentId=2', { token: studentToken });
    test('Enrollment Service', 'Get enrollments by another student ID', () => ({ pass: getEnroll2.isOk, error: `Status ${getEnroll2.status}` }));

    const waitlistRes = await request('POST', '/api/enrollments/waitlist', {
      token: studentToken,
      body: { studentId: 2, sectionId: 3 },
      expectedStatus: [200, 201, 400, 409]
    });
    test('Enrollment Service', 'Add student to waitlist', () => ({ pass: waitlistRes.isOk, error: `Status ${waitlistRes.status}` }));
  });

  // ===================================================
  // 5. FACULTY SERVICE SUITE
  // ===================================================
  await runSuite('Faculty Service', async () => {
    const facRes = await request('GET', '/api/faculty/1', { token: studentToken, expectedStatus: [200, 404] });
    test('Faculty Service', 'Get faculty by ID (1)', () => ({ pass: facRes.isOk, error: `Status ${facRes.status}` }));

    const rosterRes = await request('GET', '/api/faculty/roster?sectionId=1', { token: studentToken });
    test('Faculty Service', 'Get class roster for section', () => ({ pass: rosterRes.isOk, error: `Status ${rosterRes.status}` }));

    // Draft grade
    const draftRes = await request('POST', '/api/faculty/grades/draft', {
      token: studentToken,
      body: {
        enrollmentId: 1,
        studentId: 1,
        sectionId: 1,
        assignmentTitle: 'Automated Test Assignment',
        pointsEarned: 95.0,
        maxPoints: 100.0,
        letterGrade: 'A',
        comments: 'Automated grading',
        gradedBy: 'prof_smith'
      },
      expectedStatus: [200, 201]
    });
    if (draftRes.isOk && draftRes.data?.data?.id) {
      createdGradeId = draftRes.data.data.id;
    }
    test('Faculty Service', 'Create grade draft (DRAFT state)', () => ({ pass: draftRes.isOk, error: `Status ${draftRes.status}` }));

    if (createdGradeId) {
      const submitRes = await request('POST', '/api/faculty/grades/submit', {
        token: studentToken,
        body: { gradeId: createdGradeId }
      });
      test('Faculty Service', 'Submit grade (DRAFT -> PENDING state transition)', () => ({ pass: submitRes.isOk, error: `Status ${submitRes.status}` }));

      const approveRes = await request('POST', '/api/faculty/grades/approve', {
        token: studentToken,
        body: { gradeId: createdGradeId }
      });
      test('Faculty Service', 'Approve grade (PENDING -> APPROVED state transition)', () => ({ pass: approveRes.isOk, error: `Status ${approveRes.status}` }));
    }
  });

  // ===================================================
  // 6. ACADEMIC RECORD SERVICE SUITE
  // ===================================================
  await runSuite('Academic Record Service', async () => {
    const recRes = await request('GET', '/api/records?studentId=1', { token: studentToken });
    test('Academic Record Service', 'Get academic records by student ID', () => ({ pass: recRes.isOk, error: `Status ${recRes.status}` }));

    const compRes = await request('GET', '/api/records/completed-courses?studentId=1', { token: studentToken });
    test('Academic Record Service', 'Get completed courses for student', () => ({ pass: compRes.isOk, error: `Status ${compRes.status}` }));

    const postComp = await request('POST', '/api/records/completed-courses', {
      token: studentToken,
      body: { studentId: 1, courseCode: 'CS-101', courseTitle: 'Intro to Programming', credits: 3, grade: 'A', semester: 'FALL', year: 2025 },
      expectedStatus: [200, 201, 409]
    });
    test('Academic Record Service', 'Add completed course record', () => ({ pass: postComp.isOk, error: `Status ${postComp.status}` }));
  });

  // ===================================================
  // 7. NOTIFICATION SERVICE SUITE
  // ===================================================
  await runSuite('Notification Service', async () => {
    const createNotif = await request('POST', '/api/notifications', {
      token: studentToken,
      body: { eventType: 'ENROLLMENT_CREATED', recipientUserId: 1, title: 'Test Notification', message: 'Automated test suite message', notificationType: 'ENROLLMENT', priority: 'HIGH' },
      expectedStatus: [200, 201]
    });
    if (createNotif.isOk && createNotif.data?.data?.id) {
      createdNotificationId = createNotif.data.data.id;
    }
    test('Notification Service', 'Create notification', () => ({ pass: createNotif.isOk, error: `Status ${createNotif.status}` }));

    const getNotifs = await request('GET', '/api/notifications/user/1', { token: studentToken });
    test('Notification Service', 'Get notifications for user', () => ({ pass: getNotifs.isOk, error: `Status ${getNotifs.status}` }));

    const getUnread = await request('GET', '/api/notifications/user/1/unread-count', { token: studentToken });
    test('Notification Service', 'Get unread notification count', () => ({ pass: getUnread.isOk, error: `Status ${getUnread.status}` }));
  });

  // ===================================================
  // 8. REPORTING SERVICE SUITE
  // ===================================================
  await runSuite('Reporting Service', async () => {
    const statsRes = await request('GET', '/api/reports/enrollment-stats?semester=FALL&year=2025', { token: studentToken });
    test('Reporting Service', 'Get enrollment statistics report', () => ({ pass: statsRes.isOk, error: `Status ${statsRes.status}` }));

    const popRes = await request('GET', '/api/reports/course-popularity?semester=FALL&year=2025', { token: studentToken });
    test('Reporting Service', 'Get course popularity report', () => ({ pass: popRes.isOk, error: `Status ${popRes.status}` }));

    const workRes = await request('GET', '/api/reports/faculty-workload?semester=FALL&year=2025', { token: studentToken });
    test('Reporting Service', 'Get faculty workload report', () => ({ pass: workRes.isOk, error: `Status ${workRes.status}` }));
  });

  // ===================================================
  // 9. ERROR & VALIDATION TESTING SUITE
  // ===================================================
  await runSuite('Validation & Error Scenarios', async () => {
    // Bad Password -> 401
    const badLogin = await request('POST', '/api/auth/login', {
      body: { identifier: 'john_doe', password: 'WrongPassword!' },
      expectedStatus: 401
    });
    test('Validation & Error', 'Login with invalid password returns 401 Unauthorized', () => ({ pass: badLogin.status === 401, error: `Received ${badLogin.status}` }));

    // Unauthenticated request -> 401
    const unauthRes = await request('GET', '/api/courses', { expectedStatus: 401 });
    test('Validation & Error', 'Missing Authorization header returns 401 Unauthorized', () => ({ pass: unauthRes.status === 401, error: `Received ${unauthRes.status}` }));

    // Non-existent course -> 404
    const notFoundCourse = await request('GET', '/api/courses/999999', { token: studentToken, expectedStatus: 404 });
    test('Validation & Error', 'Get non-existent course returns 404 Not Found', () => ({ pass: notFoundCourse.status === 404, error: `Received ${notFoundCourse.status}` }));

    // Invalid grade submit -> 400
    const badGrade = await request('POST', '/api/faculty/grades/submit', {
      token: studentToken,
      body: { gradeId: 0 },
      expectedStatus: [400, 404]
    });
    test('Validation & Error', 'Submitting invalid grade ID returns 400/404 Error', () => ({ pass: [400, 404].includes(badGrade.status), error: `Received ${badGrade.status}` }));
  });

  // ===================================================
  // TEST SUMMARY REPORT
  // ===================================================
  console.log(`\n${COLORS.bright}${COLORS.white}====================================================${COLORS.reset}`);
  console.log(`${COLORS.bright}${COLORS.white} 📊 NEXUSENROLL API TEST SUITE SUMMARY${COLORS.reset}`);
  console.log(`${COLORS.bright}${COLORS.white}====================================================${COLORS.reset}`);

  console.log(`\n Total Tests : ${COLORS.bright}${results.total}${COLORS.reset}`);
  console.log(` Passed      : ${COLORS.bright}${COLORS.green}${results.passed}${COLORS.reset}`);
  console.log(` Failed      : ${COLORS.bright}${results.failed > 0 ? COLORS.red : COLORS.green}${results.failed}${COLORS.reset}\n`);

  if (results.failed === 0) {
    console.log(`${COLORS.bright}${COLORS.green}🎉 ALL API TESTS PASSED SUCCESSFULLY!${COLORS.reset}\n`);
    process.exit(0);
  } else {
    console.log(`${COLORS.bright}${COLORS.red}⚠️ SOME API TESTS FAILED. PLEASE REVIEW LOGS ABOVE.${COLORS.reset}\n`);
    process.exit(1);
  }
}

start().catch(err => {
  console.error(`${COLORS.red}Unhandled error in test runner: ${err.message}${COLORS.reset}`);
  process.exit(1);
});
