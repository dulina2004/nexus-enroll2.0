# Design Pattern Index

Quick reference to where each design pattern is implemented in the NexusEnroll codebase.

## Factory Method — Account Creation
- [UserFactory.java](auth-service/src/main/java/com/nexusenroll/auth/factory/UserFactory.java) — Creator
- [StudentFactory.java](auth-service/src/main/java/com/nexusenroll/auth/factory/StudentFactory.java) — Concrete Creator
- [AuthService.java](auth-service/src/main/java/com/nexusenroll/auth/service/AuthService.java) — role-based factory dispatch

## Strategy — Enrollment Validation
- [EnrollmentValidationStrategy.java](enrollment-service/src/main/java/com/nexusenroll/enrollment/strategy/EnrollmentValidationStrategy.java) — Strategy interface
- [PrerequisiteCheckStrategy.java](enrollment-service/src/main/java/com/nexusenroll/enrollment/strategy/PrerequisiteCheckStrategy.java) — Concrete Strategy
- [CapacityCheckStrategy.java](enrollment-service/src/main/java/com/nexusenroll/enrollment/strategy/CapacityCheckStrategy.java) — Concrete Strategy

## Facade — Enrollment Orchestration
- [EnrollmentFacade.java](enrollment-service/src/main/java/com/nexusenroll/enrollment/facade/EnrollmentFacade.java) — unified entry point for enroll, override, and drop

## Observer — Notification Fan-Out
- [NotificationSubject.java](notification-service/src/main/java/com/nexusenroll/notification/model/NotificationSubject.java) — Subject
- [NotificationObserver.java](notification-service/src/main/java/com/nexusenroll/notification/observer/NotificationObserver.java) — Observer interface
- [StudentNotifier.java](notification-service/src/main/java/com/nexusenroll/notification/observer/StudentNotifier.java) — Concrete Observer

## State — Grade Lifecycle
- [GradeState.java](faculty-service/src/main/java/com/nexusenroll/faculty/state/GradeState.java) — State interface
- [DraftState.java](faculty-service/src/main/java/com/nexusenroll/faculty/state/DraftState.java) — Concrete State
- [ApprovedState.java](faculty-service/src/main/java/com/nexusenroll/faculty/state/ApprovedState.java) — Concrete State
- [GradeContext.java](faculty-service/src/main/java/com/nexusenroll/faculty/state/GradeContext.java) — Context
- [GradeService.java](faculty-service/src/main/java/com/nexusenroll/faculty/service/GradeService.java) — batch grade submission

## Builder — Report Assembly
- [ReportBuilder.java](reporting-service/src/main/java/com/nexusenroll/reporting/builder/ReportBuilder.java) — Builder interface
- [EnrollmentReportBuilder.java](reporting-service/src/main/java/com/nexusenroll/reporting/builder/EnrollmentReportBuilder.java) — Concrete Builder
- [Report.java](reporting-service/src/main/java/com/nexusenroll/reporting/model/Report.java) — Product
- [ReportingService.java](reporting-service/src/main/java/com/nexusenroll/reporting/service/ReportingService.java) — Director
